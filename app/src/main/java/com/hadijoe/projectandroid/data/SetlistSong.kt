package com.hadijoe.projectandroid.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

// Link table: one row means "this song is in this setlist, at this position".
// A setlist has many songs and a song can be in many setlists (many-to-many).
@Entity(
    tableName = "setlist_songs",
    // The pair is the primary key, so the same song cannot be added twice to one setlist.
    primaryKeys = ["setlistId", "songId"],
    // Foreign keys guarantee a link row always points to a real setlist and a real song.
    // CASCADE: if the setlist or the song is deleted, its link rows are deleted too.
    foreignKeys = [
        ForeignKey(
            entity = SetlistEntity::class,
            parentColumns = ["id"],
            childColumns = ["setlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SongEntity::class,
            parentColumns = ["id"],
            childColumns = ["songId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Room asks for an index on a foreign key column that is not first in the primary key.
    indices = [Index("songId")]
)
data class SetlistSong(
    val setlistId: Long,
    val songId: Long,
    val position: Int // order inside the setlist: 0 is first
)
