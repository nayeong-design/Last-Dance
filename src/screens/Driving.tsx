import { useEffect, useRef, useState } from 'react';
import { fmtTime, useStore } from '../state/store';
import { adas, camera as cam, gleo, navigation, weather, type CameraId, type Reaction } from '../pleos/sdk';
import { MiniPlayer } from '../components/ui';
import {
  GuideCorners, GuideCross, IcBack, IcClose, IcExchange, IcGleo, IcKnob, IcSearch, IcSnapshotWhite, IcStar, IcTurnRight, RecDot,
} from '../components/icons';
import s from './Driving.module.css';

/* Emoji reaction tiles received from nearby vehicles (Figma Component 20). Stage coords. */
const REACTION_TILES: Record<Reaction, { x: number; y: number; w: number; h: number; img: string; iw: number; ih: number }> = {
  fist: { x: 103, y: 810, w: 119, h: 96, img: 'emoji-fist', iw: 66, ih: 68 },
  thumb: { x: 267, y: 343, w: 90, h: 73, img: 'emoji-thumb', iw: 53, ih: 54 },
  heart: { x: 488, y: 138, w: 81, h: 65, img: 'emoji-heart', iw: 45, ih: 47 },
  party: { x: 712, y: 962, w: 119, h: 96, img: 'emoji-party', iw: 71, ih: 65 },
  clap: { x: 395, y: 419, w: 92, h: 74, img: 'emoji-clap', iw: 50, ih: 54 },
};

/** Navigation app (left split). Owned by Pleos Navigation; rendered here as the host context. */
function NavPanel() {
  const r = navigation.routeAt(0.5);
  return (
    <div className={s.nav}>
      <img className={s.navRoad} src="assets/nav-road.png" alt="" />
      <div className={s.turn}>
        <IcTurnRight />
        <span className="t-headline-s-xbold">{r.nextTurnKm}km</span>
        <span className="t-body-m-semibold" style={{ color: 'var(--text-disabled)' }}>
          {r.nextTurnRoad}
        </span>
      </div>
      <img className={s.navCar} src="assets/nav-car.png" alt="" />
      <div className={s.navBar}>
        <span className={s.navSearch}>
          <IcSearch />
        </span>
        <div className={s.navInfo}>
          <div className={s.navMeta}>
            <span className="t-label-m-bold">{r.eta}</span>
            <span className={`t-label-m-xbold ${s.dim}`}>ㆍ</span>
            <span className={`t-label-m-medium ${s.dim}`}>{r.remainMin}분</span>
            <span className={`t-label-m-xbold ${s.dim}`}>ㆍ</span>
            <span className={`t-label-m-medium ${s.dim}`}>{r.remainKm}km</span>
            <span className={`t-label-m-xbold ${s.dim}`}>ㆍ</span>
            <span className={s.battery} aria-hidden>
              <i />
            </span>
            <span className={`t-label-m-medium ${s.dim}`}>80%</span>
          </div>
          <div className={s.traffic}>
            <i style={{ left: -8, width: 336, background: 'var(--border-strong)' }} />
            <i style={{ left: 328, width: 77, background: 'var(--traffic-free)' }} />
            <i style={{ left: 397, width: 14, background: 'var(--traffic-slow)', height: 7 }} />
            <i style={{ left: 405, width: 24, background: 'var(--traffic-jam)' }} />
            <i style={{ left: 425, width: 48, background: 'var(--traffic-free)' }} />
            <i style={{ left: 429, width: 14, background: 'var(--traffic-slow)', height: 7 }} />
          </div>
          <IcKnob className={s.knob} />
        </div>
        <button className={`${s.navEnd} t-label-m-xbold pressable`}>안내종료</button>
      </div>
    </div>
  );
}

function RecordPanel({ onSnapshot, onToast }: { onSnapshot: () => void; onToast: (m: string) => void }) {
  const { s: st, dispatch, go, back } = useStore();
  const rec = st.recording;
  const route = navigation.routeAt(Math.min(1, rec.elapsed / 600));
  const w = weather.now();
  return (
    <aside className={s.panel}>
      <div className={s.panelHead}>
        <button className="topnav__back" onClick={back} aria-label="뒤로">
          <IcBack />
        </button>
        <h1 className="t-title-l-bold">Last Dance 주행기록</h1>
        <span style={{ width: 111 }} />
      </div>
      <div className={s.panelBody}>
        <section className={s.record}>
          <div className={s.recordInner}>
            <div className={s.recStatus}>
              <span className="t-body-l-bold">{rec.paused ? '일시정지' : '기록 중'}</span>
              <RecDot />
              <span className="t-body-l-bold" style={{ fontVariantNumeric: 'tabular-nums' }}>
                {fmtTime(rec.elapsed)}
              </span>
            </div>
            <dl className={s.stats}>
              <div style={{ width: 191 }}>
                <dt className="t-label-s-regular">현재 지점</dt>
                <dd className="t-body-s-semibold">{route.currentPlace}</dd>
              </div>
              <div style={{ width: 87 }}>
                <dt className="t-label-s-regular">이동 거리</dt>
                <dd className="t-body-s-semibold">{route.distanceKm}km</dd>
              </div>
              <div>
                <dt className="t-label-s-regular">기록</dt>
                <dd className="t-body-s-semibold">스냅샷 {rec.snapshots}</dd>
              </div>
              <div style={{ width: 191 }}>
                <dt className="t-label-s-regular">목적지</dt>
                <dd className="t-body-s-semibold">{route.destination}</dd>
              </div>
              <div>
                <dt className="t-label-s-regular">날씨</dt>
                <dd className="t-body-s-semibold">{w.label}</dd>
              </div>
            </dl>
          </div>
          <div className={s.recButtons}>
            <button className={`${s.recBtn} t-body-l-bold pressable`} onClick={() => dispatch({ type: 'pause' })}>
              {rec.paused ? '기록 재개' : '기록 중지'}
            </button>
            <button
              className={`${s.recBtn} ${s.recEnd} t-body-l-bold pressable`}
              onClick={() => {
                dispatch({ type: 'stopRecording' });
                go('detail');
              }}
            >
              기록 종료
            </button>
          </div>
        </section>
        <div className={s.tools}>
          <div className={s.tiles}>
            <button className={`${s.tile} pressable`} style={{ width: 200 }} onClick={async () => onToast(await gleo.ask('default'))}>
              <IcGleo />
              <span className="t-title-s-bold">Gleo AI</span>
            </button>
            <button className={`${s.tile} pressable`} style={{ width: 328 }} onClick={onSnapshot}>
              <IcSnapshotWhite />
              <span className="t-title-s-bold">드라이브 스냅샷</span>
            </button>
            <button className={`${s.thumb} pressable`} onClick={() => go('gallery')} aria-label="갤러리 열기">
              <img src="assets/snapshot-thumb.png" alt="" />
            </button>
          </div>
          <MiniPlayer style={{ width: 760, height: 236 }} />
        </div>
      </div>
    </aside>
  );
}

function CameraPanel({ onDone }: { onDone: () => void }) {
  const { s: st, dispatch } = useStore();
  const [count, setCount] = useState(3);
  const [flash, setFlash] = useState(false);
  const ids: CameraId[] = ['left', 'front-center', 'right'];

  // 녹화 타이머가 매초 리렌더를 일으키므로 onDone은 ref로 고정하고 count에만 반응한다.
  const done = useRef(onDone);
  done.current = onDone;
  useEffect(() => {
    if (count === 0) {
      setFlash(true);
      dispatch({ type: 'snapshot' });
      const t = setTimeout(() => done.current(), 650);
      return () => clearTimeout(t);
    }
    const t = setTimeout(() => setCount((c) => c - 1), 1000);
    return () => clearTimeout(t);
  }, [count, dispatch]);

  return (
    <div className={s.camera}>
      <div className={s.cameraBase} />
      <div className={s.preview}>
        <img src="assets/camera-preview.png" alt="실내 카메라 미리보기" />
        <GuideCorners className={s.guideTop} />
        <GuideCorners className={s.guideBottom} />
        <GuideCross className={s.guideCross} />
        {count > 0 && (
          <div className={s.countdown}>
            <p className="t-title-m-bold">{count}초 후 촬영</p>
            <div className={s.ring}>
              <svg width="166" height="166" viewBox="0 0 166 166">
                <circle cx="83" cy="83" r="75" stroke="color-mix(in srgb, var(--accent) 20%, transparent)" strokeWidth="8" fill="none" />
                <circle
                  key={count}
                  className={s.ringArc}
                  cx="83" cy="83" r="75" stroke="var(--accent)" strokeWidth="8" fill="none" strokeLinecap="round"
                  strokeDasharray={2 * Math.PI * 75}
                />
              </svg>
              <span className="t-headline-l-bold" style={{ color: 'var(--accent)' }}>
                {count}
              </span>
            </div>
          </div>
        )}
        {flash && <div className={s.flash} />}
      </div>
      <div className={s.camSelect}>
        {ids.map((id) =>
          id === st.cameraId ? (
            <span key={id} className={`${s.camActive} t-body-l-bold`}>
              {cam.label(id)}
              <IcExchange />
            </span>
          ) : (
            <button key={id} className={`${s.camSide} t-body-l-bold pressable`} onClick={() => dispatch({ type: 'camera', id })}>
              {cam.label(id)}
            </button>
          ),
        )}
      </div>
      <span className={s.camThumb}>
        <img src="assets/snapshot-thumb.png" alt="" />
      </span>
      <button className={s.shutter} onClick={() => setCount(0)} aria-label="지금 촬영">
        <i />
      </button>
      <button className={`${s.camClose} pressable`} onClick={onDone} aria-label="닫기">
        <IcClose width={37} height={37} />
      </button>
    </div>
  );
}

/** Figma 354:3662 / 3073 / 3221 / 3360 / 3511 (주행 기록) and 354:2884 (스냅샷). Stage-level split view. */
export default function Driving() {
  const { s: st, dispatch, go, back } = useStore();
  const [toast, setToast] = useState<string | null>(null);
  const toastTimer = useRef<number>(0);
  const showToast = (m: string) => {
    setToast(m);
    clearTimeout(toastTimer.current);
    toastTimer.current = window.setTimeout(() => setToast(null), 3200);
  };
  const cameraOpen = st.screen === 'camera';

  useEffect(() => {
    if (!st.notifying) return;
    const off = adas.reactions.on((r) => dispatch({ type: 'reaction', r }));
    const stop = adas.broadcastFarewell();
    return () => {
      off();
      stop();
    };
  }, [st.notifying, dispatch]);

  return (
    <>
      <div className={s.cover} />
      <div className={s.window} />
      <NavPanel />
      <RecordPanel onSnapshot={() => go('camera')} onToast={showToast} />
      {cameraOpen && <CameraPanel onDone={back} />}

      {/* ADAS overlay on the driving-information area (left). */}
      {st.notifying && (
        <>
          <div className={s.carGlow} aria-hidden>
            <i style={{ left: 93, top: 191, width: 86, height: 86 }} />
            <i style={{ left: 243, top: 97, width: 134, height: 137 }} />
            <i style={{ left: 277, top: 243, width: 67, height: 68 }} />
          </div>
          <div className={`${s.popup} t-body-l-bold`} role="status">
            오늘 이 차와의 마지막이에요 ㅜㅜ
          </div>
          {st.reactions.map((r) => {
            const t = REACTION_TILES[r];
            return (
              <span key={r} className={s.reaction} style={{ left: t.x, top: t.y, width: t.w, height: t.h }}>
                <img src={`assets/${t.img}.png`} alt="" width={t.iw} height={t.ih} />
              </span>
            );
          })}
        </>
      )}
      <button className={`${s.notify} t-label-s-medium pressable`} onClick={() => dispatch({ type: 'notify', on: !st.notifying })}>
        {st.notifying ? (
          '알리기 취소'
        ) : (
          <>
            <IcStar width={34} height={34} />
            라스트댄스 알리기
          </>
        )}
      </button>
      {toast && (
        <div className="toast t-body-m-semibold" style={{ left: 2160, bottom: 200, width: 720, whiteSpace: 'normal', textAlign: 'center' }}>
          {toast}
        </div>
      )}
    </>
  );
}
