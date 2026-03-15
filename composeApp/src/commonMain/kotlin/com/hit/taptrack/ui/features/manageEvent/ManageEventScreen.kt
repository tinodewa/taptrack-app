package com.hit.taptrack.ui.features.manageEvent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.scan.ScanScreen
import com.hit.taptrack.ui.shared.RmsScaffold
import org.koin.core.parameter.parametersOf

data class ManageEventScreen(val eventId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ManageEventScreenModel> { parametersOf(eventId) }
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        RmsScaffold(backgroundColor = MaterialTheme.colorScheme.background) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                // Header Dashboard
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
                    Column {
                        Text(
                            "PUSAT KOMANDO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            uiState.eventName,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        // Sekarang maxWidth sudah bisa diakses dari BoxWithConstraintsScope
                        if (maxWidth < 800.dp) {
                            ManageEventCompact(uiState, screenModel::onEvent) {
                                navigator.push(
                                    ScanScreen(eventId)
                                )
                            }
                        } else {
                            ManageEventExpanded(uiState, screenModel::onEvent) {
                                navigator.push(
                                    ScanScreen(eventId)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}