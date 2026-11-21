package com.lastdance.app.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lastdance.app.R
import com.lastdance.app.pleos.CameraId
import com.lastdance.app.pleos.Pleos
import com.lastdance.app.pleos.Reaction
import com.lastdance.app.state.LastDanceViewModel
import com.lastdance.app.state.Screen
import com.lastdance.app.state.UiState
import com.lastdance.app.state.fmtTime
import com.lastdance.app.ui.components.LDCard
import com.lastdance.app.ui.components.Stat
import com.lastdance.app.ui.components.pressable
import com.lastdance.app.ui.theme.LD
import com.lastdance.app.ui.theme.LDType
import com.lastdance.app.ui.theme.Radius
import kotlinx.coroutines.delay

/**
 * 주행 중 스플릿 뷰. 실제 Pleos Connect에서는 왼쪽 내비가 시스템/내비 앱 영역이고
 * 이 앱은 오른쪽 패널만 그린다. 앱 단독 실행 시를 위해 왼쪽에 Navigation SDK 요약을 함께 둔다.
 */
@Composable
fun DrivingScreen(s: UiState, vm: LastDanceViewModel) {
    Row(Modifier.fillMaxSize().background(LD.bgBase), horizontalArrangement = Arrangement.spacedBy(22.dp)) {
        NavPanel(s, vm, Modifier.width(838.dp).fillMaxHeight())
        Box(Modifier.width(840.dp).fillMaxHeight().clip(RoundedCornerShape(Radius.window)).background(LD.bgSurface)) {
            if (s.screen == Screen.Camera) CameraPanel(s, vm) else RecordPanel(s, vm)
        }
    }
}

@Composable
private fun NavPanel(s: UiState, vm: LastDanceViewModel, modifier: Modifier) {
    val route = Pleos.current.navigation.routeAt((s.recording.elapsed % 600) / 600f)
    Box(modifier.clip(RoundedCornerShape(Radius.window))) {
        Image(painterResource(R.drawable.nav_road), "내비게이션 지도", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Row(
            Modifier.padding(24.dp).fillMaxWidth().clip(RoundedCornerShape(Radius.md)).background(LD.bgSurface).padding(28.dp),
            verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("↱ ${route.nextTurnKm}km", style = LDType.headlineSBold)
            Text(route.nextTurnRoad, style = LDType.bodySRegular, color = LD.textSecondary)
        }
        Image(painterResource(R.drawable.nav_car), null, Modifier.align(Alignment.Center).offset(y = 260.dp).size(120.dp, 180.dp))

        // ADAS 라스트댄스 알리기
        if (s.notifying) {
            Text(
                "오늘 이 차와의 마지막이에요 ㅜㅜ", style = LDType.labelMBold,
                modifier = Modifier.align(Alignment.Center).offset(y = 60.dp).clip(RoundedCornerShape(99.dp)).background(LD.bgSurface).padding(horizontal = 28.dp, vertical = 14.dp),
            )
            Row(Modifier.align(Alignment.TopCenter).padding(top = 220.dp), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                s.reactions.forEach { r ->
                    Image(painterResource(r.drawable()), r.name, Modifier.size(96.dp).clip(RoundedCornerShape(Radius.md)).background(LD.bgGlass).padding(12.dp))
                }
            }
        }
        Row(
            Modifier.align(Alignment.BottomCenter).padding(bottom = 180.dp).clip(RoundedCornerShape(99.dp)).background(LD.bgSurface)
                .pressable(if (s.notifying) "알리기 취소" else "라스트댄스 알리기") { vm.setNotify(!s.notifying) }
                .padding(horizontal = 28.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (!s.notifying) Icon(Icons.Default.Star, null, tint = LD.textPrimary, modifier = Modifier.size(28.dp))
            Text(if (s.notifying) "알리기 취소" else "라스트댄스 알리기", style = LDType.labelSMedium)
        }
        Row(
            Modifier.align(Alignment.BottomCenter).padding(24.dp).fillMaxWidth().clip(RoundedCornerShape(Radius.md)).background(LD.bgSurface).padding(28.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(route.eta, style = LDType.bodySBold)
            Text("${route.remainMin}분 · ${route.remainKm}km", style = LDType.bodySRegular, color = LD.textSecondary, modifier = Modifier.weight(1f))
            Text("안내종료", style = LDType.labelMBold, color = LD.textOnAccent, modifier = Modifier.clip(RoundedCornerShape(Radius.sm)).background(LD.textPrimary).padding(horizontal = 24.dp, vertical = 14.dp))
        }
    }
}

private fun Reaction.drawable() = when (this) {
    Reaction.Fist -> R.drawable.emoji_fist
    Reaction.Thumb -> R.drawable.emoji_thumb
    Reaction.Heart -> R.drawable.emoji_heart
    Reaction.Party -> R.drawable.emoji_party
    Reaction.Clap -> R.drawable.emoji_clap
}

@Composable
private fun RecordPanel(s: UiState, vm: LastDanceViewModel) {
    val rec = s.recording
    val route = Pleos.current.navigation.routeAt((rec.elapsed % 600) / 600f)
    val weather = Pleos.current.weather.now()
    val blink by rememberInfiniteTransition(label = "rec").animateFloat(1f, 0.2f, infiniteRepeatable(tween(700), RepeatMode.Reverse), label = "dot")

    Column(Modifier.fillMaxSize().padding(horizontal = 40.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Box(Modifier.fillMaxWidth().height(132.dp)) {
            Box(Modifier.align(Alignment.CenterStart).size(80.dp).pressable("뒤로") { vm.back() }, contentAlignment = Alignment.CenterStart) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LD.textPrimary, modifier = Modifier.size(40.dp))
            }
            Text("Last Dance 주행기록", style = LDType.titleMBold, modifier = Modifier.align(Alignment.Center))
        }
        LDCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(36.dp), verticalArrangement = Arrangement.spacedBy(36.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(if (rec.paused) "기록 중지됨" else "기록 중", style = LDType.bodyMBold)
                    if (!rec.paused) Box(Modifier.size(16.dp).alpha(blink).clip(CircleShape).background(LD.statusRecording))
                    Text(fmtTime(rec.elapsed), style = LDType.bodyMBold)
                }
                Row { Stat("현재 지점", route.currentPlace, Modifier.weight(2f)); Stat("이동 거리", "${route.distanceKm}km", Modifier.weight(1.3f)); Stat("기록", "스냅샷 ${rec.snapshots}", Modifier.weight(1f)) }
                Row { Stat("목적지", route.destination, Modifier.weight(2f)); Stat("날씨", weather.label, Modifier.weight(2.3f)) }
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RecButton(if (rec.paused) "기록 재개" else "기록 중지", LD.bgPressed, Modifier.weight(1f)) { vm.togglePause() }
                    RecButton("기록 종료", LD.statusDangerBg, Modifier.weight(1f)) { vm.stopRecording() }
                }
            }
        }
        Row(Modifier.height(200.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Tile("Gleo AI", Modifier.width(200.dp)) { vm.askGleo() }
            Tile("드라이브 스냅샷", Modifier.weight(1f)) { vm.go(Screen.Camera) }
            Image(
                painterResource(R.drawable.snapshot_thumb), "최근 스냅샷 보기",
                Modifier.width(200.dp).fillMaxHeight().clip(RoundedCornerShape(Radius.md)).pressable("갤러리") { vm.go(Screen.Gallery) },
                contentScale = ContentScale.Crop,
            )
        }
        Image(painterResource(R.drawable.player), "재생 중인 음악", Modifier.fillMaxWidth().height(236.dp).clip(RoundedCornerShape(Radius.md)), contentScale = ContentScale.Crop)
    }
}

@Composable
private fun RecButton(text: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.height(84.dp).clip(RoundedCornerShape(Radius.sm)).background(color).pressable(onClick = onClick), contentAlignment = Alignment.Center) {
        Text(text, style = LDType.bodyLBold)
    }
}

@Composable
private fun Tile(text: String, modifier: Modifier, onClick: () -> Unit) {
    LDCard(modifier.fillMaxHeight(), onClick = onClick) {
        Text(text, style = LDType.bodyMBold, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp))
    }
}

/** 드라이브 스냅샷 — 3·2·1 카운트다운 후 촬영 */
@Composable
private fun CameraPanel(s: UiState, vm: LastDanceViewModel) {
    var count by remember { mutableIntStateOf(3) }
    LaunchedEffect(count) {
        if (count == 0) { vm.snapshot(); delay(650); vm.back() } else { delay(1000); count -= 1 }
    }
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().height(680.dp)) {
            Image(painterResource(R.drawable.camera_preview), "실내 카메라 미리보기", Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            if (count > 0) Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${count}초 후 촬영", style = LDType.titleMBold)
                Box(Modifier.padding(top = 20.dp).size(160.dp).border(8.dp, LD.accent, CircleShape), contentAlignment = Alignment.Center) {
                    Text("$count", style = LDType.headlineLBold, color = LD.accent)
                }
            } else Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.6f)))
        }
        Row(Modifier.fillMaxWidth().padding(28.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            CameraId.entries.forEach { id ->
                val on = id == s.cameraId
                Text(
                    id.label, style = LDType.bodySSemibold,
                    modifier = Modifier.clip(RoundedCornerShape(99.dp)).background(LD.bgElevated)
                        .border(1.dp, if (on) LD.accent else Color.Transparent, RoundedCornerShape(99.dp))
                        .pressable(id.label) { vm.selectCamera(id) }.padding(horizontal = 32.dp, vertical = 16.dp),
                )
            }
        }
        Row(Modifier.fillMaxWidth().weight(1f).padding(horizontal = 60.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Image(painterResource(R.drawable.snapshot_thumb), "최근 스냅샷", Modifier.size(116.dp).clip(RoundedCornerShape(Radius.md)), contentScale = ContentScale.Crop)
            Box(Modifier.size(170.dp).clip(CircleShape).background(LD.accentGlow).padding(14.dp).clip(CircleShape).background(Color.White).pressable("지금 촬영") { count = 0 })
            Box(Modifier.size(116.dp).clip(RoundedCornerShape(Radius.md)).background(LD.bgBase).pressable("닫기") { vm.back() }, contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Close, null, tint = LD.textPrimary, modifier = Modifier.size(48.dp))
            }
        }
    }
}
