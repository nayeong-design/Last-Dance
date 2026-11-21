package com.lastdance.app.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lastdance.app.pleos.CameraId
import com.lastdance.app.pleos.Pleos
import com.lastdance.app.pleos.Reaction
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** 웹 `src/state/store.tsx`의 reducer를 그대로 옮긴 상태. */
enum class Screen { Home, Setup, Plan, Checklist, Driving, Camera, Gallery, Detail }

data class ChecklistItem(val id: String, val title: String, val place: String)

val CHECKLIST = listOf(
    ChecklistItem("wash", "마지막 세차", "베스트 세차장"),
    ChecklistItem("charge", "마지막 충전", "그린파워 충전소"),
    ChecklistItem("dt", "드라이브 스루 즐기기", "모닝브루 DT"),
    ChecklistItem("cafe", "단골 카페 방문하기", "카페 이오"),
    ChecklistItem("course", "좋아하는 드라이브 코스 달리기", "명암 저수지"),
)

val REASONS = listOf("중고 차량 판매", "신차 구매", "폐차", "가족에게 양도", "직접입력")

data class Farewell(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null,
    val reason: String = "직접입력",
    val memo: String = "",
    val registered: Boolean = false,
) {
    val isValid get() = year != null && month != null && day != null && (reason != "직접입력" || memo.isNotBlank())
    val dateLabel get() = if (year == null) "" else "%d-%02d-%02d".format(year, month ?: 1, day ?: 1)
    val reasonLabel get() = if (reason == "직접입력") memo else reason
}

data class Recording(val active: Boolean = false, val paused: Boolean = false, val elapsed: Int = 0, val snapshots: Int = 0)

data class UiState(
    val screen: Screen = Screen.Home,
    val history: List<Screen> = emptyList(),
    val farewell: Farewell = Farewell(),
    val selected: Set<String> = setOf("wash"),
    val recording: Recording = Recording(),
    val notifying: Boolean = false,
    val reactions: List<Reaction> = emptyList(),
    val cameraId: CameraId = CameraId.FrontCenter,
    val toast: String? = null,
)

class LastDanceViewModel : ViewModel() {
    private val _s = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _s

    private var ticker: Job? = null
    private var broadcast: Job? = null
    private var toastJob: Job? = null

    fun go(to: Screen) = _s.update { if (it.screen == to) it else it.copy(screen = to, history = it.history + it.screen) }
    fun back() = _s.update { it.copy(screen = it.history.lastOrNull() ?: Screen.Home, history = it.history.dropLast(1)) }

    fun updateFarewell(f: (Farewell) -> Farewell) = _s.update { it.copy(farewell = f(it.farewell)) }

    /** 등록하기 — 검증 실패 시 토스트만 띄운다 (엣지 케이스) */
    fun register() {
        val f = _s.value.farewell
        if (!f.isValid) { toast("차량 교체 일자와 이별 사유를 입력해주세요"); return }
        updateFarewell { it.copy(registered = true) }
        go(Screen.Plan)
    }

    fun toggle(id: String) = _s.update { it.copy(selected = if (id in it.selected) it.selected - id else it.selected + id) }

    fun startRecording() {
        _s.update { it.copy(recording = Recording(active = true), reactions = emptyList(), notifying = false) }
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (true) {
                delay(1000)
                _s.update { s -> if (s.recording.active && !s.recording.paused) s.copy(recording = s.recording.copy(elapsed = s.recording.elapsed + 1)) else s }
            }
        }
    }

    fun togglePause() = _s.update { it.copy(recording = it.recording.copy(paused = !it.recording.paused)) }

    fun stopRecording() {
        ticker?.cancel(); broadcast?.cancel()
        _s.update { it.copy(recording = it.recording.copy(active = false), notifying = false) }
        go(Screen.Detail)
    }

    fun snapshot() = _s.update { it.copy(recording = it.recording.copy(snapshots = it.recording.snapshots + 1)) }

    fun setNotify(on: Boolean) {
        broadcast?.cancel()
        _s.update { it.copy(notifying = on, reactions = if (on) it.reactions else emptyList()) }
        if (on) broadcast = viewModelScope.launch {
            Pleos.current.adas.broadcastFarewell().collect { r ->
                _s.update { s -> if (r in s.reactions) s else s.copy(reactions = s.reactions + r) }
            }
        }
    }

    fun selectCamera(id: CameraId) = _s.update { it.copy(cameraId = id) }

    fun askGleo() = viewModelScope.launch { toast(Pleos.current.gleo.ask("default")) }

    fun toast(msg: String) {
        toastJob?.cancel()
        _s.update { it.copy(toast = msg) }
        toastJob = viewModelScope.launch { delay(2600); _s.update { it.copy(toast = null) } }
    }
}

fun fmtTime(sec: Int) = "%02d:%02d:%02d".format(sec / 3600, (sec % 3600) / 60, sec % 60)
