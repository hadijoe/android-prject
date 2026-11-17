package com.hadijoe.projectandroid.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hadijoe.projectandroid.model.Song

// One row of the "songs" table.
@Entity(tableName = "songs")
data class SongEntity(
    // id = 0 means "no id yet": Room generates the real one on insert.
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val songKey: String,
    val difficulty: String,
    val status: String
)

// Converts a database row into the Song that the UI uses.
fun SongEntity.toSong() = Song(id, title, songKey, difficulty, status)
