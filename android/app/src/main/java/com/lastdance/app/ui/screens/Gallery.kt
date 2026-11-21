package com.lastdance.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lastdance.app.R
import com.lastdance.app.state.LastDanceViewModel
import com.lastdance.app.state.Screen
import com.lastdance.app.ui.components.pressable
import com.lastdance.app.ui.theme.LD
import com.lastdance.app.ui.theme.LDType
import com.lastdance.app.ui.theme.Radius

private data class Rec(val id: String, val title: String, val date: String, val km: String, val dur: String, val img: Int?, val kind: String, val fav: Boolean = false)

private val RECORDS = listOf(
    Rec("ld2026", "2026 Last Dance", "2026.11.15.", "14.9km", "2시간 36분", R.drawable.photo_lastdance, "Last Dance", true),
    Rec("jeju", "2023 제주도 여행", "2023.07.08.", "102km", "13시간 27분", R.drawable.photo_jeju, "1Day 주행기록"),
    Rec("gukbap", "친구들과 국밥투어", "2023.07.08.", "102km", "13시간 27분", R.drawable.photo_gukbap, "드라이브 스냅샷"),
    Rec("gunsan", "2023 군산여행", "2023.02.24.", "24km", "3시간 27분", R.drawable.photo_gunsan, "1Day 주행기록"),
    Rec("vlog", "시험날 등교길 브이로그", "2023.07.08.", "102km", "13시간 27분", R.drawable.photo_vlog, "스토리 숏폼"),
    Rec("jeju2", "2023 제주도 여행", "2023.07.08.", "102km", "13시간 27분", null, "1Day 주행기록"),
    Rec("geumjeong", "금정구 외 3곳 방문", "2025.06.08.", "34km", "2시간 30분", null, "1Day 주행기록"),
)
private val TABS = listOf("전체", "1Day 주행기록", "Last Dance", "드라이브 스냅샷", "스토리 숏폼", "즐겨찾기")

/** Figma 354:2737 — 갤러리 */
@Composable
fun GalleryScreen(vm: LastDanceViewModel) {
    var tab by rememberSaveable { mutableStateOf("전체") }
    var q by rememberSaveable { mutableStateOf("") }
    val list = RECORDS
        .filter { tab == "전체" || (tab == "즐겨찾기" && it.fav) || it.kind == tab }
        .filter { q.isBlank() || q in it.title || q in it.date }

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth().background(LD.bgElevated)) {
            Row(Modifier.fillMaxWidth().height(140.dp).padding(horizontal = 40.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(80.dp).pressable("뒤로") { vm.back() }, contentAlignment = Alignment.CenterStart) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LD.textPrimary, modifier = Modifier.size(44.dp))
                }
                Text("갤러리", style = LDType.titleLXBold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
                Icon(Icons.Default.MoreVert, "더보기", tint = LD.textPrimary, modifier = Modifier.size(48.dp))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                TABS.forEach { t ->
                    val on = t == tab
                    Column(Modifier.pressable(t, Role.Tab) { tab = t }.padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(t, style = if (on) LDType.titleSXBold else LDType.bodyLBold, color = if (on) LD.textPrimary else LD.textDisabled, modifier = Modifier.padding(vertical = 20.dp))
                        Box(Modifier.height(4.dp).width(if (on) 120.dp else 0.dp).background(LD.accent))
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 40.dp, end = 40.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier.width(733.dp).height(74.dp).clip(RoundedCornerShape(Radius.xxl)).background(LD.bgElevated).padding(horizontal = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f)) {
                    if (q.isEmpty()) Text("날짜나 기록명을 검색해 살펴보세요.", style = LDType.bodyLRegular, color = LD.textDisabled)
                    BasicTextField(q, { q = it }, textStyle = LDType.bodyLRegular.copy(color = LD.textPrimary), cursorBrush = SolidColor(LD.accent), singleLine = true, modifier = Modifier.fillMaxWidth())
                }
                Icon(Icons.Default.Search, null, tint = LD.textSecondary, modifier = Modifier.size(44.dp))
            }
            Box(Modifier.width(174.dp))
            Text("기록 전체 ▾", style = LDType.labelSMedium, modifier = Modifier.width(269.dp).clip(RoundedCornerShape(Radius.window)).background(LD.bgPressed).padding(20.dp))
        }
        LazyVerticalGrid(
            GridCells.Fixed(3),
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 40.dp, end = 40.dp, top = 34.dp, bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            items(list, key = { it.id }) { r -> RecordCard(r) { vm.go(Screen.Detail) } }
            if (tab == "전체" && q.isBlank()) item {
                Box(Modifier.height(401.dp).pressable("새 기록 만들기") { vm.go(Screen.Home) }, contentAlignment = Alignment.Center) {
                    Box(Modifier.size(207.dp).clip(RoundedCornerShape(50)).background(LD.bgSurface), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, null, tint = LD.bgPressed, modifier = Modifier.size(120.dp))
                    }
                }
            }
            if (list.isEmpty()) item(span = { GridItemSpan(3) }) {
                Text("조건에 맞는 기록이 없어요.", style = LDType.bodyLRegular, color = LD.textDisabled, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 120.dp))
            }
        }
    }
}

@Composable
private fun RecordCard(r: Rec, onClick: () -> Unit) {
    val shape = RoundedCornerShape(Radius.window)
    Box(Modifier.height(401.dp).clip(shape).background(Color(0xFF5D5D5D)).border(1.dp, LD.borderGallery, shape).pressable(r.title, onClick = onClick)) {
        if (r.img != null) Image(painterResource(r.img), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(182.dp).background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black))))
        Column(Modifier.align(Alignment.BottomStart).padding(start = 31.dp, bottom = 26.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(r.title, style = LDType.titleMBold)
                if (r.fav) Icon(Icons.Default.Star, "즐겨찾기", tint = LD.accent, modifier = Modifier.size(36.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(r.date, r.km, r.dur).forEach { Text(it, style = LDType.bodySRegular, color = LD.textSecondary) }
            }
        }
    }
}
