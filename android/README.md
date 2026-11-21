# Last Dance — Android Automotive (Pleos Connect)

웹 프로토타입(`../src`)을 Jetpack Compose로 옮긴 Android Automotive OS 앱입니다.

## 열기 / 실행

1. Android Studio(Ladybug 이상)에서 `android/` 폴더를 엽니다.
2. SDK Manager에서 **Android Automotive** 시스템 이미지를 받습니다(API 33/34 권장). Pleos Playground 에뮬레이터를 받았다면 그것을 사용하세요.
3. `app`을 실행합니다.

> 클라우드 작업 환경에서 Android SDK를 받을 수 없어서 이 프로젝트는 **아직 컴파일해 보지 않았습니다.** 처음 열었을 때 나오는 빌드 오류는 Android Studio에서 바로 고칠 수 있는 수준일 것으로 예상합니다.

## 구조

```
app/src/main/java/com/lastdance/app/
  MainActivity.kt          화면 전환 (웹 App.tsx)
  ui/theme/Tokens.kt       Primitive → Semantic 색, Radius, Space  (웹 tokens.css)
  ui/theme/Type.kt         Asta Sans 타입 램프                     (웹 typography.css)
  ui/theme/Theme.kt        1.dp = Figma 1px 로 맞추는 DesignDensity
  ui/components/           TopNav, CircleArrow, PrimaryButton, LDCard, LDToast …
  ui/screens/              Home, Farewell(Setup/Plan), Checklist, Driving(+Camera), Gallery, Detail
  pleos/PleosSdk.kt        Pleos SDK 인터페이스 + 목업              (웹 pleos/sdk.ts)
  state/LastDanceViewModel 상태와 화면 이동                         (웹 store.tsx)
```

## 웹 ↔ Android 대응

| 웹 | Android |
|---|---|
| `var(--accent)` | `LD.accent` |
| `.t-title-m-bold` | `LDType.titleMBold` |
| `useStore().go('detail')` | `vm.go(Screen.Detail)` |
| `adas.broadcastFarewell()` | `Pleos.current.adas.broadcastFarewell(): Flow<Reaction>` |

## 실제 Pleos SDK 연결

`pleos/PleosSdk.kt`의 인터페이스(`VehicleSdk`, `NavigationSdk`, `AdasSdk`, `GleoSdk` …)를 구현하는 어댑터를 만들고
`Pleos.current`를 바꾸면 됩니다. Pleos Playground 개발자 계정에서 SDK 저장소 주소를 받아 `settings.gradle.kts`에 추가하세요.

## 주행 중 화면

실제 Pleos Connect에서는 주행 중 왼쪽 내비가 시스템 영역이고 앱은 오른쪽 패널만 그립니다.
`DrivingScreen`은 앱만 띄웠을 때를 위해 왼쪽에 Navigation SDK 요약을 함께 그립니다.

폰트: Asta Sans (SIL Open Font License).
