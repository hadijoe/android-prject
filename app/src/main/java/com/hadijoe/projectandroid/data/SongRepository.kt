package com.hadijoe.projectandroid.data

import com.hadijoe.projectandroid.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// The only class the ViewModel talks to. It hides Room from the rest of the app.
class SongRepository(private val songDao: SongDao) {

    // Database rows (SongEntity) are converted to UI songs (Song).
    val songs: Flow<List<Song>> = songDao.getAllSongs().map { entities ->
        entities.map { it.toSong() }
    }

    suspend fun addSong(title: String, songKey: String, difficulty: String, status: String) {
        // No id here: Room generates it.
        songDao.insert(SongEntity(title = title, songKey = songKey, difficulty = difficulty, status = status))
    }
}
