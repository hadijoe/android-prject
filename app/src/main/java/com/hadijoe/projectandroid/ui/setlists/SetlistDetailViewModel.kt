package com.hadijoe.projectandroid.ui.setlists

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hadijoe.projectandroid.data.AppDatabase
import com.hadijoe.projectandroid.data.SetlistRepository
import com.hadijoe.projectandroid.data.SongRepository
import com.hadijoe.projectandroid.model.Setlist
import com.hadijoe.projectandroid.model.Song
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// One setlist: its songs in order, and the saved songs that can still be added.
class SetlistDetailViewModel(
    private val setlistId: Long,
    private val setlistRepository: SetlistRepository,
    songRepository: SongRepository
) : ViewModel() {

    val setlist: StateFlow<Setlist?> = setlistRepository.getSetlist(setlistId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    val songsInSetlist: StateFlow<List<Song>> = setlistRepository.getSongsInSetlist(setlistId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    // combine: recalculated when either list changes.
    // Available = all saved songs minus the ones already in this setlist.
    val availableSongs: StateFlow<List<Song>> = combine(
        songRepository.songs,
        setlistRepository.getSongsInSetlist(setlistId)
    ) { allSongs, inSetlist ->
        val usedIds = inSetlist.map { it.id }
        allSongs.filter { it.id !in usedIds }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun addSong(songId: Long) {
        viewModelScope.launch {
            setlistRepository.addSongToSetlist(setlistId, songId)
        }
    }

    fun removeSong(songId: Long) {
        viewModelScope.launch {
            setlistRepository.removeSongFromSetlist(setlistId, songId)
        }
    }

    // The ViewModel does not reorder a list in memory. It changes the positions in the database, and the new order comes back through songsInSetlist.
    fun moveSongUp(songId: Long) {
        viewModelScope.launch {
            setlistRepository.moveSongUp(setlistId, songId)
        }
    }

    fun moveSongDown(songId: Long) {
        viewModelScope.launch {
            setlistRepository.moveSongDown(setlistId, songId)
        }
    }

    companion object {
        // A function, not a val: each setlist screen needs its own setlistId.
        fun factory(setlistId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val database = AppDatabase.getInstance(application)
                SetlistDetailViewModel(
                    setlistId = setlistId,
                    setlistRepository = SetlistRepository(database.setlistDao()),
                    songRepository = SongRepository(database.songDao())
                )
            }
        }
    }
}
