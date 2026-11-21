# CLAUDE.md — Last Dance 하네스

Hyundai Pleos Connect(Android Automotive) 인포테인먼트 앱 **Last Dance**의 웹 프로토타입입니다.
차를 팔거나 바꾸기 전 마지막 주행들을 기록하는 앱입니다. UI 작업 전 **DESIGN.md를 먼저 읽으세요.**

## 실행

```bash
npm install
npm run dev      # http://localhost:5173
npm run build    # tsc + vite build (커밋 전 필수 통과)
```

화면 하단의 개발용 버튼으로 모든 화면에 바로 이동할 수 있습니다.

## 구조

```
src/
  tokens/        tokens.css(primitive→semantic), typography.css
  styles/        base.css (스테이지, 공용 클래스)
  components/    ui.tsx(공용 컴포넌트), icons.tsx(Figma SVG)
  pleos/sdk.ts   Pleos SDK 목업 (Vehicle, Navigation, ADAS, Gleo AI, Camera, Weather)
  state/store.tsx 화면 이동·폼·녹화 상태 (useReducer)
  screens/       화면별 컴포넌트 + CSS Module
  design-panel/  코드 없이 토큰·문구를 조정하는 디자인 패널 (DESIGN.md 7장)
public/assets/   Figma에서 추출한 이미지(배경, 사진, 지도)
android/         Jetpack Compose 포팅 (Android Automotive / Pleos Connect) — android/README.md
scripts/         build_artifact.py (미리보기 링크용 단일 HTML 묶기)
```

웹과 Android는 **같은 토큰 이름, 같은 화면 id, 같은 SDK 인터페이스**를 유지합니다. 한쪽 UI를 바꾸면 다른 쪽도 같이 고칩니다.

## 규칙 (반드시)

1. **색·간격·반경·폰트는 토큰으로만.** 컴포넌트 CSS에 새 HEX, 임의 px 간격 금지. 필요하면 `tokens.css`에 semantic 토큰을 추가하고 DESIGN.md 표를 같이 고친다.
2. **Figma가 정답.** 레이아웃 수치는 Figma 노드 값을 따른다(DESIGN.md 5장 node id). 추측으로 바꾸지 않는다.
3. **차량 데이터는 `src/pleos/sdk.ts`를 통해서만.** 화면에서 하드코딩된 차량/내비 값을 새로 만들지 않는다.
4. 화면 이동은 `useStore().go/back`만 사용. 새 화면은 `Screen` 타입 + `App.tsx` 매핑 + DESIGN.md 화면 맵에 함께 추가.
5. 터치 대상은 `.pressable`, 최소 60px, `aria-label` 필수(아이콘 버튼).
6. 변경 후 `npm run build` 통과 + 해당 화면 스크린샷으로 Figma와 비교.

## Pleos SDK 목업 → Android 포팅 계획

`src/pleos/sdk.ts`의 인터페이스를 그대로 유지하고 구현만 교체합니다.

| 목업 | Pleos Playground SDK |
|---|---|
| `vehicle.getInfo()` | Vehicle SDK (주행거리, 배터리, 모델) |
| `navigation.routeAt()` | Navigation SDK (현재 위치, 목적지, TBT) |
| `adas.reactions` / `broadcastFarewell()` | ADAS SDK (주변 차량 V2X 리액션은 컨셉) |
| `gleo.ask()` | Gleo AI SDK |
| `camera.*` | 실내 카메라 API |
| 위치 기록 | Vehicle Location SDK |
| 기록 백업 | Sync SDK |

Android 포팅은 `android/`에 있습니다. `tokens.css` ↔ `Tokens.kt`, `typography.css` ↔ `Type.kt`, `sdk.ts` ↔ `PleosSdk.kt`, `store.tsx` ↔ `LastDanceViewModel.kt`가 1:1로 대응합니다. 실행에는 Android Studio와 Automotive 에뮬레이터(또는 Pleos Playground 에뮬레이터)가 필요합니다.

## 디자인 변경 요청서 처리

사용자가 "[Last Dance 디자인 변경 요청]"으로 시작하는 글을 붙여넣으면:
1. 색/스케일/폰트 값을 `src/tokens/tokens.css`의 기본값으로 반영하고 `tokens-config.ts`의 default도 같이 바꾼다.
2. 같은 값을 `android/.../Tokens.kt`(스케일은 `Radius`/`LDType`)에 반영한다.
3. 문구는 해당 화면 컴포넌트(웹 + Android)의 텍스트를 바꾼다.
4. DESIGN.md 표를 갱신하고 `npm run build` 후 화면을 확인한다.

## 알려진 제한

- 미니 플레이어는 정적 이미지(재생 기능 없음).
- 시스템 UI(상태바·좌측 주행정보·하단 독)는 배경 이미지.
- Pleos SDK는 모두 목업 데이터.
