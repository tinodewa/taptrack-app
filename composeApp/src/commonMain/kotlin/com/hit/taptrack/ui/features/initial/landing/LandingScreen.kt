package com.hit.taptrack.ui.features.initial.landing

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.auth.login.LoginScreen
import com.hit.taptrack.ui.features.auth.register.RegisterScreen
import com.hit.taptrack.ui.shared.RmsScaffold

class LandingScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        RmsScaffold(backgroundColor = MaterialTheme.colorScheme.background) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (maxWidth < 800.dp) {
                    LandingCompact(
                        onLoginClick = { navigator.push(LoginScreen()) }, // Asumsi kamu punya LoginScreen
                        onRegisterClick = { navigator.push(RegisterScreen()) } // Asumsi kamu punya RegisterScreen
                    )
                } else {
                    LandingExpanded(
                        onLoginClick = { navigator.push(LoginScreen()) },
                        onRegisterClick = { navigator.push(RegisterScreen()) }
                    )
                }
            }
        }
    }
}