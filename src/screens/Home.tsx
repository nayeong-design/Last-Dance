import { useStore } from '../state/store';
import { CircleArrow, MiniPlayer, TopNav } from '../components/ui';
import { IcCamera, IcHeadphones, IcMapPin, IcSnapshot } from '../components/icons';
import s from './Home.module.css';

const FEATURES = [
  { icon: <IcMapPin />, label: ['주행경로'] },
  { icon: <IcHeadphones />, label: ['재생 음악 리스트'] },
  { icon: <IcSnapshot size={40} />, label: ['분위기 감지', '드라이브 스냅샷'] },
  { icon: <IcCamera />, label: ['실시간 차량', '내/외부 기록'] },
];

/** Figma 354:2378 — 1Day 주행기록 */
export default function Home() {
  const { go, dispatch } = useStore();
  return (
    <>
      <TopNav title="1Day 주행기록" onBack={() => {}} />

      <section className={s.today}>
        <div className={s.todayHead}>
          <h2 className="t-title-l-bold">오늘의 드라이브 시작하기</h2>
          <p className="t-title-s-regular" style={{ color: 'var(--text-secondary)' }}>
            특별한 오늘의 드라이브 순간을 저장해보세요
          </p>
        </div>
        <span className={s.todayArrow}>
          <CircleArrow label="오늘의 드라이브 시작하기" onClick={() => {
            dispatch({ type: 'startRecording' });
            go('driving');
          }} />
        </span>
        <ul className={s.features}>
          {FEATURES.map((f, i) => (
            <li key={i} className={s.feature}>
              {f.icon}
              <span className="t-label-m-regular">
                {f.label.map((l) => (
                  <span key={l} style={{ display: 'block' }}>
                    {l}
                  </span>
                ))}
              </span>
            </li>
          ))}
        </ul>
      </section>

      <button className={`${s.log} pressable`} onClick={() => go('gallery')}>
        <span className="t-title-l-bold">주행로그</span>
        <CircleArrow />
      </button>

      <button className={`${s.lastDance} pressable`} onClick={() => go('setup')}>
        <span className={s.lastDanceGlow} aria-hidden />
        <span className={s.lastDanceRow}>
          <span className="t-title-l-bold">Last Dance</span>
          <CircleArrow />
        </span>
      </button>

      <MiniPlayer wide style={{ position: 'absolute', left: 39, top: 907, width: 1620, height: 236 }} />
    </>
  );
}
