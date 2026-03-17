package com.hit.taptrack.ui.features.eventCreate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hit.taptrack.ui.features.eventCreate.components.CategoriesSection
import com.hit.taptrack.ui.features.eventCreate.components.CheckpointsSection
import com.hit.taptrack.ui.features.eventCreate.states.BasicInfoSection
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateEvent
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateUiState

// --- TAMPILAN MOBILE ---
@Composable
fun CreateEventCompact(uiState: EventCreateUiState, onEvent: (EventCreateEvent) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        BasicInfoSection(uiState, onEvent)
        CategoriesSection(uiState, onEvent)
        CheckpointsSection(uiState, onEvent)

        Button(
            onClick = { onEvent(EventCreateEvent.SubmitEvent) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
            else Text("SIMPAN ACARA", fontWeight = FontWeight.Bold)
        }
    }
}

// --- TAMPILAN DESKTOP ---
@Composable
fun CreateEventExpanded(uiState: EventCreateUiState, onEvent: (EventCreateEvent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            BasicInfoSection(uiState, onEvent)
        }
        Column(
            modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CategoriesSection(uiState, onEvent)
            CheckpointsSection(uiState, onEvent)

            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { onEvent(EventCreateEvent.SubmitEvent) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                else Text("SIMPAN ACARA", fontWeight = FontWeight.Bold)
            }
        }
    }
}