package com.hit.taptrack.ui.features.eventDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.hit.taptrack.data.model.EventDetailModel
import com.hit.taptrack.ui.features.eventDetail.components.CategoryChip
import com.hit.taptrack.ui.features.eventDetail.components.DynamicActionButton
import com.hit.taptrack.ui.features.eventDetail.components.InfoRow
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailEvent
import com.hit.taptrack.ui.features.eventDetail.states.EventDetailUiState
import com.hit.taptrack.ui.features.eventDiscovery.components.StatusBadge

// --- TAMPILAN MOBILE ---
@Composable
fun EventDetailCompact(
    detail: EventDetailModel,
    uiState: EventDetailUiState,
    onEvent: (EventDetailEvent) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToTickets: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp) // Ruang untuk sticky button di bawah
        ) {
            // Header Image Placeholder (Sporty Gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                        )
                    )
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
                }
                StatusBadge(status = detail.status, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp))
            }

            // Konten Detail
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = detail.name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
                Spacer(modifier = Modifier.height(16.dp))

                InfoRow(icon = Icons.Default.CalendarToday, text = detail.date)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = Icons.Default.LocationOn, text = detail.location)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = Icons.Default.Group, text = "${detail.registeredRacers} / ${detail.maxCapacity} Peserta Terdaftar")

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Deskripsi", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = detail.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f))

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Kelas Tersedia", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                detail.categories.forEach { category ->
                    CategoryChip(category)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Regulasi", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = detail.rules, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
            }
        }

        // Sticky Bottom Action Bar
        Surface(
            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                DynamicActionButton(uiState, onEvent, onNavigateToTickets)
            }
        }
    }
}

// --- TAMPILAN DESKTOP ---
@Composable
fun EventDetailExpanded(
    detail: EventDetailModel,
    uiState: EventDetailUiState,
    onEvent: (EventDetailEvent) -> Unit,
    onBackClick: () -> Unit,
    onNavigateToTickets: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        // Kiri: Hero & Info Utama
        Card(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp).background(Brush.verticalGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))))) {
                    IconButton(onClick = onBackClick, modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
                Column(modifier = Modifier.padding(32.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = detail.name, style = MaterialTheme.typography.displaySmall, modifier = Modifier.weight(1f))
                        StatusBadge(status = detail.status, modifier = Modifier.padding(start = 16.dp))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    InfoRow(icon = Icons.Default.CalendarToday, text = detail.date)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(icon = Icons.Default.LocationOn, text = detail.location)
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow(icon = Icons.Default.Group, text = "${detail.registeredRacers} / ${detail.maxCapacity} Terdaftar")

                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = "Deskripsi", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = detail.description, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        // Kanan: Kategori, Regulasi, dan Tombol
        Card(
            modifier = Modifier.weight(0.6f).fillMaxHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(32.dp).fillMaxHeight()) {
                Text(text = "Kelas Tersedia", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))
                detail.categories.forEach { CategoryChip(it) }

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "Regulasi Khusus", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = detail.rules, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)

                Spacer(modifier = Modifier.weight(1f)) // Dorong tombol ke paling bawah

                DynamicActionButton(uiState, onEvent, onNavigateToTickets)
            }
        }
    }
}