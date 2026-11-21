# DESIGN.md — Last Dance 디자인 계약서

> AI(Claude Code)와 사람이 UI를 고칠 때 **반드시 지키는 규칙**입니다.
> 원본: Figma `현대차 앱개발 공모전` › Page 3 (node `354:2287`)

## 1. 캔버스 & 레이아웃

| 항목 | 값 | 토큰 |
|---|---|---|
| 스테이지(Pleos Connect 화면) | 2600 × 1480 | `--stage-w`, `--stage-h` |
| 앱 윈도우 | x880, y116, 1700 × 1184, radius 40 | `--app-x/y/w/h`, `--radius-window` |
| 좌측 | 주행 정보 영역 (시스템 소유, 배경 이미지) | — |
| 하단 | 독/공조 바 (시스템 소유, 배경 이미지) | — |

- 스테이지는 고정 해상도이고 뷰포트에 맞춰 `scale()` 됩니다. 디자인 px = 코드 px.
- 고정 해상도 IVI라서 화면 내부 배치는 Figma 좌표의 **absolute 배치를 허용**합니다. 단, 카드 안의 반복 요소(리스트, 통계, 탭)는 flex로 짭니다.
- 주행 중(driving/camera) 화면만 앱 윈도우 대신 **스플릿 뷰**(내비 838px + 앱 840px)를 씁니다.

## 2. 토큰 규칙 (가장 중요)

토큰은 2단계입니다. `src/tokens/tokens.css`

1. **Primitive** `--p-*` : 원시 팔레트. *컴포넌트에서 직접 쓰지 않습니다.*
2. **Semantic** : 의도를 가진 이름. 컴포넌트는 이것만 씁니다.

| 그룹 | 토큰 |
|---|---|
| 배경 | `--bg-base` `--bg-surface` `--bg-elevated` `--bg-card-strong` `--bg-pressed` `--bg-scrim` `--bg-glass` |
| 텍스트 | `--text-primary` `--text-secondary` `--text-tertiary` `--text-disabled` `--text-on-accent` `--text-accent` |
| 테두리 | `--border-subtle` `--border-strong` `--border-card` `--border-accent` `--border-accent-soft` `--border-gallery` |
| 브랜드 | `--accent`(#BDFE1C 라임) `--accent-glow` `--accent-wash` |
| 상태 | `--status-recording` `--status-danger-bg` `--traffic-free/slow/jam` `--map-pin` `--map-waypoint` |
| 반경 | `--radius-xs`(10) … `--radius-window`(40) `--radius-pill` |
| 간격 | `--space-4` … `--space-60` |
| 모션 | `--ease-standard` `--dur-fast/base/slow` |

**금지**
- 새 HEX/RGB 값을 컴포넌트 CSS에 쓰지 않기. 필요하면 먼저 토큰을 추가하고 이 문서 표를 갱신.
- 라임(`--accent`)은 "Last Dance / 기록 / 주요 CTA" 의미에만. 장식용으로 남발 금지.
- Figma에 없는 그림자·그라데이션을 임의로 추가하지 않기.

## 3. 타이포그래피

폰트: **Asta Sans** (`--font-family`). 클래스는 `src/tokens/typography.css`.

| 클래스 | size / line-height | 주 용도 |
|---|---|---|
| `t-headline-l-bold` | 60/80 | 큰 숫자 |
| `t-headline-s-*` | 48/64 | 모달 제목 |
| `t-title-l-*` | 40/52 | 화면 제목(TopNav) |
| `t-title-m-*` | 36/44 | 카드 제목 |
| `t-title-s-*` | 32/40 | 섹션 제목, 탭(선택) |
| `t-body-l-*` | 30/38 | 버튼, 탭 |
| `t-body-m-*` | 28/36 | 본문 |
| `t-body-s-*` | 26/34 | 보조 본문 |
| `t-label-m-*` | 24/28 | 라벨 |
| `t-label-s-*` | 22/26, 20/24 | 작은 라벨 |
| `t-caption-medium` | 18/22 | 캡션 |

새 크기를 만들지 말고 위 램프에서 고릅니다.

## 4. 컴포넌트 (src/components, src/styles/base.css)

| 컴포넌트 | 위치 | 비고 |
|---|---|---|
| `TopNav` | ui.tsx | 148px 높이, 뒤로·제목·부제·우측 슬롯 |
| `CircleArrow` / `.btn-circle` | ui.tsx | 93px 글래스 원형 버튼 |
| `PrimaryButton` / `.btn-primary` | ui.tsx | 100px 라임 CTA, disabled 지원 |
| `MiniPlayer` | ui.tsx | 미디어 카드(현재 정적 이미지) |
| `Toast` | ui.tsx | 하단 피드백 |
| `.card`, `.card--lime-glow` | base.css | 카드 표면 |
| `Dropdown` | Dropdown.tsx | 선택 목록. 브라우저 기본 목록 대신 bg-elevated 표면 · radius-xl · 80px 행 · 선택 시 accent 체크. 공간이 없으면 위로 펼침 |
| `.select`, `.input`, `.pill-label` | base.css | 폼 |
| `.tab`, `.tab__bar` | base.css | 갤러리 탭 |
| 아이콘 | icons.tsx | Figma SVG export를 그대로 옮김 |

**상호작용 상태**: 모든 터치 대상은 `.pressable`(누름 시 scale/opacity) + `:focus-visible` 라임 아웃라인. 최소 터치 영역 60px 이상(차량 환경).

## 5. 화면 맵

| Screen id | 화면 | Figma node | 파일 |
|---|---|---|---|
| `home` | 1Day 주행기록 홈 | 354:2378 | screens/Home.tsx |
| `setup` | Last Dance 설정(교체일·사유) | 354:2290 | screens/Farewell.tsx |
| `plan` | 이별 프로세스 추천 타임라인 | 354:2455 | screens/Farewell.tsx |
| `checklist` | Last Dance 코스 선택 모달 | 354:2555 | screens/Checklist.tsx |
| `driving` | 주행 기록(스플릿 뷰) + ADAS 알리기 | Page 3 주행 프레임 | screens/Driving.tsx |
| `camera` | 드라이브 스냅샷(카운트다운) | Page 3 카메라 프레임 | screens/Driving.tsx |
| `gallery` | 갤러리 | 354:2737 | screens/Gallery.tsx |
| `detail` | 2026 Last Dance 기록 상세 | 354:2591 | screens/Detail.tsx |

흐름: `home → setup → (등록) plan → checklist → driving ⇄ camera → (기록 종료) detail`, `home → gallery → detail`.

## 6. 엣지 케이스 (구현됨)

- 교체 일자/사유 미입력 등록 → 토스트로 안내, 화면 이동 없음
- 교체 일자는 오늘(포함) 이후만 선택지에 나옴. 연·월을 바꿔 고를 수 없게 된 월·일은 자동으로 비움 (`src/state/dateOptions.ts`)
- 등록 전 "주행 시작" → 토스트 "먼저 차량 교체 일자를 등록해주세요"
- 체크리스트 0개 선택 → 시작하기 비활성
- 갤러리 검색/탭 결과 없음 → 빈 상태 문구
- 녹화 일시정지/재개, 스냅샷 카운트 반영, 알리기 취소
- 스냅샷 카메라 선택: 칸은 [좌, 중앙, 우]로 고정. 좌우 칸(두 글자 짧은 이름)을 누르면 그 카메라가 중앙 칸(전체 이름 + 교체 아이콘)과 자리를 바꾸고, 칸 넓이·위치는 변하지 않는다.
- 스냅샷 카운트다운: 3초 동안 원이 한 번만 채워진다(비어 있음 → 가득, 선형). 숫자만 3→2→1로 바뀐다.

## 7. 디자인 패널 (코드 없이 조정하기)

화면 오른쪽 아래 **디자인 조정** 버튼을 누르면 패널이 열립니다.

| 할 수 있는 것 | 바뀌는 토큰 |
|---|---|
| 포인트 색 프리셋 · 색상 9종 | `--accent`, `--text-accent`, `--bg-*`, `--text-primary/secondary`, `--status-recording` |
| 모서리 둥글기 | `--radius-scale` (모든 `--radius-*`에 곱해짐) |
| 제목 / 본문 크기 | `--type-scale-title`, `--type-scale-body` |
| 폰트 | `--font-family` (Google Fonts 한글 폰트) |
| 문구 수정 모드 | 화면 글자를 클릭해서 직접 수정 (화면을 옮겨도 유지) |

- 바꾼 값은 그 브라우저에만 저장됩니다. **변경 요청서 복사**를 눌러 Claude에게 붙여넣으면 실제 코드(웹 + Android)와 이 문서에 반영됩니다.
- 패널에 항목을 더하려면 `src/design-panel/tokens-config.ts`에 한 줄을 추가합니다.
- 라임 계열 파생 색(글로우, 테두리, 워시)은 `color-mix()`로 `--accent`에서 계산되므로 포인트 색 하나만 바꾸면 같이 바뀝니다.
- 패널 자체 스타일(`DesignPanel.module.css`)은 도구 UI라서 앱 토큰 규칙의 예외입니다.

## 8. 사운드

| 이벤트 | 소리 | 파일 |
|---|---|---|
| 라스트댄스 알리기 후 다른 차량의 응원 반응 수신 (반응 1개당 1번) | 묵직한 조약돌 두 개가 "톡, 톡" 부딪히는 0.4초 소리 (낮은 몸통 울림 520Hz + 저음 쿵). 매번 음높이를 ±8% 바꿔 기계적으로 들리지 않게 | `public/assets/pebble.wav`, `android/.../res/raw/pebble.wav` (`scripts/make_pebble.py`로 생성) |

- 볼륨 60%, 알림(sonification) 용도로 재생해 음악 위에 겹쳐 들리게 합니다.
- 웹은 브라우저 자동재생 정책 때문에 "라스트댄스 알리기"를 누른 순간부터 소리가 켜집니다.
