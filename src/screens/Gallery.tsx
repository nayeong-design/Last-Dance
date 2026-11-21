import { useMemo, useState } from 'react';
import { useStore } from '../state/store';
import { IcBack, IcMore, IcSearchLg, IcStarLime, IcTriangleDown } from '../components/icons';
import s from './Gallery.module.css';

type Kind = '1Day 주행기록' | 'Last Dance' | '드라이브 스냅샷' | '스토리 숏폼';
interface Record {
  id: string;
  title: string;
  date: string;
  km: string;
  dur: string;
  img?: string;
  kind: Kind;
  fav?: boolean;
}

const RECORDS: Record[] = [
  { id: 'ld2026', title: '2026 Last Dance', date: '2026.11.15.', km: '14.9km', dur: '2시간 36분', img: 'photo-lastdance', kind: 'Last Dance', fav: true },
  { id: 'jeju', title: '2023 제주도 여행', date: '2023.07.08.', km: '102km', dur: '13시간 27분', img: 'photo-jeju', kind: '1Day 주행기록' },
  { id: 'gukbap', title: '친구들과 국밥투어', date: '2023.07.08.', km: '102km', dur: '13시간 27분', img: 'photo-gukbap', kind: '드라이브 스냅샷' },
  { id: 'gunsan', title: '2023 군산여행', date: '2023.02.24.', km: '24km', dur: '3시간 27분', img: 'photo-gunsan', kind: '1Day 주행기록' },
  { id: 'vlog', title: '시험날 등교길 브이로그', date: '2023.07.08.', km: '102km', dur: '13시간 27분', img: 'photo-vlog', kind: '스토리 숏폼' },
  { id: 'jeju2', title: '2023 제주도 여행', date: '2023.07.08.', km: '102km', dur: '13시간 27분', kind: '1Day 주행기록' },
  { id: 'geumjeong', title: '금정구 외 3곳 방문', date: '2025.06.08.', km: '34km', dur: '2시간 30분', kind: '1Day 주행기록' },
];

const TABS = ['전체', '1Day 주행기록', 'Last Dance', '드라이브 스냅샷', '스토리 숏폼', '즐겨찾기'] as const;

/** Figma 354:2737 — 갤러리 */
export default function Gallery() {
  const { back, go } = useStore();
  const [tab, setTab] = useState<(typeof TABS)[number]>('전체');
  const [q, setQ] = useState('');

  const list = useMemo(
    () =>
      RECORDS.filter((r) => (tab === '전체' ? true : tab === '즐겨찾기' ? r.fav : r.kind === tab)).filter(
        (r) => !q || r.title.includes(q) || r.date.includes(q),
      ),
    [tab, q],
  );

  return (
    <>
      <header className={s.header}>
        <div className={s.headRow}>
          <div className={s.headLeft}>
            <button className="topnav__back" style={{ width: 44, height: 44 }} onClick={back} aria-label="뒤로">
              <IcBack />
            </button>
            <h1 className="t-title-l-xbold">갤러리</h1>
          </div>
          <button aria-label="더보기">
            <IcMore />
          </button>
        </div>
        <nav className={s.tabs} role="tablist">
          {TABS.map((t) => (
            <button
              key={t}
              role="tab"
              aria-selected={tab === t}
              className={`tab ${tab === t ? 't-title-s-xbold' : 't-body-l-bold'}`}
              style={{ color: tab === t ? 'var(--text-primary)' : 'var(--text-disabled)' }}
              onClick={() => setTab(t)}
            >
              {t}
              {tab === t && <span className="tab__bar" />}
            </button>
          ))}
        </nav>
      </header>

      <div className={s.filters}>
        <label className={s.search}>
          <input className="t-body-l-regular" placeholder="날짜나 기록명을 검색해 살펴보세요." value={q} onChange={(e) => setQ(e.target.value)} />
          <IcSearchLg />
        </label>
        <button className={s.sort}>
          <span>기록 전체</span>
          <IcTriangleDown />
        </button>
      </div>

      <div className={s.gridWrap}>
        <div className={s.grid}>
          {list.map((r) => (
            <button key={r.id} className={`${s.card} ${r.img ? '' : s.cardEmpty} pressable`} onClick={() => go('detail')}>
              {r.img && <img src={`assets/${r.img}.png`} alt="" />}
              <span className={s.shade} />
              <span className={s.meta}>
                <span className={s.titleRow}>
                  <span className="t-title-m-bold">{r.title}</span>
                  {r.fav && <IcStarLime />}
                </span>
                <span className={`t-body-s-regular ${s.sub}`}>
                  <span>{r.date}</span>
                  <span>{r.km}</span>
                  <span>{r.dur}</span>
                </span>
              </span>
            </button>
          ))}
          {tab === '전체' && !q && (
            <button className={`${s.add} pressable`} onClick={() => go('home')} aria-label="새 기록 만들기">
              <span />
            </button>
          )}
          {list.length === 0 && <p className={`t-body-l-regular ${s.empty}`}>조건에 맞는 기록이 없어요.</p>}
        </div>
      </div>
    </>
  );
}
