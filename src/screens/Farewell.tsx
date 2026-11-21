import { useState } from 'react';
import { CHECKLIST, REASONS, useStore } from '../state/store';
import { vehicle } from '../pleos/sdk';
import { PrimaryButton, Toast, TopNav } from '../components/ui';
import { DotActive, DotIdle, IcChat, IcSnapshot, IcSunset } from '../components/icons';
import { Dropdown } from '../components/Dropdown';
import s from './Farewell.module.css';

function Heading() {
  return (
    <div className={s.heading}>
      <h2 className="t-headline-s-bold">
        이주영 님을 위한
        <br />
        차량 이별 프로세스 추천
      </h2>
      <p className="t-body-m-regular" style={{ color: 'var(--text-secondary)' }}>
        나만의 차량 이별 여정을 기록으로 남겨보세요.
      </p>
    </div>
  );
}

/** Right column shared by setup and plan screens (Figma 354:2305 / 354:2514). */
function Actions({ onStart, onToast }: { onStart: () => void; onToast: (m: string) => void }) {
  return (
    <div className={s.actions}>
      <button className={`card card--lime-glow pressable ${s.startCard}`} onClick={onStart}>
        <span className={s.startGlow} aria-hidden />
        <span className="t-title-l-bold">Last Dance 주행 시작</span>
        <IcSunset />
      </button>
      <div className={s.actionRow}>
        <button className={`card pressable ${s.smallCard}`} onClick={() => onToast('차량 초기화는 이별 당일에 진행할 수 있어요')}>
          <span className="t-title-l-bold">차량 초기화</span>
          <IcSnapshot size={90} />
        </button>
        <button className={`card pressable ${s.smallCard}`} onClick={() => onToast('다음 오너에게 남길 메시지 카드를 준비 중이에요')}>
          <span className="t-title-l-bold">이별 메시지 카드</span>
          <IcChat />
        </button>
      </div>
    </div>
  );
}

function useToast() {
  const [msg, setMsg] = useState<string | null>(null);
  const show = (m: string) => {
    setMsg(m);
    window.setTimeout(() => setMsg(null), 2200);
  };
  return { msg, show };
}

const range = (a: number, b: number) => Array.from({ length: b - a + 1 }, (_, i) => a + i);

/** Figma 354:2290 — 차량 교체 일자 / 이별 사유 등록 */
export function FarewellSetup() {
  const { s: st, back, go, dispatch } = useStore();
  const f = st.farewell;
  const v = vehicle.getInfo();
  const t = useToast();
  const ready = !!(f.year && f.month && f.day) && (f.reason !== '직접입력' || f.memo.trim().length > 0);

  const sel = (key: 'year' | 'month' | 'day', label: string, unit: string, opts: number[]) => (
    <Dropdown
      className={s.dateSelect}
      label={label}
      placeholder={label}
      value={f[key]}
      options={opts.map((o) => ({ value: o, label: `${o}${unit}` }))}
      onChange={(v) => dispatch({ type: 'farewell', patch: { [key]: v } })}
    />
  );

  return (
    <>
      <TopNav title="Last Dance" onBack={back} />
      <Heading />
      <p className={`t-body-m-bold ${s.carLine}`}>
        <span>{v.nickname}</span>
        <span>|</span>
        <span>{v.model}</span>
      </p>

      <form className={s.form} onSubmit={(e) => e.preventDefault()}>
        <div className={s.group} style={{ width: 548 }}>
          <span className="pill-label t-body-l-bold">차량 교체 일자</span>
          <div className={s.row}>
            {sel('year', '년도', '년', range(2026, 2028))}
            {sel('month', '월', '월', range(1, 12))}
            {sel('day', '일', '일', range(1, 31))}
          </div>
        </div>
        <div className={s.group}>
          <span className="pill-label t-body-l-bold">차량 이별 사유</span>
          <Dropdown
            style={{ width: '100%' }}
            label="차량 이별 사유"
            placeholder="사유 선택"
            value={f.reason}
            options={REASONS.map((r) => ({ value: r, label: r }))}
            onChange={(v) => dispatch({ type: 'farewell', patch: { reason: v } })}
          />
          <input
            className="input t-body-l-regular"
            placeholder="차량 이별 사유를 작성하세요!"
            value={f.memo}
            onChange={(e) => dispatch({ type: 'farewell', patch: { memo: e.target.value } })}
          />
        </div>
      </form>

      <PrimaryButton
        style={{ position: 'absolute', left: 67, top: 1015, width: 676 }}
        onClick={() => {
          if (!ready) return t.show('교체 일자와 이별 사유를 모두 입력해주세요');
          dispatch({ type: 'farewell', patch: { registered: true } });
          go('plan');
        }}
      >
        등록하기
      </PrimaryButton>

      <Actions onStart={() => t.show('먼저 차량 교체 일자를 등록해주세요')} onToast={t.show} />
      {t.msg && <Toast>{t.msg}</Toast>}
    </>
  );
}

/** Figma 354:2455 — 등록 완료, 이별 프로세스 타임라인 */
export function FarewellPlan() {
  const { s: st, back, go } = useStore();
  const f = st.farewell;
  const v = vehicle.getInfo();
  const t = useToast();
  const date = f.year ? `${f.year}-${String(f.month).padStart(2, '0')}-${String(f.day).padStart(2, '0')}` : '2026-11-16';
  const reason = f.reason === '직접입력' ? f.memo || '중고 차량 판매' : f.reason;

  return (
    <>
      <TopNav title="Last Dance" onBack={back} />
      <div className={s.planLeft}>
        <Heading />
        <ol className={s.timeline}>
          <span className={s.timelineLine} aria-hidden />
          {CHECKLIST.map((c, i) => (
            <li key={c.id} className={s.step}>
              <span className={s.stepDot}>
                {i === 0 ? <DotActive /> : <DotIdle />}
                {i === 0 && <span className={s.stepActiveLine} aria-hidden />}
              </span>
              <span className={s.stepText}>
                <span className="t-title-m-semibold" style={{ color: 'var(--p-white)' }}>
                  {c.title}
                </span>
                <span className="t-body-s-regular" style={{ color: 'var(--text-secondary)' }}>
                  {c.place}
                </span>
              </span>
            </li>
          ))}
        </ol>
      </div>

      <p className={`t-body-s-bold ${s.model}`}>{v.model}</p>
      <div className={`${s.summary} t-body-s-regular`}>
        <span style={{ color: 'var(--text-secondary)' }}>차량 교체 일자</span>
        <span>{date}</span>
        <span className={s.vline} />
        <span style={{ color: 'var(--text-secondary)' }}>차량 이별 사유</span>
        <span>{reason}</span>
      </div>

      <Actions onStart={() => go('checklist')} onToast={t.show} />
      {t.msg && <Toast>{t.msg}</Toast>}
    </>
  );
}
