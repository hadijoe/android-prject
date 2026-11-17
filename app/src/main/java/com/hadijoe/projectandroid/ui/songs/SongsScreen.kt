package com.hadijoe.projectandroid.ui.songs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hadijoe.projectandroid.model.Song
import com.hadijoe.projectandroid.ui.theme.ProjectAndroidTheme

@Composable
fun SongsScreen(
    modifier: Modifier = Modifier,
    viewModel: SongsViewModel = viewModel(factory = SongsViewModel.Factory)
) {
    // viewModel() returns the same instance after a rotation.
    // collectAsStateWithLifecycle turns the StateFlow into Compose state and stops collecting when the screen is not visible.
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val form by viewModel.form.collectAsStateWithLifecycle()

    SongsContent(
        songs = songs,
        form = form,
        onTitleChange = viewModel::onTitleChange,
        onSongKeyChange = viewModel::onSongKeyChange,
        onDifficultyChange = viewModel::onDifficultyChange,
        onStatusChange = viewModel::onStatusChange,
        onAddClick = viewModel::addSong,
        modifier = modifier
    )
}

// Stateless, so it can be previewed without a ViewModel.
@Composable
fun SongsContent(
    songs: List<Song>,
    form: SongFormState,
    onTitleChange: (String) -> Unit,
    onSongKeyChange: (String) -> Unit,
    onDifficultyChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AddSongForm(
            title = form.title,
            onTitleChange = onTitleChange,
            songKey = form.songKey,
            onSongKeyChange = onSongKeyChange,
            difficulty = form.difficulty,
            onDifficultyChange = onDifficultyChange,
            status = form.status,
            onStatusChange = onStatusChange,
            onAddClick = onAddClick
        )

        LazyColumn(
            // Takes the remaining height so the list can scroll.
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // A stable key (the song id) lets Compose keep track of each row
            // when the list changes, instead of relying on its position.
            items(items = songs, key = { it.id }) { song ->
                SongRow(song)
            }
        }
    }
}

@Composable
fun SongRow(song: Song, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Key: ${song.songKey}  •  ${song.difficulty}  •  ${song.status}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongsContentPreview() {
    ProjectAndroidTheme {
        SongsContent(
            songs = previewSongs,
            form = SongFormState(),
            onTitleChange = {},
            onSongKeyChange = {},
            onDifficultyChange = {},
            onStatusChange = {},
            onAddClick = {}
        )
    }
}

// Fake data, only for the preview.
private val previewSongs = listOf(
    Song(1, "Morning Blues", "C", "Easy", "Ready"),
    Song(2, "Slow Waltz in G", "G", "Medium", "Practicing")
)
