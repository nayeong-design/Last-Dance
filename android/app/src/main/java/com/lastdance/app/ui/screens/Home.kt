package com.lastdance.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lastdance.app.R
import com.lastdance.app.state.LastDanceViewModel
import com.lastdance.app.state.Screen
import com.lastdance.app.ui.components.CircleArrow
import com.lastdance.app.ui.components.LDCard
import com.lastdance.app.ui.components.TopNav
import com.lastdance.app.ui.theme.LD
import com.lastdance.app.ui.theme.LDType
import com.lastdance.app.ui.theme.Radius

/** Figma 354:2378 — 1Day 주행기록 홈 */
@Composable
fun HomeScreen(vm: LastDanceViewModel) {
    Box(Modifier.fillMaxSize()) {
        TopNav("1Day 주행기록", onBack = {})

        // 오늘의 드라이브 시작하기 (39,137 · 1106×746)
        LDCard(Modifier.offset(39.dp, 137.dp).size(1106.dp, 746.dp)) {
            Row(Modifier.fillMaxWidth().padding(start = 40.dp, end = 40.dp, top = 76.dp), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text("오늘의 드라이브 시작하기", style = LDType.titleMBold)
                    Text("특별한 오늘의 드라이브 순간을 저장해보세요", style = LDType.bodyMRegular, color = LD.textSecondary)
                }
                CircleArrow("오늘의 드라이브 시작하기") { vm.startRecording(); vm.go(Screen.Driving) }
            }
            Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp, start = 40.dp, end = 40.dp).fillMaxWidth()) {
                Feature(Icons.Default.Place, "주행경로")
                Feature(Icons.Default.PlayArrow, "재생 음악 리스트")
                Feature(Icons.Default.Search, "분위기 감지\n드라이브 스냅샷")
                Feature(Icons.Default.Star, "실시간 차량\n내/외부 기록")
            }
        }

        // 주행로그 (1169,137 · 490×449)
        LDCard(Modifier.offset(1169.dp, 137.dp).size(490.dp, 449.dp), onClick = { vm.go(Screen.Gallery) }) {
            Row(Modifier.fillMaxWidth().padding(start = 39.dp, end = 39.dp, top = 39.dp)) {
                Text("주행로그", style = LDType.titleMBold, modifier = Modifier.weight(1f))
                CircleArrow("주행로그") { vm.go(Screen.Gallery) }
            }
        }

        // Last Dance (1169,610 · 490×273)
        LDCard(Modifier.offset(1169.dp, 610.dp).size(490.dp, 273.dp), accent = true, onClick = { vm.go(Screen.Setup) }) {
            // 하단 라임 글로우
            Box(
                Modifier.fillMaxSize().background(
                    Brush.verticalGradient(0.45f to Color.Transparent, 0.8f to LD.accentGlow, 1f to LD.accent.copy(alpha = 0.9f))
                )
            )
            Row(Modifier.fillMaxWidth().padding(start = 33.dp, end = 39.dp, top = 51.dp)) {
                Text("Last Dance", style = LDType.titleMBold, modifier = Modifier.weight(1f))
                CircleArrow("Last Dance") { vm.go(Screen.Setup) }
            }
        }

        // 미니 플레이어 (39,907 · 1620×236)
        Image(
            painterResource(R.drawable.player_wide), "재생 중: 12월 (Hate December) · l'll",
            Modifier.offset(39.dp, 907.dp).size(1620.dp, 236.dp).clip(RoundedCornerShape(Radius.md)),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.Feature(icon: ImageVector, label: String) {
    Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Icon(icon, null, tint = LD.accent, modifier = Modifier.size(40.dp))
        Text(label, style = LDType.labelMMedium, color = LD.textPrimary, textAlign = TextAlign.Center)
    }
}
