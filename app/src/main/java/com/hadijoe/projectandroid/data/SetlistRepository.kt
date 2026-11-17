package com.hadijoe.projectandroid.data

import com.hadijoe.projectandroid.model.Setlist
import com.hadijoe.projectandroid.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Hides Room from the ViewModels, like SongRepository.
class SetlistRepository(private val setlistDao: SetlistDao) {

    val setlists: Flow<List<Setlist>> = setlistDao.getAllSetlists().map { entities ->
        entities.map { it.toSetlist() }
    }

    fun getSetlist(setlistId: Long): Flow<Setlist?> =
        setlistDao.getSetlist(setlistId).map { it?.toSetlist() }

    // Songs of one setlist, already in the saved order.
    fun getSongsInSetlist(setlistId: Long): Flow<List<Song>> =
        setlistDao.getSongsInSetlist(setlistId).map { entities -> entities.map { it.toSong() } }

    suspend fun addSetlist(name: String) {
        setlistDao.insertSetlist(SetlistEntity(name = name))
    }

    suspend fun addSongToSetlist(setlistId: Long, songId: Long) {
        setlistDao.addSongToSetlist(setlistId, songId)
    }

    suspend fun removeSongFromSetlist(setlistId: Long, songId: Long) {
        setlistDao.removeSongFromSetlist(setlistId, songId)
    }

    suspend fun moveSongUp(setlistId: Long, songId: Long) {
        setlistDao.moveSong(setlistId, songId, direction = -1)
    }

    suspend fun moveSongDown(setlistId: Long, songId: Long) {
        setlistDao.moveSong(setlistId, songId, direction = 1)
    }
}
