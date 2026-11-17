package com.hadijoe.projectandroid.ui.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hadijoe.projectandroid.model.SongOptions

@Composable
fun AddSongForm(
    title: String,
    onTitleChange: (String) -> Unit,
    songKey: String,
    onSongKeyChange: (String) -> Unit,
    difficulty: String,
    onDifficultyChange: (String) -> Unit,
    status: String,
    onStatusChange: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text("Title") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = songKey,
            onValueChange = onSongKeyChange,
            label = { Text("Key (e.g. Am)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OptionChips(
            label = "Difficulty",
            options = SongOptions.difficulties,
            selected = difficulty,
            onSelect = onDifficultyChange
        )
        OptionChips(
            label = "Status",
            options = SongOptions.statuses,
            selected = status,
            onSelect = onStatusChange
        )
        // The button stays disabled until both text fields are filled in.
        Button(
            onClick = onAddClick,
            enabled = title.isNotBlank() && songKey.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add song")
        }
    }
}

@Composable
private fun OptionChips(
    label: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (option in options) {
                FilterChip(
                    selected = option == selected,
                    onClick = { onSelect(option) },
                    label = { Text(option) }
                )
            }
        }
    }
}
