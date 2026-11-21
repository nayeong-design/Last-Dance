package com.lastdance.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lastdance.app.ui.theme.LD
import com.lastdance.app.ui.theme.LDType
import com.lastdance.app.ui.theme.Radius

/** 웹 `.pressable` — 누르면 살짝 작아진다. 차량 환경이라 리플 대신 스케일 피드백. */
fun Modifier.pressable(label: String? = null, role: Role = Role.Button, onClick: () -> Unit): Modifier = composed {
    val src = remember { MutableInteractionSource() }
    val pressed by src.collectIsPressedAsState()
    this
        .scale(if (pressed) 0.97f else 1f)
        .clickable(interactionSource = src, indication = null, role = role, onClick = onClick)
        .then(if (label != null) Modifier.semantics { contentDescription = label } else Modifier)
}

/** TopNav — 높이 148, 뒤로 · 제목(부제) · 우측 슬롯 */
@Composable
fun TopNav(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null, slot: @Composable () -> Unit = {}) {
    Box(Modifier.fillMaxWidth().height(148.dp).padding(horizontal = 40.dp)) {
        if (onBack != null) {
            Box(Modifier.align(Alignment.CenterStart).size(88.dp).pressable("뒤로", onClick = onBack), contentAlignment = Alignment.CenterStart) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LD.textPrimary, modifier = Modifier.size(44.dp))
            }
        }
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = LDType.titleLBold, color = LD.textPrimary)
            if (subtitle != null) Text(subtitle, style = LDType.labelMRegular, color = LD.textSecondary)
        }
        Box(Modifier.align(Alignment.CenterEnd)) { slot() }
    }
}

/** 93px 글래스 원형 화살표 버튼 */
@Composable
fun CircleArrow(label: String, onClick: () -> Unit) {
    Box(
        Modifier.size(93.dp).clip(CircleShape).background(LD.bgGlass).pressable(label, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = LD.textPrimary, modifier = Modifier.size(48.dp)) }
}

/** 100px 라임 CTA */
@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier
            .height(100.dp)
            .clip(RoundedCornerShape(Radius.md))
            .background(if (enabled) LD.accent else LD.bgPressed)
            .then(if (enabled) Modifier.pressable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) { Text(text, style = LDType.bodyLBold, color = if (enabled) LD.textOnAccent else LD.textDisabled) }
}

/** 카드 표면. accent=true 이면 Last Dance 라임 테두리 + 워시 */
@Composable
fun LDCard(
    modifier: Modifier = Modifier,
    color: Color = LD.bgCardStrong,
    accent: Boolean = false,
    radius: Dp = Radius.md,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val shape = RoundedCornerShape(radius)
    Box(
        modifier
            .clip(shape)
            .background(color)
            .then(if (accent) Modifier.background(LD.accentWash).border(BorderStroke(1.dp, LD.borderAccentSoft), shape) else Modifier)
            .then(if (onClick != null) Modifier.pressable(onClick = onClick) else Modifier),
        content = content,
    )
}

/** 라임 글로우 (웹의 blur radial-gradient 대체) */
fun Modifier.limeGlow(): Modifier = background(
    Brush.radialGradient(listOf(LD.accentGlow, Color.Transparent), radius = 600f)
)

@Composable
fun PillLabel(text: String) {
    Box(Modifier.clip(RoundedCornerShape(99.dp)).background(LD.bgElevated).padding(horizontal = 24.dp, vertical = 12.dp)) {
        Text(text, style = LDType.labelMBold, color = LD.textPrimary)
    }
}

/** 하단 토스트 */
@Composable
fun BoxScope.LDToast(message: String?) {
    AnimatedVisibility(message != null, Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp), enter = fadeIn(), exit = fadeOut()) {
        Box(
            Modifier.width(900.dp).clip(RoundedCornerShape(Radius.lg)).background(LD.bgElevated)
                .border(1.dp, LD.borderSubtle, RoundedCornerShape(Radius.lg)).padding(horizontal = 40.dp, vertical = 28.dp),
            contentAlignment = Alignment.Center,
        ) { Text(message ?: "", style = LDType.bodyMSemibold, color = LD.textPrimary) }
    }
}

@Composable
fun Stat(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = LDType.labelMRegular, color = LD.textSecondary)
        Text(value, style = LDType.bodySSemibold, color = LD.textPrimary)
    }
}

@Composable
fun Dot(color: Color, size: Dp = 16.dp) = Box(Modifier.size(size).clip(CircleShape).background(color))

@Composable
fun HSpacer(w: Dp) = Box(Modifier.width(w))

@Composable
fun RowCenter(content: @Composable () -> Unit) = Row(verticalAlignment = Alignment.CenterVertically) { content() }
