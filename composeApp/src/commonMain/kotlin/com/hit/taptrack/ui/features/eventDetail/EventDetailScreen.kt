package com.hit.taptrack.ui.features.eventDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.shared.RmsScaffold
import org.koin.core.parameter.parametersOf // Penting untuk inject parameter

// Kita tambahkan parameter eventId di class Screen
data class EventDetailScreen(val eventId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }

        // Inject ScreenModel dan berikan parameter eventId
        val screenModel = koinScreenModel<EventDetailScreenModel> { parametersOf(eventId) }
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.isJoinSuccess) {
            if (uiState.isJoinSuccess) {
                snackbarHostState.showSnackbar("Berhasil mendaftar! Cek tiket Anda.")
                // TODO: Bisa diarahkan ke halaman "My Events / Tiket"
            }
        }

        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.background,
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    uiState.eventDetail?.let { detail ->
                        if (maxWidth < 800.dp) {
                            EventDetailCompact(detail, uiState, screenModel::onEvent) { navigator.pop() }
                        } else {
                            EventDetailExpanded(detail, uiState, screenModel::onEvent) { navigator.pop() }
                        }
                    }
                }
            }
        }
    }
}