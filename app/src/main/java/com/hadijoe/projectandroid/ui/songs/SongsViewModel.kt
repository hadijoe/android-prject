package com.hadijoe.projectandroid.ui.songs

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hadijoe.projectandroid.data.AppDatabase
import com.hadijoe.projectandroid.data.SongRepository
import com.hadijoe.projectandroid.model.Song
import com.hadijoe.projectandroid.model.SongOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// What the user has typed/chosen in the add-song form.
data class SongFormState(
    val title: String = "",
    val songKey: String = "",
    val difficulty: String = SongOptions.difficulties.first(),
    val status: String = SongOptions.statuses.first()
)

class SongsViewModel(private val repository: SongRepository) : ViewModel() {

    // The list now comes from Room. stateIn turns the Flow into a StateFlow for the UI.
    // WhileSubscribed(5_000): keep collecting 5 seconds after the UI stops, so a rotation does not restart the query.
    val songs: StateFlow<List<Song>> = repository.songs.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _form = MutableStateFlow(SongFormState())
    val form: StateFlow<SongFormState> = _form.asStateFlow()

    fun onTitleChange(value: String) {
        // copy makes a new object with one field changed.
        _form.update { it.copy(title = value) }
    }

    fun onSongKeyChange(value: String) {
        _form.update { it.copy(songKey = value) }
    }

    fun onDifficultyChange(value: String) {
        _form.update { it.copy(difficulty = value) }
    }

    fun onStatusChange(value: String) {
        _form.update { it.copy(status = value) }
    }

    fun addSong() {
        val current = _form.value
        // Safety check: the button is disabled in this case, but the ViewModel should not trust the UI.
        if (current.title.isBlank() || current.songKey.isBlank()) return

        // Launch a coroutine: the insert is a suspend function and runs off the main thread.
        viewModelScope.launch {
            repository.addSong(current.title.trim(), current.songKey.trim(), current.difficulty, current.status)
        }
        // Clear the text fields, keep the chosen difficulty and status.
        _form.update { it.copy(title = "", songKey = "") }
    }

    companion object {
        // Tells viewModel() how to build this ViewModel, because it now needs a repository.
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val database = AppDatabase.getInstance(application)
                SongsViewModel(SongRepository(database.songDao()))
            }
        }
    }
}
