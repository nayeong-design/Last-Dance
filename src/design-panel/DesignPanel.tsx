import { useState } from 'react';
import { useStore } from '../state/store';
import { ACCENT_PRESETS, COLOR_TOKENS, FONTS, SCALE_TOKENS } from './tokens-config';
import { useDesignOverrides } from './useDesignOverrides';
import { useCopyEditing } from './useCopyEditing';
import { buildRequest, changeCount } from './exportRequest';
import s from './DesignPanel.module.css';

const HEX = /^#[0-9a-f]{6}$/i;

/**
 * 디자인 패널 — 코드를 몰라도 화면을 보면서 토큰(색 · 둥글기 · 글자)과 문구를 바꿔 볼 수 있다.
 * 바꾼 값은 이 브라우저에만 저장되고, "변경 요청서 복사"로 Claude에게 전달해 실제 코드에 반영한다.
 */
export default function DesignPanel({ open, onOpenChange }: { open: boolean; onOpenChange: (v: boolean) => void }) {
  const { s: st } = useStore();
  const { o, setColor, setScale, setFont, upsertCopy, removeCopy, reset } = useDesignOverrides();
  const [editing, setEditing] = useState(false);
  const [copied, setCopied] = useState<'ok' | 'fail' | null>(null);
  const [confirmReset, setConfirmReset] = useState(false);
  const request = buildRequest(o);
  const count = changeCount(o);

  useCopyEditing({ editing: open && editing, edits: o.copy, screen: st.screen, onEdit: upsertCopy });

  const copy = async () => {
    try {
      await navigator.clipboard.writeText(request);
      setCopied('ok');
    } catch {
      setCopied('fail');
    }
    setTimeout(() => setCopied(null), 2500);
  };

  if (!open) {
    return (
      <button className={s.fab} onClick={() => onOpenChange(true)}>
        <span className={s.fabDot} aria-hidden />
        디자인 조정{count > 0 && <span className={s.badge}>{count}</span>}
      </button>
    );
  }

  return (
    <aside className={s.panel} aria-label="디자인 패널">
      <header className={s.head}>
        <div>
          <h2>디자인 조정</h2>
          <p>화면을 보면서 바로 바꿔 보세요. 이 브라우저에만 저장돼요.</p>
        </div>
        <button className={s.close} onClick={() => onOpenChange(false)} aria-label="패널 닫기">✕</button>
      </header>

      <div className={s.body}>
        <section>
          <h3>포인트 색 프리셋</h3>
          <div className={s.presets}>
            {ACCENT_PRESETS.map((p) => {
              const on = (o.colors['--accent'] ?? '#bdfe1c').toLowerCase() === p.accent;
              return (
                <button
                  key={p.name}
                  className={s.preset}
                  aria-pressed={on}
                  onClick={() => {
                    const isDefault = p.accent === '#bdfe1c';
                    setColor('--accent', isDefault ? null : p.accent);
                    setColor('--text-accent', isDefault ? null : p.text);
                  }}
                >
                  <span style={{ background: p.accent }} />
                  {p.name}
                </button>
              );
            })}
          </div>
        </section>

        <section>
          <h3>색상</h3>
          {COLOR_TOKENS.map((c) => {
            const v = o.colors[c.token] ?? c.default;
            const changed = Boolean(o.colors[c.token]);
            return (
              <div key={c.token} className={s.row}>
                <label className={s.swatch} style={{ background: v }}>
                  <input type="color" value={v} onChange={(e) => setColor(c.token, e.target.value)} aria-label={`${c.label} 색 고르기`} />
                </label>
                <div className={s.rowText}>
                  <span className={s.rowLabel}>
                    {c.label}
                    {changed && <i className={s.changed}>변경됨</i>}
                  </span>
                  <span className={s.rowHint}>{c.hint}</span>
                </div>
                <input
                  className={s.hex}
                  defaultValue={v.toUpperCase()}
                  key={v}
                  onBlur={(e) => HEX.test(e.target.value) && setColor(c.token, e.target.value.toLowerCase())}
                  onKeyDown={(e) => e.key === 'Enter' && (e.target as HTMLInputElement).blur()}
                  aria-label={`${c.label} HEX`}
                />
                {changed && (
                  <button className={s.undo} onClick={() => setColor(c.token, null)} aria-label={`${c.label} 되돌리기`}>↺</button>
                )}
              </div>
            );
          })}
        </section>

        <section>
          <h3>모양 · 글자</h3>
          {SCALE_TOKENS.map((t) => {
            const v = o.scales[t.token] ?? 1;
            return (
              <div key={t.token} className={s.slider}>
                <div className={s.sliderHead}>
                  <span className={s.rowLabel}>{t.label}</span>
                  <span className={s.value}>{Math.round(v * 100)}%</span>
                </div>
                <input type="range" min={t.min} max={t.max} step={t.step} value={v} onChange={(e) => setScale(t.token, Number(e.target.value))} aria-label={t.label} />
                <span className={s.rowHint}>{t.hint}</span>
              </div>
            );
          })}
          <label className={s.slider}>
            <span className={s.rowLabel}>폰트</span>
            <select className={s.select} value={o.font} onChange={(e) => setFont(e.target.value)}>
              {FONTS.map((f) => (
                <option key={f.name} value={f.name}>{f.label}</option>
              ))}
            </select>
          </label>
        </section>

        <section>
          <h3>문구</h3>
          <button className={s.toggle} aria-pressed={editing} onClick={() => setEditing((v) => !v)}>
            <span className={s.switch} aria-hidden />
            {editing ? '문구 수정 중 · 끝내려면 누르세요' : '문구 수정 모드 켜기'}
          </button>
          <p className={s.rowHint}>
            {editing
              ? '화면의 글자를 클릭해서 바로 고치세요. 수정 중에는 버튼을 눌러도 화면이 넘어가지 않아요.'
              : '켜면 화면의 글자를 직접 고칠 수 있어요. 다른 화면은 아래 개발용 버튼으로 이동하세요.'}
          </p>
          {o.copy.length > 0 && (
            <ul className={s.copyList}>
              {o.copy.map((c) => (
                <li key={c.before}>
                  <span className={s.before}>{c.before}</span>
                  <span className={s.after}>{c.after || '(빈 문구)'}</span>
                  <button className={s.undo} onClick={() => removeCopy(c.before)} aria-label="이 문구 되돌리기">↺</button>
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>

      <footer className={s.foot}>
        <span className={s.count}>{count > 0 ? `변경 ${count}개` : '아직 바꾼 게 없어요'}</span>
        <div className={s.footBtns}>
          {confirmReset ? (
            <>
              <button className={s.ghost} onClick={() => setConfirmReset(false)}>취소</button>
              <button className={s.danger} onClick={() => { reset(); setConfirmReset(false); }}>모두 되돌리기</button>
            </>
          ) : (
            <>
              <button className={s.ghost} disabled={count === 0} onClick={() => setConfirmReset(true)}>되돌리기</button>
              <button className={s.primary} disabled={count === 0} onClick={copy}>
                {copied === 'ok' ? '복사했어요' : copied === 'fail' ? '아래 글을 직접 복사하세요' : '변경 요청서 복사'}
              </button>
            </>
          )}
        </div>
        {copied === 'fail' && <textarea className={s.fallback} readOnly value={request} onFocus={(e) => e.target.select()} />}
        <p className={s.footHint}>복사한 요청서를 Claude에게 붙여넣으면 실제 코드에 반영돼요.</p>
      </footer>
    </aside>
  );
}
