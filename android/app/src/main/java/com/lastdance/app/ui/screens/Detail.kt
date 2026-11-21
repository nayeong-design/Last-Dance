package com.lastdance.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lastdance.app.R
import com.lastdance.app.pleos.Pleos
import com.lastdance.app.state.LastDanceViewModel
import com.lastdance.app.state.UiState
import com.lastdance.app.ui.components.Stat
import com.lastdance.app.ui.components.TopNav
import com.lastdance.app.ui.components.pressable
import com.lastdance.app.ui.theme.LD
import com.lastdance.app.ui.theme.LDType
import com.lastdance.app.ui.theme.Radius

private val STOPS = listOf(
    Triple("출발지", "18:09", "우리집"),
    Triple("경유지1", "18:09-20:12", "국립중앙박물관"),
    Triple("도착지", "20:12-20:45", "조선팰리스 강남"),
)

/** Figma 354:2591 — 2026 Last Dance 기록 상세 */
@Composable
fun DetailScreen(s: UiState, vm: LastDanceViewModel) {
    val snaps = if (s.recording.snapshots > 0) s.recording.snapshots else 4
    val weather = Pleos.current.weather.now()
    Box(Modifier.fillMaxSize()) {
        TopNav("2026 Last Dance", "2026.11.15 (일) 오후 8:46", onBack = vm::back) {
            Box(Modifier.size(60.dp).clip(RoundedCornerShape(Radius.md)).background(LD.bgElevated).pressable("편집") { vm.toast("기록 이름을 편집할 수 있어요") }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Edit, null, tint = LD.textSecondary, modifier = Modifier.size(32.dp))
            }
        }

        Row(Modifier.offset(40.dp, 171.dp)) {
            Column(
                Modifier.size(348.dp, 599.dp).clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)).background(LD.bgBase).padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(64.dp),
            ) {
                Text("Last Dance 경로", style = LDType.titleSBold)
                STOPS.forEachIndexed { i, (label, time, name) ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.Place, null, tint = if (i == 1) LD.mapWaypoint else LD.mapPin, modifier = Modifier.size(28.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(label, style = LDType.captionMedium)
                                Text(time, style = LDType.captionMedium, color = LD.textDisabled)
                            }
                            Text(name, style = LDType.bodySBold)
                        }
                    }
                }
            }
            Box(Modifier.size(1278.dp, 600.dp).clip(RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp))) {
                Image(painterResource(R.drawable.route_map), "Last Dance 주행 경로 지도", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Text(
                    "☀ 맑음 ${weather.tempC}℃   ☾ 야간", style = LDType.labelMRegular,
                    modifier = Modifier.align(Alignment.TopEnd).padding(28.dp).clip(RoundedCornerShape(99.dp)).background(LD.bgBase).padding(horizontal = 20.dp, vertical = 12.dp),
                )
            }
        }

        Column(Modifier.offset(48.dp, 809.dp), verticalArrangement = Arrangement.spacedBy(60.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("주행 정보", style = LDType.titleSBold)
                Row(horizontalArrangement = Arrangement.spacedBy(80.dp)) {
                    Stat("이동 거리", "14.9km"); Stat("평균 시속", "18km"); Stat("평균 전비", "5.8km/kw"); Stat("컨텐츠 기록", "스냅샷 ${snaps}건")
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Text("플레이리스트", style = LDType.titleSBold)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Image(painterResource(R.drawable.track_1), "12월 (Hate December) · l'll", Modifier.size(360.dp, 68.dp).clip(RoundedCornerShape(Radius.md)), contentScale = ContentScale.Crop)
                    Image(painterResource(R.drawable.track_2), "Hollywood · 검정치마", Modifier.size(360.dp, 68.dp).clip(RoundedCornerShape(Radius.md)), contentScale = ContentScale.Crop)
                    Text("외 10건", style = LDType.bodySRegular)
                }
            }
        }

        Column(
            Modifier.offset(1190.dp, 913.dp).width(458.dp).clip(RoundedCornerShape(20.dp)).background(LD.bgElevated)
                .border(1.dp, LD.accent, RoundedCornerShape(20.dp)).pressable("리캡 다시보기") { vm.toast("리캡 영상을 만드는 중이에요") }.padding(28.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Icon(Icons.Default.Refresh, null, tint = LD.textAccent, modifier = Modifier.size(60.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("리캡 다시보기", style = LDType.titleMXBold, color = LD.textAccent)
                Text("마지막 주행 다시 경험하기", style = LDType.bodyMBold)
            }
        }
    }
}
