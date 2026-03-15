package com.hit.taptrack.ui.features.eventDiscovery

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.hit.taptrack.ui.features.eventDetail.EventDetailScreen
import com.hit.taptrack.ui.features.eventDiscovery.components.DiscoveryHeader
import com.hit.taptrack.ui.features.eventDiscovery.components.DiscoveryTabs
import com.hit.taptrack.ui.features.eventDiscovery.states.EventDiscoveryEvent
import com.hit.taptrack.ui.features.myTicket.MyTicketsScreen
import com.hit.taptrack.ui.features.profile.ProfileScreen
import com.hit.taptrack.ui.shared.RmsScaffold

class EventDiscoveryScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<EventDiscoveryScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        RmsScaffold(
            backgroundColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Header Global (App Bar Custom)
                DiscoveryHeader(
                    { navigator.push(MyTicketsScreen()) },
                    { navigator.push(ProfileScreen()) }
                )

                // Tab Navigasi Sesuai SKPL (Mendatang, Berlangsung, Selesai)
                DiscoveryTabs(
                    selectedTabIndex = uiState.selectedTabIndex,
                    onTabSelected = { screenModel.onEvent(EventDiscoveryEvent.TabSelected(it)) }
                )

                // Render area list responsif
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (uiState.filteredEvents.isEmpty()) {
                        Text(
                            text = "Tidak ada acara dalam kategori ini.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        if (maxWidth < 800.dp) {
                            EventDiscoveryCompact(
                                uiState.filteredEvents,
                                onEventClick = { event ->
                                    navigator.push(EventDetailScreen(event.id))
                                }
                            )
                        } else {
                            EventDiscoveryExpanded(
                                uiState.filteredEvents,
                                onEventClick = { event ->
                                    navigator.push(EventDetailScreen(event.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}