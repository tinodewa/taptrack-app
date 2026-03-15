package com.hit.taptrack.ui.features.eventDiscovery

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hit.taptrack.data.model.EventModel
import com.hit.taptrack.ui.features.eventDiscovery.components.EventCard

// --- TAMPILAN MOBILE (List 1 Kolom) ---
@Composable
fun EventDiscoveryCompact(events: List<EventModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(events) { event ->
            EventCard(event = event, onClick = { /* TODO: Navigasi ke Detail Event */ })
        }
    }
}

// --- TAMPILAN DESKTOP (Grid Banyak Kolom) ---
@Composable
fun EventDiscoveryExpanded(events: List<EventModel>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 350.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(events) { event ->
            EventCard(event = event, onClick = { /* TODO: Navigasi ke Detail Event */ })
        }
    }
}