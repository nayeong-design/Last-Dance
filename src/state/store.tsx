import { createContext, useCallback, useContext, useEffect, useMemo, useReducer, type ReactNode } from 'react';
import type { CameraId, Reaction } from '../pleos/sdk';

export type Screen =
  | 'home' // 1Day 주행기록
  | 'setup' // Last Dance 등록
  | 'plan' // 등록 완료 / 이별 프로세스
  | 'checklist' // 코스 선택 모달
  | 'driving' // 주행 기록 중
  | 'camera' // 드라이브 스냅샷
  | 'gallery'
  | 'detail';

export interface ChecklistItem {
  id: string;
  title: string;
  place: string;
}

export const CHECKLIST: ChecklistItem[] = [
  { id: 'wash', title: '마지막 세차', place: '베스트 세차장' },
  { id: 'charge', title: '마지막 충전', place: '그린파워 충전소' },
  { id: 'dt', title: '드라이브 스루 즐기기', place: '모닝브루 DT' },
  { id: 'cafe', title: '단골 카페 방문하기', place: '카페 이오' },
  { id: 'course', title: '좋아하는 드라이브 코스 달리기', place: '명암 저수지' },
];

export const REASONS = ['중고 차량 판매', '신차 구매', '폐차', '가족에게 양도', '직접입력'];

export interface State {
  screen: Screen;
  history: Screen[];
  farewell: { year?: number; month?: number; day?: number; reason: string; memo: string; registered: boolean };
  selected: string[];
  recording: { active: boolean; paused: boolean; elapsed: number; snapshots: number };
  notifying: boolean;
  reactions: Reaction[];
  cameraId: CameraId;
}

const initial: State = {
  screen: 'home',
  history: [],
  farewell: { reason: '직접입력', memo: '', registered: false },
  selected: ['wash'],
  recording: { active: false, paused: false, elapsed: 0, snapshots: 0 },
  notifying: false,
  reactions: [],
  cameraId: 'front-center',
};

type Action =
  | { type: 'go'; to: Screen }
  | { type: 'back' }
  | { type: 'farewell'; patch: Partial<State['farewell']> }
  | { type: 'toggle'; id: string }
  | { type: 'startRecording' }
  | { type: 'tick' }
  | { type: 'pause' }
  | { type: 'stopRecording' }
  | { type: 'snapshot' }
  | { type: 'notify'; on: boolean }
  | { type: 'reaction'; r: Reaction }
  | { type: 'camera'; id: CameraId };

function reducer(s: State, a: Action): State {
  switch (a.type) {
    case 'go':
      return s.screen === a.to ? s : { ...s, screen: a.to, history: [...s.history, s.screen] };
    case 'back': {
      const h = [...s.history];
      const prev = h.pop() ?? 'home';
      return { ...s, screen: prev, history: h };
    }
    case 'farewell':
      return { ...s, farewell: { ...s.farewell, ...a.patch } };
    case 'toggle':
      return { ...s, selected: s.selected.includes(a.id) ? s.selected.filter((x) => x !== a.id) : [...s.selected, a.id] };
    case 'startRecording':
      return { ...s, recording: { active: true, paused: false, elapsed: 0, snapshots: 0 }, reactions: [], notifying: false };
    case 'tick':
      return s.recording.active && !s.recording.paused ? { ...s, recording: { ...s.recording, elapsed: s.recording.elapsed + 1 } } : s;
    case 'pause':
      return { ...s, recording: { ...s.recording, paused: !s.recording.paused } };
    case 'stopRecording':
      return { ...s, recording: { ...s.recording, active: false }, notifying: false };
    case 'snapshot':
      return { ...s, recording: { ...s.recording, snapshots: s.recording.snapshots + 1 } };
    case 'notify':
      return { ...s, notifying: a.on, reactions: a.on ? s.reactions : [] };
    case 'reaction':
      return s.reactions.includes(a.r) ? s : { ...s, reactions: [...s.reactions, a.r] };
    case 'camera':
      return { ...s, cameraId: a.id };
  }
}

interface Ctx {
  s: State;
  go: (to: Screen) => void;
  back: () => void;
  dispatch: React.Dispatch<Action>;
}

const StoreCtx = createContext<Ctx | null>(null);

export function StoreProvider({ children }: { children: ReactNode }) {
  const [s, dispatch] = useReducer(reducer, initial);
  const go = useCallback((to: Screen) => dispatch({ type: 'go', to }), []);
  const back = useCallback(() => dispatch({ type: 'back' }), []);

  useEffect(() => {
    if (!s.recording.active) return;
    const t = window.setInterval(() => dispatch({ type: 'tick' }), 1000);
    return () => clearInterval(t);
  }, [s.recording.active]);

  const value = useMemo(() => ({ s, go, back, dispatch }), [s, go, back]);
  return <StoreCtx.Provider value={value}>{children}</StoreCtx.Provider>;
}

export function useStore() {
  const c = useContext(StoreCtx);
  if (!c) throw new Error('useStore outside provider');
  return c;
}

export const fmtTime = (sec: number) =>
  [Math.floor(sec / 3600), Math.floor((sec % 3600) / 60), sec % 60].map((n) => String(n).padStart(2, '0')).join(':');
