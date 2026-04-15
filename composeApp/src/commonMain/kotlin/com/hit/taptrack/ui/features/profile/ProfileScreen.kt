package com.hit.taptrack.ui.features.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.auth.login.LoginScreen
import com.hit.taptrack.ui.features.initial.landing.LandingScreen
import com.hit.taptrack.ui.shared.RmsScaffold
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ProfileScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        // Handle Logout Side Effect
        LaunchedEffect(uiState.isLoggedOut) {
            if (uiState.isLoggedOut) {
                // Gunakan replaceAll dan arahkan ke LandingScreen sesuai alur terbaru kita
                navigator.replaceAll(LandingScreen())
            }
        }

        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { screenModel.onEvent(com.hit.taptrack.ui.features.profile.states.ProfileEvent.RefreshProfile) },
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (uiState.isLoading && !uiState.isRefreshing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.profile != null) {
                        if (maxWidth < 800.dp) {
                            ProfileCompact(
                                uiState.profile!!,
                                screenModel::onEvent
                            ) { navigator.pop() }
                        } else {
                            ProfileExpanded(
                                uiState.profile!!,
                                screenModel::onEvent
                            ) { navigator.pop() }
                        }
                    }
                }
            }
        }
    }
}