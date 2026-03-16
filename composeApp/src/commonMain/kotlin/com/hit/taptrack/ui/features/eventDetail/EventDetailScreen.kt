package com.hit.taptrack.ui.features.eventDetail

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import com.hit.taptrack.ui.features.leaderboard.LeaderboardScreen
import com.hit.taptrack.ui.features.eventManage.EventManageScreen
import com.hit.taptrack.ui.features.myTicket.MyTicketsScreen
import com.hit.taptrack.ui.shared.RmsScaffold
import org.koin.core.parameter.parametersOf

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
                            EventDetailCompact(
                                detail,
                                uiState,
                                screenModel::onEvent,
                                onNavigateToTickets = { navigator.push(MyTicketsScreen()) },
                                onNavigateToLeaderboard = { navigator.push(LeaderboardScreen(detail.id)) },
                                onNavigateToManageEvent = { navigator.push(EventManageScreen(detail.id)) },
                                onBackClick = { navigator.pop() },
                            )
                        } else {
                            EventDetailExpanded(
                                detail,
                                uiState,
                                screenModel::onEvent,
                                onNavigateToTickets = { navigator.push(MyTicketsScreen()) },
                                onNavigateToLeaderboard = { navigator.push(LeaderboardScreen(detail.id)) },
                                onNavigateToManageEvent = { navigator.push(EventManageScreen(detail.id)) },
                                onBackClick = { navigator.pop() }
                            )
                        }
                    }
                }
            }
        }
    }
}