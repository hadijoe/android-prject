package com.hadijoe.projectandroid.model

data class Song(
    val id: Long,
    val title: String,
    val songKey: String,   // musical key, e.g. "Am". Named songKey because "key" is an SQL keyword.
    val difficulty: String, // one of SongOptions.difficulties
    val status: String      // one of SongOptions.statuses
)

// The fixed choices live in ONE place so the list and (later) the form agree.
object SongOptions {
    val difficulties = listOf("Easy", "Medium", "Hard")
    val statuses = listOf("Learning", "Practicing", "Ready")
}
