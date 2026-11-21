import { useState } from 'react';
import { useStore } from '../state/store';
import { Toast, TopNav } from '../components/ui';
import { IcMoon, IcMuseum, IcPencil, IcPin, IcRecap, IcSun } from '../components/icons';
import s from './Detail.module.css';

const STOPS = [
  { icon: <IcPin />, label: '출발지', time: '18:09', name: '우리집', top: 120 },
  { icon: <IcMuseum />, label: '경유지1', time: '18:09-20:12', name: '국립중앙박물관', top: 297.5 },
  { icon: <IcPin />, label: '도착지', time: '20:12-20:45', name: '조선팰리스 강남', top: 475 },
];

/** Figma 354:2591 — 2026 Last Dance 기록 상세 */
export default function Detail() {
  const { s: st, back } = useStore();
  const [toast, setToast] = useState<string | null>(null);
  const snaps = st.recording.snapshots > 0 ? st.recording.snapshots : 4;
  const flash = (m: string) => {
    setToast(m);
    setTimeout(() => setToast(null), 2200);
  };

  return (
    <>
      <TopNav
        title="2026 Last Dance"
        subtitle="2026.11.15 (일) 오후 8:46"
        onBack={back}
        slot={
          <button className={s.edit} onClick={() => flash('기록 이름을 편집할 수 있어요')} aria-label="편집">
            <IcPencil />
          </button>
        }
      />

      <section className={s.route}>
        <div className={s.stops}>
          <h2 className="t-title-s-bold">Last Dance 경로</h2>
          <span className={s.scroll} aria-hidden />
          <span className={s.dash} aria-hidden />
          <span className={`t-label-s-regular ${s.dist}`} style={{ top: 222 }}>
            6.5km
          </span>
          <span className={`t-label-s-regular ${s.dist}`} style={{ top: 401 }}>
            8.4km
          </span>
          {STOPS.map((p) => (
            <div key={p.label} className={s.stop} style={{ top: p.top }}>
              {p.icon}
              <div className={s.stopText}>
                <span className="t-caption-medium">
                  {p.label} <span style={{ color: 'var(--text-disabled)', marginLeft: 12 }}>{p.time}</span>
                </span>
                <span className="t-body-s-bold">{p.name}</span>
              </div>
            </div>
          ))}
        </div>
        <div className={s.map}>
          <img src="assets/route-map.png" alt="Last Dance 주행 경로 지도: 우리집에서 국립중앙박물관을 거쳐 조선팰리스 강남까지" />
          <div className={`${s.weather} t-label-m-regular`}>
            <span>
              <IcSun /> 맑음 17℃
            </span>
            <span>
              <IcMoon /> 야간
            </span>
          </div>
        </div>
      </section>

      <div className={s.info}>
        <section className={s.block} style={{ width: 774 }}>
          <h2 className="t-title-s-bold">주행 정보</h2>
          <dl className={s.stats}>
            {[
              ['이동 거리', '14.9km'],
              ['평균 시속', '18km'],
              ['평균 전비', '5.8km/kw'],
              ['컨텐츠 기록', `스냅샷 ${snaps}건`],
            ].map(([k, v]) => (
              <div key={k}>
                <dt className="t-label-m-regular">{k}</dt>
                <dd className="t-body-s-semibold">{v}</dd>
              </div>
            ))}
          </dl>
        </section>
        <section className={s.block}>
          <h2 className="t-title-s-bold">플레이리스트</h2>
          <div className={s.tracks}>
            <img src="assets/track-1.png" alt="12월 (Hate December) · l'll" />
            <img src="assets/track-2.png" alt="Hollywood · 검정치마" />
            <span className="t-body-s-regular">외 10건</span>
          </div>
        </section>
      </div>

      <button className={`${s.recap} pressable`} onClick={() => flash('리캡 영상을 만드는 중이에요')}>
        <IcRecap />
        <span className={s.recapText}>
          <span className={s.recapRow}>
            <span className="t-title-m-xbold" style={{ color: 'var(--text-accent)' }}>
              리캡 다시보기
            </span>
          </span>
          <span className="t-body-m-bold">마지막 주행 다시 경험하기</span>
        </span>
      </button>
      <span className={s.pageScroll} aria-hidden />
      {toast && <Toast>{toast}</Toast>}
    </>
  );
}
