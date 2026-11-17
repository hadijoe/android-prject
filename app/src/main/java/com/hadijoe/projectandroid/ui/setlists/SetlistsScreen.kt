package com.hadijoe.projectandroid.ui.setlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hadijoe.projectandroid.model.Setlist
import com.hadijoe.projectandroid.ui.theme.ProjectAndroidTheme

@Composable
fun SetlistsScreen(
    onSetlistClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SetlistsViewModel = viewModel(factory = SetlistsViewModel.Factory)
) {
    val setlists by viewModel.setlists.collectAsStateWithLifecycle()
    val newName by viewModel.newName.collectAsStateWithLifecycle()

    SetlistsContent(
        setlists = setlists,
        newName = newName,
        onNewNameChange = viewModel::onNewNameChange,
        onAddClick = viewModel::addSetlist,
        onSetlistClick = onSetlistClick,
        modifier = modifier
    )
}

// Stateless, so it can be previewed without a ViewModel.
@Composable
fun SetlistsContent(
    setlists: List<Setlist>,
    newName: String,
    onNewNameChange: (String) -> Unit,
    onAddClick: () -> Unit,
    onSetlistClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = newName,
            onValueChange = onNewNameChange,
            label = { Text("New setlist name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onAddClick,
            enabled = newName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add setlist")
        }

        if (setlists.isEmpty()) {
            Text("No setlists yet.")
        } else {
            LazyColumn(
                // Takes the remaining height so the list can scroll.
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = setlists, key = { it.id }) { setlist ->
                    SetlistRow(setlist, onClick = { onSetlistClick(setlist.id) })
                }
            }
        }
    }
}

@Composable
fun SetlistRow(setlist: Setlist, onClick: () -> Unit, modifier: Modifier = Modifier) {
    // This Card version takes onClick, so the whole card is clickable.
    Card(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Text(
            text = setlist.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SetlistsContentPreview() {
    ProjectAndroidTheme {
        SetlistsContent(
            setlists = previewSetlists,
            newName = "",
            onNewNameChange = {},
            onAddClick = {},
            onSetlistClick = {}
        )
    }
}

// Fake data, only for the preview.
private val previewSetlists = listOf(
    Setlist(1, "Friday gig"),
    Setlist(2, "Sunday practice")
)
