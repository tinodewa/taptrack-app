package com.hit.taptrack.ui.features.auth.register

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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.auth.register.states.RegisterEvent
import com.hit.taptrack.ui.features.eventDiscovery.EventDiscoveryScreen
import com.hit.taptrack.ui.shared.RmsScaffold

class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val screenModel = koinScreenModel<RegisterScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        // Efek Sukses
        LaunchedEffect(uiState.isRegisterSuccess) {
            if (uiState.isRegisterSuccess) {
                // Tampilkan snackbar sebelum navigasi
                snackbarHostState.showSnackbar("Registrasi berhasil!")
                // Karena sudah di-login-kan otomatis oleh Repo, langsung tembak ke Event Discovery!
                navigator.replaceAll(EventDiscoveryScreen())
            }
        }

        // Efek Error
        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let { msg ->
                snackbarHostState.showSnackbar(msg)
                screenModel.onEvent(RegisterEvent.ClearError)
            }
        }

        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.surface,
            snackbarHostState = snackbarHostState
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (maxWidth < 800.dp) {
                    RegisterCompact(uiState, screenModel::onEvent) { navigator.pop() }
                } else {
                    RegisterExpanded(uiState, screenModel::onEvent) { navigator.pop() }
                }
            }
        }
    }
}