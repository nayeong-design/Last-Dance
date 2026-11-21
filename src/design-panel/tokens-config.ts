/**
 * 디자인 패널에서 조정할 수 있는 토큰 목록.
 * 여기 항목을 추가하면 패널에 컨트롤이 자동으로 생깁니다.
 */
export interface ColorToken {
  token: string;
  label: string;
  hint: string;
  default: string;
}

export const COLOR_TOKENS: ColorToken[] = [
  { token: '--accent', label: '포인트 색', hint: '버튼, 체크, 테두리, 글로우', default: '#bdfe1c' },
  { token: '--text-accent', label: '포인트 글자', hint: '리캡 다시보기 같은 강조 문구', default: '#b8f71b' },
  { token: '--bg-base', label: '앱 바탕', hint: '홈 · 모달 뒤 배경', default: '#131417' },
  { token: '--bg-surface', label: '화면 바탕', hint: '설정 · 갤러리 · 상세 배경', default: '#1d1e21' },
  { token: '--bg-card-strong', label: '카드', hint: '홈의 큰 카드', default: '#313236' },
  { token: '--bg-elevated', label: '떠 있는 요소', hint: '입력창 · 헤더 · 칩', default: '#292a2e' },
  { token: '--text-primary', label: '기본 글자', hint: '제목과 본문', default: '#f7f8fa' },
  { token: '--text-secondary', label: '보조 글자', hint: '설명 · 라벨', default: '#c7c9ce' },
  { token: '--status-recording', label: '녹화 표시', hint: '기록 중 빨간 점', default: '#ff3654' },
];

export const ACCENT_PRESETS = [
  { name: '라임 (기본)', accent: '#bdfe1c', text: '#b8f71b' },
  { name: '스카이', accent: '#4cc9ff', text: '#6fd3ff' },
  { name: '코랄', accent: '#ff7a59', text: '#ff8e72' },
  { name: '바이올렛', accent: '#a98bff', text: '#b9a0ff' },
  { name: '골드', accent: '#ffc83d', text: '#ffd166' },
];

export interface ScaleToken {
  token: string;
  label: string;
  hint: string;
  min: number;
  max: number;
  step: number;
}

export const SCALE_TOKENS: ScaleToken[] = [
  { token: '--radius-scale', label: '모서리 둥글기', hint: '카드 · 버튼 · 창', min: 0, max: 2, step: 0.05 },
  { token: '--type-scale-title', label: '제목 크기', hint: 'Headline · Title', min: 0.8, max: 1.3, step: 0.01 },
  { token: '--type-scale-body', label: '본문 크기', hint: 'Body · Label · Caption', min: 0.8, max: 1.3, step: 0.01 },
];

/** Google Fonts에서 불러올 수 있는 한글 폰트 */
export const FONTS = [
  { name: 'Asta Sans', label: 'Asta Sans (기본)' },
  { name: 'Noto Sans KR', label: 'Noto Sans KR' },
  { name: 'IBM Plex Sans KR', label: 'IBM Plex Sans KR' },
  { name: 'Gothic A1', label: 'Gothic A1' },
  { name: 'Nanum Gothic', label: '나눔고딕' },
  { name: 'Gowun Dodum', label: '고운돋움' },
];
