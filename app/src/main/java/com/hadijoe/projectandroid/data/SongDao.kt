package com.hadijoe.projectandroid.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {
    // Returns a Flow: Room emits a new list every time the songs table changes.
    @Query("SELECT * FROM songs ORDER BY id")
    fun getAllSongs(): Flow<List<SongEntity>>

    // suspend: writing to disk is slow, so it must not run on the main thread.
    @Insert
    suspend fun insert(song: SongEntity)
}
