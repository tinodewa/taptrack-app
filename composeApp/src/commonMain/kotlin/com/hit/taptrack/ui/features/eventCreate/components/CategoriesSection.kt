package com.hit.taptrack.ui.features.eventCreate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateEvent
import com.hit.taptrack.ui.features.eventCreate.states.EventCreateUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoriesSection(uiState: EventCreateUiState, onEvent: (EventCreateEvent) -> Unit) {
    var newCategory by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Kelas Balap",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.categories.forEachIndexed { index, category ->
                    InputChip(
                        selected = true,
                        onClick = { },
                        label = {
                            Text(
                            category,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        },
                        trailingIcon = {
                            if (uiState.categories.size > 1) { // Jangan izinkan hapus jika sisa 1
                                IconButton(
                                    onClick = { onEvent(EventCreateEvent.RemoveCategory(index)) },
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Hapus",
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        },
                        colors = InputChipDefaults.inputChipColors(selectedContainerColor = MaterialTheme.colorScheme.secondary)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = newCategory,
                    onValueChange = { newCategory = it },
                    placeholder = { Text("Tambah Kelas Baru...", style = MaterialTheme.typography.labelLarge) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.labelLarge,
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (newCategory.isNotBlank()) {
                            onEvent(EventCreateEvent.AddCategory(newCategory))
                            newCategory = ""
                        }
                    },
                    modifier = Modifier.size(48.dp)
                        .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                ) { Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color.White) }
            }
        }
    }
}