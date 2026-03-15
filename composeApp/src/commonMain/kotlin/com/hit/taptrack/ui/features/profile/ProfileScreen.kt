package com.hit.taptrack.ui.features.profile

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
import com.hit.taptrack.ui.shared.RmsScaffold

class ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ProfileScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        // Handle Logout Side Effect
        LaunchedEffect(uiState.isLoggedOut) {
            if (uiState.isLoggedOut) {
                // Gunakan replaceAll agar seluruh tumpukan navigasi dihapus
                // dan user tidak bisa menekan tombol back untuk masuk lagi
                navigator.replaceAll(LoginScreen())
            }
        }

        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                if (uiState.isLoading && uiState.profile == null) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (uiState.profile != null) {
                    if (maxWidth < 800.dp) {
                        ProfileCompact(uiState.profile!!, screenModel::onEvent) { navigator.pop() }
                    } else {
                        ProfileExpanded(uiState.profile!!, screenModel::onEvent) { navigator.pop() }
                    }
                }
            }
        }
    }
}