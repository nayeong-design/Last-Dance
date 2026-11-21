package com.lastdance.app.ui.theme

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private val Scheme = darkColorScheme(
    primary = LD.accent,
    onPrimary = LD.textOnAccent,
    background = LD.bgBase,
    onBackground = LD.textPrimary,
    surface = LD.bgSurface,
    onSurface = LD.textPrimary,
    surfaceVariant = LD.bgElevated,
    outline = LD.borderStrong,
    error = LD.statusRecording,
)

/**
 * 디자인(1700×1184 px)을 실제 앱 윈도우 크기에 맞춘다.
 * Density를 바꿔 1.dp = 디자인 1px, 1.sp = 디자인 1px 이 되게 하므로
 * 화면 코드에서 Figma 수치를 그대로 dp/sp로 쓸 수 있다.
 */
@Composable
fun LastDanceTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme) {
        BoxWithConstraints {
            val px = with(LocalDensity.current) { maxWidth.toPx() }
            val designDensity = px / Window.DESIGN_WIDTH
            CompositionLocalProvider(
                LocalDensity provides Density(designDensity, fontScale = 1f),
                LocalTextStyle provides LDType.bodyMRegular.copy(color = LD.textPrimary),
                content = content,
            )
        }
    }
}
