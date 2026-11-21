import type { ReactNode } from 'react';
import { IcBack, IcChevronRight } from './icons';

/** Pleos Connect "Top navigation": back · title(+subtitle) · slot */
export function TopNav({ title, subtitle, onBack, slot }: { title: string; subtitle?: string; onBack?: () => void; slot?: ReactNode }) {
  return (
    <header className="topnav">
      {onBack ? (
        <button className="topnav__back" onClick={onBack} aria-label="뒤로">
          <IcBack />
        </button>
      ) : (
        <span className="topnav__side" />
      )}
      <div className="topnav__center">
        <h1 className="t-title-l-bold">{title}</h1>
        {subtitle && <p className="t-body-m-regular">{subtitle}</p>}
      </div>
      <span className="topnav__side">{slot}</span>
    </header>
  );
}

export function CircleArrow({ onClick, label }: { onClick?: () => void; label?: string }) {
  const inner = <IcChevronRight />;
  return onClick ? (
    <button className="btn-circle" onClick={onClick} aria-label={label}>
      {inner}
    </button>
  ) : (
    <span className="btn-circle" aria-hidden>
      {inner}
    </span>
  );
}

export function PrimaryButton({ children, onClick, disabled, style }: { children: ReactNode; onClick?: () => void; disabled?: boolean; style?: React.CSSProperties }) {
  return (
    <button className="btn-primary t-title-m-xbold" onClick={onClick} disabled={disabled} style={style}>
      {children}
    </button>
  );
}

export function MiniPlayer({ wide, style }: { wide?: boolean; style?: React.CSSProperties }) {
  return (
    <div className="mini-player" style={style} role="img" aria-label="재생 중: 12월 (Hate December) · l'll">
      <img src={wide ? 'assets/player-wide.png' : 'assets/player.png'} alt="" />
    </div>
  );
}

export function Toast({ children }: { children: ReactNode }) {
  return <div className="toast t-body-l-bold">{children}</div>;
}
