import { COLOR_TOKENS, FONTS, SCALE_TOKENS } from './tokens-config';
import type { Overrides } from './useDesignOverrides';

const SCREEN_LABEL: Record<string, string> = {
  home: '홈', setup: '이별 설정', plan: '이별 플랜', checklist: '체크리스트',
  driving: '주행 기록', camera: '스냅샷', gallery: '갤러리', detail: '기록 상세',
};

export function changeCount(o: Overrides) {
  return (
    Object.keys(o.colors).length +
    Object.entries(o.scales).filter(([, v]) => v !== 1).length +
    (o.font !== FONTS[0].name ? 1 : 0) +
    o.copy.length
  );
}

/** Claude(또는 개발자)에게 그대로 붙여넣을 수 있는 변경 요청서 */
export function buildRequest(o: Overrides): string {
  const lines: string[] = ['[Last Dance 디자인 변경 요청]', '아래 값으로 디자인 토큰과 문구를 바꿔줘. 웹(tokens.css)과 Android(Tokens.kt)에 같이 반영하고 DESIGN.md 표도 갱신해줘.', ''];
  const colors = COLOR_TOKENS.filter((c) => o.colors[c.token]);
  if (colors.length) {
    lines.push('■ 색상');
    colors.forEach((c) => lines.push(`- ${c.label} (${c.token}): ${c.default.toUpperCase()} → ${o.colors[c.token].toUpperCase()}`));
    lines.push('');
  }
  const scales = SCALE_TOKENS.filter((t) => o.scales[t.token] !== undefined && o.scales[t.token] !== 1);
  if (scales.length || o.font !== FONTS[0].name) {
    lines.push('■ 모양 · 글자');
    scales.forEach((t) => lines.push(`- ${t.label} (${t.token}): 100% → ${Math.round(o.scales[t.token] * 100)}%`));
    if (o.font !== FONTS[0].name) lines.push(`- 폰트 (--font-family): Asta Sans → ${o.font}`);
    lines.push('');
  }
  if (o.copy.length) {
    lines.push('■ 문구');
    o.copy.forEach((c) => lines.push(`- [${SCREEN_LABEL[c.screen] ?? c.screen}] "${c.before}" → "${c.after}"`));
    lines.push('');
  }
  const css = [
    ...colors.map((c) => `  ${c.token}: ${o.colors[c.token]};`),
    ...scales.map((t) => `  ${t.token}: ${o.scales[t.token]};`),
    ...(o.font !== FONTS[0].name ? [`  --font-family: '${o.font}', system-ui, sans-serif;`] : []),
  ];
  if (css.length) lines.push('■ CSS', ':root {', ...css, '}');
  return lines.join('\n').trim();
}
