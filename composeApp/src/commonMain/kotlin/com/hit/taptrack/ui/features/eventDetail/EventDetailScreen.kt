package com.hit.taptrack.ui.features.eventDetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailEvent
import com.hit.taptrack.ui.features.leaderboard.LeaderboardScreen
import com.hit.taptrack.ui.features.eventManage.EventManageScreen
import com.hit.taptrack.ui.features.myTicket.MyTicketsScreen
import com.hit.taptrack.ui.shared.RmsScaffold
import org.koin.core.parameter.parametersOf
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

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
                snackbarHostState.showSnackbar(uiState.joinSuccessMessage ?: "Berhasil mendaftar!")
                // Opsional: Langsung arahkan user ke halaman MyTicketsScreen
                // navigator.push(MyTicketsScreen())
            }
        }

        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.background,
            snackbarHostState = snackbarHostState
        ) { innerPadding ->
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { screenModel.onEvent(EventDetailEvent.RefreshData) },
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isLoading && !uiState.isRefreshing) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        uiState.eventDetail?.let { detail ->
                            if (maxWidth < 800.dp) {
                                EventDetailCompact(
                                    detail,
                                    uiState,
                                    screenModel::onEvent,
                                    onNavigateToTickets = { navigator.push(MyTicketsScreen()) },
                                    onNavigateToLeaderboard = {
                                        navigator.push(
                                            LeaderboardScreen(
                                                detail.id
                                            )
                                        )
                                    },
                                    onNavigateToManageEvent = {
                                        navigator.push(
                                            EventManageScreen(
                                                detail.id
                                            )
                                        )
                                    },
                                    onBackClick = { navigator.pop() },
                                )
                            } else {
                                EventDetailExpanded(
                                    detail,
                                    uiState,
                                    screenModel::onEvent,
                                    onNavigateToTickets = { navigator.push(MyTicketsScreen()) },
                                    onNavigateToLeaderboard = {
                                        navigator.push(
                                            LeaderboardScreen(
                                                detail.id
                                            )
                                        )
                                    },
                                    onNavigateToManageEvent = {
                                        navigator.push(
                                            EventManageScreen(
                                                detail.id
                                            )
                                        )
                                    },
                                    onBackClick = { navigator.pop() }
                                )
                            }
                        }
                    }
                }
            }

            // Pop-up Dialog Input Nomor Lambung
            if (uiState.showJoinDialog) {
                AlertDialog(
                    onDismissRequest = { screenModel.onEvent(EventDetailEvent.HideJoinDialog) },
                    title = { Text("Pilih Nomor Lambung") },
                    text = {
                        OutlinedTextField(
                            value = uiState.raceNumberInput,
                            onValueChange = {
                                screenModel.onEvent(
                                    EventDetailEvent.RaceNumberChanged(
                                        it
                                    )
                                )
                            },
                            label = { Text("Nomor Start (Cth: 99)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { screenModel.onEvent(EventDetailEvent.SubmitJoinEvent) },
                            enabled = !uiState.isJoinLoading && uiState.raceNumberInput.isNotBlank()
                        ) {
                            if (uiState.isJoinLoading) CircularProgressIndicator(
                                modifier = Modifier.size(
                                    20.dp
                                )
                            )
                            else Text("KONFIRMASI")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { screenModel.onEvent(EventDetailEvent.HideJoinDialog) },
                            enabled = !uiState.isJoinLoading
                        ) {
                            Text("Batal")
                        }
                    }
                )
            }
        }
    }
}