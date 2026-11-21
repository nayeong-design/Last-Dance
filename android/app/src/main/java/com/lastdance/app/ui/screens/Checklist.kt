package com.lastdance.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.lastdance.app.state.CHECKLIST
import com.lastdance.app.state.LastDanceViewModel
import com.lastdance.app.state.Screen
import com.lastdance.app.state.UiState
import com.lastdance.app.ui.components.PrimaryButton
import com.lastdance.app.ui.components.pressable
import com.lastdance.app.ui.theme.LD
import com.lastdance.app.ui.theme.LDType
import com.lastdance.app.ui.theme.Radius

/** Figma 354:2555 — 코스 선택 모달 */
@Composable
fun ChecklistScreen(s: UiState, vm: LastDanceViewModel) {
    Box(
        Modifier.fillMaxSize().clickable(remember { MutableInteractionSource() }, null) { vm.back() },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.width(796.dp).clip(RoundedCornerShape(Radius.xl)).background(LD.bgSurface)
                .clickable(remember { MutableInteractionSource() }, null) {}
                .padding(horizontal = 60.dp, vertical = 40.dp),
            verticalArrangement = Arrangement.spacedBy(40.dp),
        ) {
            Text("Last Dance를 선택해\n차량과의 이별주행을 시작하세요!", style = LDType.headlineSXBold)
            Column(Modifier.heightIn(max = 660.dp).verticalScroll(rememberScrollState())) {
                CHECKLIST.forEachIndexed { i, c ->
                    val on = c.id in s.selected
                    Row(
                        Modifier.fillMaxWidth().pressable(c.title, Role.Checkbox) { vm.toggle(c.id) }.padding(vertical = 28.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(48.dp),
                    ) {
                        Box(
                            Modifier.size(44.dp).clip(CircleShape).background(if (on) LD.accent else LD.bgPressed)
                                .border(1.dp, if (on) LD.accent else LD.borderSubtle, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) { if (on) Icon(Icons.Default.Check, null, tint = LD.textOnAccent, modifier = Modifier.size(30.dp)) }
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(c.title, style = LDType.titleMSemibold)
                            Text(c.place, style = LDType.bodySRegular, color = LD.textSecondary)
                        }
                    }
                    if (i < CHECKLIST.lastIndex) HorizontalDivider(Modifier.padding(start = 92.dp).width(566.dp), color = LD.borderStrong)
                }
            }
            PrimaryButton("시작하기", Modifier.fillMaxWidth(), enabled = s.selected.isNotEmpty()) {
                vm.startRecording(); vm.go(Screen.Driving)
            }
        }
    }
}
