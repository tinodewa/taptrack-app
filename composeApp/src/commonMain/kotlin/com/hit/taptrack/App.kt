package com.hit.taptrack

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.hit.taptrack.ui.features.auth.login.LoginScreen
import com.hit.taptrack.ui.theme.TapTrackTheme

@Composable
@Preview
fun App() {
    TapTrackTheme {
        Navigator(LoginScreen()) { navigator ->
            // Add default animation (Slide)
            SlideTransition(navigator)
        }
    }
}