import { useCallback, useEffect, useState } from 'react';
import { COLOR_TOKENS, FONTS, SCALE_TOKENS } from './tokens-config';

export interface CopyEdit {
  screen: string;
  before: string;
  after: string;
}

export interface Overrides {
  colors: Record<string, string>;
  scales: Record<string, number>;
  font: string;
  copy: CopyEdit[];
}

const KEY = 'last-dance:design-overrides:v1';
const EMPTY: Overrides = { colors: {}, scales: {}, font: FONTS[0].name, copy: [] };

function load(): Overrides {
  try {
    const raw = localStorage.getItem(KEY);
    return raw ? { ...EMPTY, ...JSON.parse(raw) } : EMPTY;
  } catch {
    return EMPTY;
  }
}

function ensureFont(name: string) {
  if (name === FONTS[0].name) return;
  const id = `gf-${name.replace(/\s+/g, '-')}`;
  if (document.getElementById(id)) return;
  const link = document.createElement('link');
  link.id = id;
  link.rel = 'stylesheet';
  link.href = `https://fonts.googleapis.com/css2?family=${encodeURIComponent(name).replace(/%20/g, '+')}:wght@400;500;600;700;800&display=swap`;
  document.head.appendChild(link);
}

/** 토큰 덮어쓰기를 :root 인라인 스타일에 적용하고 브라우저에 저장한다. */
export function useDesignOverrides() {
  const [o, setO] = useState<Overrides>(load);

  useEffect(() => {
    const root = document.documentElement.style;
    COLOR_TOKENS.forEach(({ token }) => {
      const v = o.colors[token];
      if (v) root.setProperty(token, v);
      else root.removeProperty(token);
    });
    SCALE_TOKENS.forEach(({ token }) => {
      const v = o.scales[token];
      if (v !== undefined && v !== 1) root.setProperty(token, String(v));
      else root.removeProperty(token);
    });
    if (o.font !== FONTS[0].name) {
      ensureFont(o.font);
      root.setProperty('--font-family', `'${o.font}', 'Asta Sans', system-ui, sans-serif`);
    } else root.removeProperty('--font-family');
    try {
      localStorage.setItem(KEY, JSON.stringify(o));
    } catch {
      /* 저장 불가 환경(시크릿 창 등) — 무시 */
    }
  }, [o]);

  const setColor = useCallback((token: string, v: string | null) =>
    setO((p) => {
      const colors = { ...p.colors };
      if (v) colors[token] = v;
      else delete colors[token];
      return { ...p, colors };
    }), []);
  const setScale = useCallback((token: string, v: number) => setO((p) => ({ ...p, scales: { ...p.scales, [token]: v } })), []);
  const setFont = useCallback((font: string) => setO((p) => ({ ...p, font })), []);
  const upsertCopy = useCallback((e: CopyEdit) =>
    setO((p) => {
      const rest = p.copy.filter((c) => c.before !== e.before);
      return { ...p, copy: e.after === e.before ? rest : [...rest, e] };
    }), []);
  const removeCopy = useCallback((before: string) => setO((p) => ({ ...p, copy: p.copy.filter((c) => c.before !== before) })), []);
  const reset = useCallback(() => setO(EMPTY), []);

  return { o, setColor, setScale, setFont, upsertCopy, removeCopy, reset };
}
