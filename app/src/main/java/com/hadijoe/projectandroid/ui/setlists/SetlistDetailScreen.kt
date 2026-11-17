package com.hadijoe.projectandroid.ui.setlists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hadijoe.projectandroid.model.Song
import com.hadijoe.projectandroid.ui.theme.ProjectAndroidTheme

@Composable
fun SetlistDetailScreen(
    setlistId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SetlistDetailViewModel = viewModel(factory = SetlistDetailViewModel.factory(setlistId))
) {
    val setlist by viewModel.setlist.collectAsStateWithLifecycle()
    val songsInSetlist by viewModel.songsInSetlist.collectAsStateWithLifecycle()
    val availableSongs by viewModel.availableSongs.collectAsStateWithLifecycle()

    SetlistDetailContent(
        // The setlist is null until the database answers, so show an empty name meanwhile.
        setlistName = setlist?.name ?: "",
        songsInSetlist = songsInSetlist,
        availableSongs = availableSongs,
        onAddSong = viewModel::addSong,
        onRemoveSong = viewModel::removeSong,
        onMoveSongUp = viewModel::moveSongUp,
        onMoveSongDown = viewModel::moveSongDown,
        onBack = onBack,
        modifier = modifier
    )
}

// Stateless, so it can be previewed without a ViewModel.
@Composable
fun SetlistDetailContent(
    setlistName: String,
    songsInSetlist: List<Song>,
    availableSongs: List<Song>,
    onAddSong: (Long) -> Unit,
    onRemoveSong: (Long) -> Unit,
    onMoveSongUp: (Long) -> Unit,
    onMoveSongDown: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) {
                Text("Back")
            }
            Text(setlistName, style = MaterialTheme.typography.titleLarge)
        }

        // Each half gets weight(1f), so the two lists share the height and scroll separately.
        Text("Songs in this setlist", style = MaterialTheme.typography.titleMedium)
        if (songsInSetlist.isEmpty()) {
            Text("No songs in this setlist yet.", modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // The list arrives already sorted by position from the database.
                // itemsIndexed also gives the index, to know which row is first and which is last.
                // The key is still the song id: when two rows swap, Compose moves them instead of rebuilding them.
                itemsIndexed(items = songsInSetlist, key = { _, song -> song.id }) { index, song ->
                    SetlistSongRow(
                        song = song,
                        canMoveUp = index > 0,
                        canMoveDown = index < songsInSetlist.lastIndex,
                        onMoveUp = { onMoveSongUp(song.id) },
                        onMoveDown = { onMoveSongDown(song.id) },
                        onRemove = { onRemoveSong(song.id) }
                    )
                }
            }
        }

        Text("Add a saved song", style = MaterialTheme.typography.titleMedium)
        if (availableSongs.isEmpty()) {
            Text("No more saved songs to add.", modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = availableSongs, key = { it.id }) { song ->
                    AvailableSongRow(song, onAdd = { onAddSong(song.id) })
                }
            }
        }
    }
}

@Composable
fun SetlistSongRow(
    song: Song,
    canMoveUp: Boolean,
    canMoveDown: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // weight(1f): the text takes the free space and pushes the button to the right.
            Column(Modifier.weight(1f)) {
                Text(text = song.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Key: ${song.songKey}  •  ${song.difficulty}  •  ${song.status}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // Disabled on the first / last row: there is nowhere to move.
            TextButton(
                onClick = onMoveUp,
                enabled = canMoveUp,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Up")
            }
            TextButton(
                onClick = onMoveDown,
                enabled = canMoveDown,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Down")
            }
            TextButton(onClick = onRemove, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text("Remove")
            }
        }
    }
}

@Composable
fun AvailableSongRow(song: Song, onAdd: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(text = song.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Key: ${song.songKey}  •  ${song.difficulty}  •  ${song.status}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            TextButton(onClick = onAdd) {
                Text("Add")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetlistDetailContentPreview() {
    ProjectAndroidTheme {
        SetlistDetailContent(
            setlistName = "Friday gig",
            songsInSetlist = previewSongsInSetlist,
            availableSongs = previewAvailableSongs,
            onAddSong = {},
            onRemoveSong = {},
            onMoveSongUp = {},
            onMoveSongDown = {},
            onBack = {}
        )
    }
}

// Fake data, only for the preview.
private val previewSongsInSetlist = listOf(
    Song(1, "Morning Blues", "C", "Easy", "Ready"),
    Song(2, "Slow Waltz in G", "G", "Medium", "Practicing")
)

private val previewAvailableSongs = listOf(
    Song(3, "Rainy Day Rag", "Am", "Hard", "Learning")
)
