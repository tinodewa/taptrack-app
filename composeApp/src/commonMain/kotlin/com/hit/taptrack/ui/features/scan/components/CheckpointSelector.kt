package com.hit.taptrack.ui.features.scan.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hit.taptrack.common.state.CheckpointType
import com.hit.taptrack.ui.features.scan.states.CheckpointUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckpointSelector(
    checkpoints: List<CheckpointUiModel>,
    selected: CheckpointUiModel?,
    onSelect: (CheckpointUiModel) -> Unit
) {
    if (checkpoints.isEmpty()) {
        Text("Memuat daftar pos...", style = MaterialTheme.typography.bodySmall)
        return
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(checkpoints) { checkpoint ->
            FilterChip(
                selected = selected?.id == checkpoint.id,
                onClick = { onSelect(checkpoint) },
                label = { Text(checkpoint.name) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}