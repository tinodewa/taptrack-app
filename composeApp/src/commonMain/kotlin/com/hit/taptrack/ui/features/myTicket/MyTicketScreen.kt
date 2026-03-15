package com.hit.taptrack.ui.features.myTicket

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.myTicket.components.QrCodeDialog
import com.hit.taptrack.ui.features.myTicket.states.MyTicketsEvent
import com.hit.taptrack.ui.shared.RmsScaffold

class MyTicketsScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<MyTicketsScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tiket Balapan Saya",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (uiState.tickets.isEmpty()) {
                        Text(
                            text = "Anda belum mendaftar di acara manapun.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        if (maxWidth < 800.dp) {
                            MyTicketsCompact(uiState, screenModel::onEvent)
                        } else {
                            MyTicketsExpanded(uiState, screenModel::onEvent)
                        }
                    }
                }
            }
        }

        // Tampilkan Dialog QR jika ada tiket yang dipilih (Khusus Mobile/Compact)
        if (uiState.selectedTicket != null) {
            QrCodeDialog(

                ticket = uiState.selectedTicket!!,
                onDismiss = { screenModel.onEvent(MyTicketsEvent.SelectTicket(null)) }
            )
        }
    }
}