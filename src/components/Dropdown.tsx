import { useEffect, useId, useLayoutEffect, useRef, useState, type CSSProperties } from 'react';
import { IcDropdown } from './icons';

export interface DropdownProps<T extends string | number> {
  label: string; // 접근성 이름 (예: "년도")
  placeholder: string; // 선택 전 표시
  value: T | undefined;
  options: { value: T; label: string }[];
  onChange: (v: T) => void;
  style?: CSSProperties;
  className?: string;
}

const ROW = 80; // 옵션 한 줄 높이 (px)
const VISIBLE = 5;

/**
 * 디자인 시스템 드롭다운. 브라우저 기본 <select> 목록 대신
 * 앱과 같은 표면(bg-elevated) · 테두리 · 반경 · 타입 램프를 쓰는 목록을 띄운다.
 * 아래 공간이 부족하면 위로 펼친다.
 */
export function Dropdown<T extends string | number>({ label, placeholder, value, options, onChange, style, className }: DropdownProps<T>) {
  const [open, setOpen] = useState(false);
  const [up, setUp] = useState(false);
  const [active, setActive] = useState(0);
  const root = useRef<HTMLDivElement>(null);
  const list = useRef<HTMLUListElement>(null);
  const id = useId();
  const current = options.find((o) => o.value === value);

  // 아래로 펼칠 공간이 없으면 위로
  useLayoutEffect(() => {
    if (!open || !root.current) return;
    const el = root.current;
    const win = el.closest('.app-window') ?? el.closest('.stage');
    if (!win) return;
    const r = el.getBoundingClientRect();
    const w = win.getBoundingClientRect();
    const scale = r.height / el.offsetHeight || 1;
    const listH = (Math.min(options.length, VISIBLE) * ROW + 24) * scale;
    setUp(r.bottom + listH > w.bottom - 16 * scale && r.top - listH > w.top);
  }, [open, options.length]);

  // 선택된 항목이 보이도록 스크롤
  useEffect(() => {
    if (!open) return;
    const i = Math.max(0, options.findIndex((o) => o.value === value));
    setActive(i);
    requestAnimationFrame(() => list.current?.children[i]?.scrollIntoView({ block: 'center' }));
  }, [open]); // eslint-disable-line react-hooks/exhaustive-deps

  // 바깥 클릭 시 닫기
  useEffect(() => {
    if (!open) return;
    const close = (e: PointerEvent) => !root.current?.contains(e.target as Node) && setOpen(false);
    document.addEventListener('pointerdown', close);
    return () => document.removeEventListener('pointerdown', close);
  }, [open]);

  const pick = (v: T) => {
    onChange(v);
    setOpen(false);
  };

  const onKey = (e: React.KeyboardEvent) => {
    if (!open && (e.key === 'ArrowDown' || e.key === 'Enter' || e.key === ' ')) {
      e.preventDefault();
      setOpen(true);
      return;
    }
    if (!open) return;
    if (e.key === 'Escape') setOpen(false);
    else if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
      e.preventDefault();
      const n = (active + (e.key === 'ArrowDown' ? 1 : -1) + options.length) % options.length;
      setActive(n);
      list.current?.children[n]?.scrollIntoView({ block: 'nearest' });
    } else if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      pick(options[active].value);
    }
  };

  return (
    <div ref={root} className={`dropdown ${open ? 'dropdown--open' : ''} ${className ?? ''}`} style={style}>
      <button
        type="button"
        className="select dropdown__trigger"
        aria-haspopup="listbox"
        aria-expanded={open}
        aria-controls={id}
        aria-label={`${label}: ${current?.label ?? '선택 안 함'}`}
        onClick={() => setOpen((v) => !v)}
        onKeyDown={onKey}
      >
        <span className={`t-body-l-bold ${current ? '' : 'dropdown__placeholder'}`}>{current?.label ?? placeholder}</span>
        <IcDropdown className="dropdown__chevron" />
      </button>
      {open && (
        <ul
          ref={list}
          id={id}
          role="listbox"
          aria-label={label}
          className={`dropdown__list ${up ? 'dropdown__list--up' : ''}`}
          style={{ maxHeight: VISIBLE * ROW + 24 }}
        >
          {options.map((o, i) => {
            const sel = o.value === value;
            return (
              <li
                key={String(o.value)}
                role="option"
                aria-selected={sel}
                className={`dropdown__option t-body-l-bold ${i === active ? 'is-active' : ''}`}
                onPointerEnter={() => setActive(i)}
                onClick={() => pick(o.value)}
              >
                {o.label}
                {sel && (
                  <svg width="28" height="28" viewBox="0 0 24 24" fill="none" aria-hidden>
                    <path d="M5 12.5l4.5 4.5L19 7.5" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                )}
              </li>
            );
          })}
        </ul>
      )}
    </div>
  );
}
