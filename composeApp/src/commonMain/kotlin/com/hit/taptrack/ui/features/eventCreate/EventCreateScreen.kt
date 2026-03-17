package com.hit.taptrack.ui.features.eventCreate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateEvent
import com.hit.taptrack.ui.shared.RmsScaffold

class EventCreateScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val screenModel = koinScreenModel<EventCreateScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.isSuccess) {
            if (uiState.isSuccess) {
                snackbarHostState.showSnackbar("Acara berhasil dibuat!")
                navigator.pop() // Kembali ke Dashboard setelah sukses
            }
        }
        LaunchedEffect(uiState.errorMessage) {
            uiState.errorMessage?.let {
                snackbarHostState.showSnackbar(it)
                screenModel.onEvent(EventCreateEvent.ClearError)
            }
        }

        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.background,
            snackbarHostState = snackbarHostState
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buat Acara Balapan", style = MaterialTheme.typography.titleLarge)
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    if (maxWidth < 800.dp) {
                        CreateEventCompact(uiState, screenModel::onEvent)
                    } else {
                        CreateEventExpanded(uiState, screenModel::onEvent)
                    }
                }
            }
        }
    }
}