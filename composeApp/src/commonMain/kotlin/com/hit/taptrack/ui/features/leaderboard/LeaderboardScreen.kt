package com.hit.taptrack.ui.features.leaderboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hit.taptrack.ui.features.leaderboard.components.CategoryFilterRow
import com.hit.taptrack.ui.features.leaderboard.components.LeaderboardHeader
import com.hit.taptrack.ui.features.leaderboard.components.LeaderboardTable
import com.hit.taptrack.ui.features.leaderboard.states.LeaderboardEvent
import com.hit.taptrack.ui.shared.RmsScaffold
import org.koin.core.parameter.parametersOf
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.pulltorefresh.PullToRefreshBox

data class LeaderboardScreen(val eventId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<LeaderboardScreenModel> { parametersOf(eventId) }
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                // Header Papan Peringkat
                LeaderboardHeader(
                    eventName = uiState.eventName,
                    onBackClick = { navigator.pop() },
                    onRefreshClick = { screenModel.onEvent(LeaderboardEvent.RefreshLeaderboard) }
                )

                // Filter Kategori (Kelas Balap)
                CategoryFilterRow(
                    categories = uiState.categories,
                    selectedCategory = uiState.currentCategory,
                    onCategorySelected = { screenModel.onEvent(LeaderboardEvent.ChangeCategory(it)) }
                )
                PullToRefreshBox(
                    isRefreshing = uiState.isRefreshing,
                    onRefresh = { screenModel.onEvent(LeaderboardEvent.RefreshLeaderboard) },
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
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
                            LeaderboardTable(entries = uiState.entries)
                        }
                    }
                }
            }
        }
    }
}