package com.lastdance.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lastdance.app.pleos.Pleos
import com.lastdance.app.state.CHECKLIST
import com.lastdance.app.state.LastDanceViewModel
import com.lastdance.app.state.REASONS
import com.lastdance.app.state.Screen
import com.lastdance.app.state.UiState
import com.lastdance.app.ui.components.LDCard
import com.lastdance.app.ui.components.PillLabel
import com.lastdance.app.ui.components.PrimaryButton
import com.lastdance.app.ui.components.TopNav
import com.lastdance.app.ui.components.pressable
import com.lastdance.app.ui.theme.LD
import com.lastdance.app.ui.theme.LDType
import com.lastdance.app.ui.theme.Radius

@Composable
private fun Heading() {
    Column(Modifier.offset(67.dp, 181.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("이주영 님을 위한\n차량 이별 프로세스 추천", style = LDType.headlineSBold)
        Text("나만의 차량 이별 여정을 기록으로 남겨보세요.", style = LDType.bodySRegular, color = LD.textSecondary)
    }
}

/** 오른쪽 액션 컬럼 (859,437) — 주행 시작 · 차량 초기화 · 이별 메시지 카드 */
@Composable
private fun Actions(vm: LastDanceViewModel, onStart: () -> Unit) {
    Column(Modifier.offset(859.dp, 437.dp).width(800.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        LDCard(Modifier.size(800.dp, 398.dp), onClick = onStart) {
            Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(LD.accentGlow, Color.Transparent), center = androidx.compose.ui.geometry.Offset(1400f, 0f), radius = 900f)))
            Text("Last Dance 주행 시작", style = LDType.titleMBold, modifier = Modifier.padding(40.dp))
            Box(Modifier.align(Alignment.BottomStart).padding(40.dp).size(60.dp, 30.dp).clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)).background(LD.accent))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SmallCard("차량 초기화", Icons.Default.Refresh) { vm.toast("차량 초기화는 이별 당일에 진행할 수 있어요") }
            SmallCard("이별 메시지 카드", Icons.Default.Edit) { vm.toast("다음 오너에게 남길 메시지 카드를 준비 중이에요") }
        }
    }
}

@Composable
private fun SmallCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    LDCard(Modifier.size(392.dp, 264.dp), onClick = onClick) {
        Text(title, style = LDType.titleMBold, modifier = Modifier.padding(40.dp))
        Icon(icon, null, tint = LD.accent, modifier = Modifier.align(Alignment.BottomStart).padding(40.dp).size(60.dp))
    }
}

/** Figma 354:2290 — 차량 교체 일자 / 이별 사유 등록 */
@Composable
fun FarewellSetupScreen(s: UiState, vm: LastDanceViewModel) {
    val f = s.farewell
    val car = Pleos.current.vehicle.info()
    Box(Modifier.fillMaxSize()) {
        TopNav("Last Dance", onBack = vm::back)
        Heading()
        Text("${car.nickname}  |  ${car.model}", style = LDType.labelMBold, modifier = Modifier.offset(67.dp, 411.dp))

        Column(
            Modifier.offset(67.dp, 485.dp).width(676.dp).clip(RoundedCornerShape(Radius.md))
                .border(1.dp, LD.borderSubtle, RoundedCornerShape(Radius.md)).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            PillLabel("차량 교체 일자")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Select(f.year?.let { "${it}년" } ?: "년도", (2026..2028).map { "${it}년" }, 184.dp) { i -> vm.updateFarewell { it.copy(year = 2026 + i) } }
                Select(f.month?.let { "${it}월" } ?: "월", (1..12).map { "${it}월" }, 160.dp) { i -> vm.updateFarewell { it.copy(month = i + 1) } }
                Select(f.day?.let { "${it}일" } ?: "일", (1..31).map { "${it}일" }, 160.dp) { i -> vm.updateFarewell { it.copy(day = i + 1) } }
            }
            PillLabel("차량 이별 사유")
            Select(f.reason, REASONS, 636.dp) { i -> vm.updateFarewell { it.copy(reason = REASONS[i]) } }
            Box(
                Modifier.fillMaxWidth().height(72.dp).clip(RoundedCornerShape(99.dp)).background(LD.bgElevated).padding(horizontal = 32.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                if (f.memo.isEmpty()) Text("차량 이별 사유를 작성하세요!", style = LDType.bodySRegular, color = LD.textDisabled)
                BasicTextField(
                    f.memo, { v -> vm.updateFarewell { it.copy(memo = v) } },
                    textStyle = LDType.bodySRegular.copy(color = LD.textPrimary), cursorBrush = SolidColor(LD.accent), singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        PrimaryButton("등록하기", Modifier.offset(67.dp, 1015.dp).width(676.dp)) { vm.register() }
        Actions(vm) { vm.toast("먼저 차량 교체 일자를 등록해주세요") }
    }
}

@Composable
private fun Select(value: String, options: List<String>, width: Dp, onPick: (Int) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        Row(
            Modifier.width(width).height(72.dp).clip(RoundedCornerShape(99.dp)).background(LD.bgElevated)
                .pressable("$value 선택") { open = true }.padding(horizontal = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(value, style = LDType.bodySBold, modifier = Modifier.weight(1f))
            Icon(Icons.Default.KeyboardArrowDown, null, tint = LD.textPrimary, modifier = Modifier.size(36.dp))
        }
        DropdownMenu(open, { open = false }, Modifier.background(LD.bgElevated)) {
            options.forEachIndexed { i, o ->
                DropdownMenuItem(text = { Text(o, style = LDType.bodySRegular) }, onClick = { onPick(i); open = false })
            }
        }
    }
}

/** Figma 354:2455 — 등록 완료 후 이별 프로세스 타임라인 */
@Composable
fun FarewellPlanScreen(s: UiState, vm: LastDanceViewModel) {
    val car = Pleos.current.vehicle.info()
    Box(Modifier.fillMaxSize()) {
        TopNav("Last Dance", onBack = vm::back)
        Heading()
        Column(Modifier.offset(67.dp, 520.dp)) {
            CHECKLIST.forEachIndexed { i, c ->
                Row(Modifier.height(142.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(40.dp).clip(CircleShape)
                            .background(if (i == 0) LD.accent.copy(alpha = 0.15f) else LD.bgPressed)
                            .border(2.dp, if (i == 0) LD.accent else Color.Transparent, CircleShape)
                    )
                    Column(Modifier.padding(start = 48.dp).width(566.dp)) {
                        Text(c.title, style = LDType.titleMSemibold)
                        Text(c.place, style = LDType.bodySRegular, color = LD.textSecondary)
                    }
                }
            }
        }
        Text(car.model, style = LDType.labelMBold, modifier = Modifier.offset(884.dp, 245.dp))
        Row(
            Modifier.offset(859.dp, 295.dp).clip(RoundedCornerShape(99.dp)).background(LD.bgBase).padding(horizontal = 28.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("차량 교체 일자", style = LDType.labelMRegular, color = LD.textSecondary)
            Text(s.farewell.dateLabel, style = LDType.labelMBold)
            Text("|", style = LDType.labelMRegular, color = LD.borderStrong)
            Text("차량 이별 사유", style = LDType.labelMRegular, color = LD.textSecondary)
            Text(s.farewell.reasonLabel, style = LDType.labelMBold)
        }
        Actions(vm) { vm.go(Screen.Checklist) }
    }
}
