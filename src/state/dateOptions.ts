/**
 * 차량 교체 일자 선택지 — 오늘(포함) 이후 날짜만 고를 수 있다.
 * 연도를 먼저 고르지 않았으면 올해 기준으로 계산한다.
 */
const range = (a: number, b: number) => Array.from({ length: Math.max(0, b - a + 1) }, (_, i) => a + i);
const daysIn = (y: number, m: number) => new Date(y, m, 0).getDate();

export interface DateParts {
  year?: number;
  month?: number;
  day?: number;
}

export function dateOptions(p: DateParts, now = new Date()) {
  const ty = now.getFullYear();
  const tm = now.getMonth() + 1;
  const td = now.getDate();
  const y = p.year ?? ty;
  const years = range(ty, ty + 2);
  const months = range(y === ty ? tm : 1, 12);
  const m = p.month;
  const days = m ? range(y === ty && m === tm ? td : 1, daysIn(y, m)) : range(1, 31);
  return { years, months, days };
}

/** 한 칸을 바꾼 뒤, 더 이상 고를 수 없게 된 아래 칸(월 · 일)은 비운다. */
export function clampDate(p: DateParts, now = new Date()): DateParts {
  const next = { ...p };
  const { months } = dateOptions(next, now);
  if (next.month && !months.includes(next.month)) {
    next.month = undefined;
    next.day = undefined;
  }
  const { days } = dateOptions(next, now);
  if (next.day && !days.includes(next.day)) next.day = undefined;
  return next;
}
