/**
 * Pleos SDK mock layer.
 *
 * Pleos Playground exposes 6 SDKs to in-vehicle apps (Vehicle, Navigation, ADAS,
 * Gleo AI, Vehicle Location, Sync) on Android Automotive OS. The real SDK needs a
 * Pleos Playground developer account, so this web prototype talks to the same
 * shaped interfaces backed by simulated data. When the app is ported to AAOS,
 * swap this file for an adapter over the real SDK and keep the UI unchanged.
 */

export type Gear = 'P' | 'R' | 'N' | 'D';

export interface VehicleInfo {
  nickname: string;
  model: string;
  odometerKm: number;
  rangeKm: number;
  batteryPct: number;
}

export interface RouteStatus {
  currentPlace: string;
  destination: string;
  distanceKm: number;
  nextTurnKm: number;
  nextTurnRoad: string;
  eta: string;
  remainMin: number;
  remainKm: number;
}

export interface Weather {
  label: string;
  tempC: number;
}

type Listener<T> = (v: T) => void;

class Emitter<T> {
  private ls = new Set<Listener<T>>();
  on(l: Listener<T>) {
    this.ls.add(l);
    return () => this.ls.delete(l);
  }
  emit(v: T) {
    this.ls.forEach((l) => l(v));
  }
}

/* ---------- Vehicle SDK ---------- */
export const vehicle = {
  getInfo(): VehicleInfo {
    return { nickname: '푸딩이', model: '2022 IONIQ 5', odometerKm: 10592, rangeKm: 287, batteryPct: 80 };
  },
  gear: new Emitter<Gear>(),
  setGear(g: Gear) {
    this.gear.emit(g);
  },
};

/* ---------- Navigation + Vehicle Location SDK ---------- */
const PLACES = ['용산구청 지나는 중', '반포대교 지나는 중', '반포대교 지나는 중', '신사역 지나는 중'];
export const navigation = {
  status: new Emitter<RouteStatus>(),
  /** Simulated progress 0..1 along the Last Dance route. */
  routeAt(progress: number): RouteStatus {
    const idx = Math.min(PLACES.length - 1, Math.floor(progress * PLACES.length));
    return {
      currentPlace: PLACES[idx],
      destination: '조선팰리스 강남',
      distanceKm: Math.round((5.2 + progress * 9.7) * 10) / 10,
      nextTurnKm: 1.5,
      nextTurnRoad: '반포대로 방면',
      eta: '2:06 PM',
      remainMin: 6,
      remainKm: 2.1,
    };
  },
};

/* ---------- Weather (Sync SDK / cloud) ---------- */
export const weather = {
  now(): Weather {
    return { label: '맑고 구름 조금', tempC: 17 };
  },
};

/* ---------- ADAS SDK: V2V social signal ---------- */
export type Reaction = 'fist' | 'thumb' | 'heart' | 'party' | 'clap';
export const adas = {
  reactions: new Emitter<Reaction>(),
  /** Broadcast the "last drive" message to surrounding vehicles. Simulates replies. */
  broadcastFarewell(): () => void {
    const seq: Reaction[] = ['fist', 'thumb', 'clap', 'heart', 'party'];
    const timers = seq.map((r, i) => window.setTimeout(() => this.reactions.emit(r), 1400 + i * 900));
    return () => timers.forEach(clearTimeout);
  },
};

/* ---------- Gleo AI SDK ---------- */
export const gleo = {
  ask(prompt: string): Promise<string> {
    const answers: Record<string, string> = {
      default: '오늘은 푸딩이와의 마지막 드라이브예요. 반포대교 야경이 가장 예쁜 시간까지 18분 남았어요.',
    };
    return new Promise((res) => setTimeout(() => res(answers[prompt] ?? answers.default), 700));
  },
};

/* ---------- Camera (in-cabin) ---------- */
export type CameraId = 'left' | 'front-center' | 'right';
export const camera = {
  label(id: CameraId) {
    return { left: '좌측', 'front-center': '앞 중앙 카메라', right: '우측' }[id];
  },
};
