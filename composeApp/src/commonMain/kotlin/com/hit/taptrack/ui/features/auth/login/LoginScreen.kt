package com.hit.taptrack.ui.features.auth.login

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.auth.login.states.LoginEvent
import com.hit.taptrack.ui.features.eventDiscovery.EventDiscoveryScreen
import com.hit.taptrack.ui.shared.RmsScaffold

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }

        // 1. Inject Model
        val screenModel = koinScreenModel<LoginScreenModel>()

        // 2. Collect State Global secara responsif terhadap Lifecycle
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        // 3. Handle Side Effects (Navigasi)
        LaunchedEffect(uiState.isLoginSuccess) {
            if (uiState.isLoginSuccess) {
                navigator.pop()
                // TODO: Arahkan user berdasarkan peran (Role) yang didapat
                navigator.replace(EventDiscoveryScreen())
            }
        }

        // 4. Handle Side Effects (Snackbar Error)
        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg)
                screenModel.onEvent(LoginEvent.ClearError) // Reset state agar tidak loop
            }
        }

        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.surface,
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (maxWidth < 800.dp) {
                    LoginCompact(
                        uiState = uiState,
                        onEvent = screenModel::onEvent,
                        onBackClick = { navigator.pop() }
                    )
                } else {
                    LoginExpanded(
                        uiState = uiState,
                        onEvent = screenModel::onEvent,
                        onBackClick = { navigator.pop() }
                    )
                }
            }
        }
    }
}