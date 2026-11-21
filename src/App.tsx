import { useEffect, useState } from 'react';
import { StoreProvider, useStore, type Screen } from './state/store';
import Home from './screens/Home';
import { FarewellPlan, FarewellSetup } from './screens/Farewell';
import Checklist from './screens/Checklist';
import Driving from './screens/Driving';
import Gallery from './screens/Gallery';
import Detail from './screens/Detail';
import DesignPanel from './design-panel/DesignPanel';

const WINDOW_VARIANT: Partial<Record<Screen, 'base' | 'surface'>> = {
  home: 'base',
  checklist: 'base',
  setup: 'surface',
  plan: 'surface',
  gallery: 'surface',
  detail: 'surface',
};

const SCREENS: Partial<Record<Screen, () => JSX.Element>> = {
  home: Home,
  setup: FarewellSetup,
  plan: FarewellPlan,
  checklist: Checklist,
  gallery: Gallery,
  detail: Detail,
};

const DEV_LINKS: [Screen, string][] = [
  ['home', '홈'],
  ['setup', '이별 설정'],
  ['plan', '이별 플랜'],
  ['checklist', '체크리스트'],
  ['driving', '주행 기록'],
  ['camera', '스냅샷'],
  ['gallery', '갤러리'],
  ['detail', '기록 상세'],
];

const DEVNAV_H = 44;
const PANEL_W = 360;

/** 2600×1480 IVI 스테이지를 뷰포트에 맞춰 스케일 */
function useStageScale(panelOpen: boolean) {
  const calc = () => Math.min((window.innerWidth - (panelOpen ? PANEL_W : 0)) / 2600, (window.innerHeight - DEVNAV_H) / 1480);
  const [scale, setScale] = useState(calc);
  useEffect(() => {
    const on = () => setScale(calc());
    on();
    window.addEventListener('resize', on);
    return () => window.removeEventListener('resize', on);
  }, [panelOpen]); // eslint-disable-line react-hooks/exhaustive-deps
  return scale;
}

function usePanelOpen() {
  const [open, setOpen] = useState(() => {
    try {
      return localStorage.getItem('last-dance:panel-open') === '1';
    } catch {
      return false;
    }
  });
  const set = (v: boolean) => {
    setOpen(v);
    try {
      localStorage.setItem('last-dance:panel-open', v ? '1' : '0');
    } catch {
      /* ignore */
    }
  };
  return [open, set] as const;
}

function Stage() {
  const { s, go } = useStore();
  const [panelOpen, setPanelOpen] = usePanelOpen();
  const scale = useStageScale(panelOpen);
  const driving = s.screen === 'driving' || s.screen === 'camera';
  const Screen = SCREENS[s.screen];
  const variant = WINDOW_VARIANT[s.screen] ?? 'base';

  return (
    <>
      <div className="viewport" style={{ right: panelOpen ? PANEL_W : 0 }}>
        <div className="stage" style={{ transform: `translate(-50%, -50%) scale(${scale})` }}>
          <img className="stage__bg" src={driving ? 'assets/bg-driving.png' : 'assets/bg-parked.png'} alt="" />
          {driving ? (
            <Driving />
          ) : (
            Screen && (
              <main key={s.screen} className={`app-window app-window--${variant} screen-enter`}>
                <Screen />
              </main>
            )
          )}
        </div>
      </div>
      <nav className="devnav" aria-label="화면 바로가기 (개발용)">
        {DEV_LINKS.map(([id, label]) => (
          <button key={id} aria-pressed={s.screen === id} onClick={() => go(id)}>
            {label}
          </button>
        ))}
      </nav>
      <DesignPanel open={panelOpen} onOpenChange={setPanelOpen} />
    </>
  );
}

export default function App() {
  return (
    <StoreProvider>
      <Stage />
    </StoreProvider>
  );
}
