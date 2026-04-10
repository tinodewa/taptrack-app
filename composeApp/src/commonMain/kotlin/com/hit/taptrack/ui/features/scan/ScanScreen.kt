package com.hit.taptrack.ui.features.scan

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.scan.components.ScanHeader
import com.hit.taptrack.ui.features.scan.components.ScanResultBanner
import com.hit.taptrack.ui.features.scan.states.ScanEvent
import com.hit.taptrack.ui.shared.RmsScaffold
import org.koin.core.parameter.parametersOf

data class ScanScreen(val eventId: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ScanScreenModel> { parametersOf(eventId) }
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.background,

        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                // Header
                ScanHeader(
                    onBackClick = { navigator.pop() },
                    actions = {
                        // Tombol Sync & Badge Antrean
                        Box {
                            IconButton(
                                onClick = { screenModel.onEvent(ScanEvent.SyncPendingScans) },
                                enabled = !uiState.isLoading // Nonaktifkan saat sedang loading
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudUpload,
                                    contentDescription = "Sinkronisasi Data"
                                )
                            }

                            // Tampilkan Badge merah jika ada data pending
                            if (uiState.pendingSyncCount > 0) {
                                Badge(modifier = Modifier.align(Alignment.TopEnd)) {
                                    Text("${uiState.pendingSyncCount}")
                                }
                            }
                        }
                    }
                )

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    if (maxWidth < 800.dp) {
                        ScanCompact(uiState, screenModel::onEvent)
                    } else {
                        ScanExpanded(uiState, screenModel::onEvent)
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter) // Pindahkan align ke sini
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        androidx.compose.animation.AnimatedVisibility( // Gunakan pemanggilan eksplisit jika IDE masih rewel
                            visible = uiState.scanResult != null,
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            uiState.scanResult?.let { result ->
                                ScanResultBanner(
                                    result = result,
                                    onDismiss = { screenModel.onEvent(ScanEvent.DismissResult) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}