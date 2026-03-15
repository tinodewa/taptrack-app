package com.hit.taptrack.ui.features.leaderboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hit.taptrack.data.model.LeaderboardEntryModel

@Composable
fun LeaderboardTable(entries: List<LeaderboardEntryModel>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Table Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("POS", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.width(40.dp))
            Text("PEMBALAP", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.weight(1f))
            Text("WAKTU / GAP", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.width(100.dp), textAlign = TextAlign.End)
        }

        Divider(color = Color.Gray.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(8.dp))

        // Table Rows
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(entries) { entry ->
                LeaderboardRow(entry)
            }
        }
    }
}