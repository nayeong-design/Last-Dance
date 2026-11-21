package com.lastdance.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 디자인 토큰 — 웹 `src/tokens/tokens.css`와 1:1 대응.
 * 화면 코드는 [LD] (semantic)만 사용하고 [Primitive]는 직접 쓰지 않는다.
 */
object Primitive {
    val Gray00 = Color(0xFF131417)
    val Gray50 = Color(0xFF1D1E21)
    val Gray75 = Color(0xFF292A2E)
    val Gray100 = Color(0xFF313236)
    val Gray150 = Color(0xFF36383E)
    val Gray200 = Color(0xFF44464E)
    val Gray300 = Color(0xFF686A72)
    val Gray350 = Color(0xFF858790)
    val Gray400 = Color(0xFFA4A8AE)
    val Gray500 = Color(0xFFC7C9CE)
    val Gray800 = Color(0xFFF7F8FA)
    val White = Color(0xFFFFFFFF)

    val LimeKey = Color(0xFFBDFE1C)
    val LimeText = Color(0xFFB8F71B)
    val Lime400 = Color(0xFF8BC91B)
    val Lime700 = Color(0xFF365319)
    val Green500 = Color(0xFF44B15C)

    val Red500 = Color(0xFFFF3654)
    val Red700 = Color(0xFFD90A35)
    val Red900 = Color(0xFF650019)
    val Orange500 = Color(0xFFFF8A00)
    val Blue500 = Color(0xFF286CFF)
    val Purple500 = Color(0xFF8547FF)
}

/** Semantic tokens */
object LD {
    // background
    val bgBase = Primitive.Gray00
    val bgSurface = Primitive.Gray50
    val bgElevated = Primitive.Gray75
    val bgCardStrong = Primitive.Gray100
    val bgPressed = Primitive.Gray150
    val bgScrim = Color(0xB3131417)
    val bgGlass = Color(0x33FFFFFF)

    // text
    val textPrimary = Primitive.Gray800
    val textSecondary = Primitive.Gray500
    val textTertiary = Primitive.Gray400
    val textDisabled = Primitive.Gray350
    val textOnAccent = Primitive.Gray00
    val textAccent = Primitive.LimeText

    // border
    val borderSubtle = Primitive.Gray200
    val borderStrong = Primitive.Gray300
    val borderCard = Color(0xFF252629)
    val borderAccent = Primitive.LimeKey
    val borderAccentSoft = Color(0xCCBDFE1C)
    val borderGallery = Primitive.Lime700

    // accent
    val accent = Primitive.LimeKey
    val accentGlow = Color(0x80BDFE1C)
    val accentWash = Brush.linearGradient(
        0.10f to Color(0x1ABDFE1C),
        0.93f to Color(0x1A44B15C),
        1.00f to Color(0x4DBDFE1C),
    )

    // status
    val statusRecording = Primitive.Red500
    val statusDangerBg = Primitive.Red900
    val trafficFree = Primitive.Lime400
    val trafficSlow = Primitive.Orange500
    val trafficJam = Primitive.Red700
    val mapPin = Primitive.Blue500
    val mapWaypoint = Primitive.Purple500
}

/** 반경 · 간격. 1.dp = 디자인 1px (MainActivity의 DesignDensity 참고) */
object Radius {
    val xs = 10.dp; val sm = 16.dp; val md = 20.dp; val lg = 24.dp
    val xl = 28.dp; val xxl = 32.dp; val window = 40.dp
}

object Space {
    val s4 = 4.dp; val s8 = 8.dp; val s12 = 12.dp; val s16 = 16.dp; val s20 = 20.dp
    val s24 = 24.dp; val s28 = 28.dp; val s32 = 32.dp; val s40 = 40.dp; val s48 = 48.dp; val s60 = 60.dp
}

/** Pleos Connect 앱 윈도우 크기 (디자인 기준) */
object Window {
    const val DESIGN_WIDTH = 1700f
    const val DESIGN_HEIGHT = 1184f
}
