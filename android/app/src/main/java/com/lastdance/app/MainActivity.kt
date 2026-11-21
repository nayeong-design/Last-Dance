package com.lastdance.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lastdance.app.state.LastDanceViewModel
import com.lastdance.app.state.Screen
import com.lastdance.app.ui.components.LDToast
import com.lastdance.app.ui.screens.ChecklistScreen
import com.lastdance.app.ui.screens.DetailScreen
import com.lastdance.app.ui.screens.DrivingScreen
import com.lastdance.app.ui.screens.FarewellPlanScreen
import com.lastdance.app.ui.screens.FarewellSetupScreen
import com.lastdance.app.ui.screens.GalleryScreen
import com.lastdance.app.ui.screens.HomeScreen
import com.lastdance.app.ui.theme.LD
import com.lastdance.app.ui.theme.LastDanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { LastDanceTheme { LastDanceApp() } }
    }
}

@Composable
fun LastDanceApp(vm: LastDanceViewModel = viewModel()) {
    val s by vm.state.collectAsStateWithLifecycle()
    BackHandler(enabled = s.history.isNotEmpty()) { vm.back() }

    val surface = when (s.screen) {
        Screen.Home, Screen.Checklist -> LD.bgBase
        else -> LD.bgSurface
    }
    Box(Modifier.fillMaxSize().background(surface)) {
        // Driving ⇄ Camera는 같은 스플릿 뷰 안에서 패널만 바뀌므로 하나의 키로 묶는다.
        val key = if (s.screen == Screen.Camera) Screen.Driving else s.screen
        AnimatedContent(key, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "screen") { screen ->
            when (screen) {
                Screen.Home -> HomeScreen(vm)
                Screen.Setup -> FarewellSetupScreen(s, vm)
                Screen.Plan -> FarewellPlanScreen(s, vm)
                Screen.Checklist -> ChecklistScreen(s, vm)
                Screen.Driving, Screen.Camera -> DrivingScreen(s, vm)
                Screen.Gallery -> GalleryScreen(vm)
                Screen.Detail -> DetailScreen(s, vm)
            }
        }
        LDToast(s.toast)
    }
}
