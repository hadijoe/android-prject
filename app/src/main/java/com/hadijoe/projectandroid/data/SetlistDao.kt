package com.hadijoe.projectandroid.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SetlistDao {
    @Query("SELECT * FROM setlists ORDER BY id")
    fun getAllSetlists(): Flow<List<SetlistEntity>>

    // Nullable: there may be no setlist with this id.
    @Query("SELECT * FROM setlists WHERE id = :setlistId")
    fun getSetlist(setlistId: Long): Flow<SetlistEntity?>

    @Insert
    suspend fun insertSetlist(setlist: SetlistEntity)

    // JOIN: take the songs whose id appears in the link table for this setlist,
    // sorted by the position stored in the link table.
    @Query(
        """
        SELECT songs.* FROM songs
        INNER JOIN setlist_songs ON songs.id = setlist_songs.songId
        WHERE setlist_songs.setlistId = :setlistId
        ORDER BY setlist_songs.position
        """
    )
    fun getSongsInSetlist(setlistId: Long): Flow<List<SongEntity>>

    // Biggest position + 1. COALESCE gives -1 when the setlist is empty, so the first song gets 0.
    @Query("SELECT COALESCE(MAX(position), -1) + 1 FROM setlist_songs WHERE setlistId = :setlistId")
    suspend fun getNextPosition(setlistId: Long): Int

    // IGNORE: if the song is already in the setlist, do nothing instead of crashing.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSetlistSong(setlistSong: SetlistSong)

    // @Transaction: the two steps run together, so two songs can never get the same position.
    @Transaction
    suspend fun addSongToSetlist(setlistId: Long, songId: Long) {
        val position = getNextPosition(setlistId)
        insertSetlistSong(SetlistSong(setlistId, songId, position))
    }

    @Query("DELETE FROM setlist_songs WHERE setlistId = :setlistId AND songId = :songId")
    suspend fun removeSongFromSetlist(setlistId: Long, songId: Long)

    // The link rows of one setlist, in the saved order.
    @Query("SELECT * FROM setlist_songs WHERE setlistId = :setlistId ORDER BY position")
    suspend fun getSetlistSongs(setlistId: Long): List<SetlistSong>

    @Query("UPDATE setlist_songs SET position = :position WHERE setlistId = :setlistId AND songId = :songId")
    suspend fun updatePosition(setlistId: Long, songId: Long, position: Int)

    // Moves a song one place. direction = -1 means up, +1 means down.
    // @Transaction: both updates are saved together, or none of them.
    @Transaction
    suspend fun moveSong(setlistId: Long, songId: Long, direction: Int) {
        val links = getSetlistSongs(setlistId)
        val index = links.indexOfFirst { it.songId == songId }
        val neighbourIndex = index + direction
        // Nothing to do: the song is not in the setlist, or it is already first/last.
        if (index == -1 || neighbourIndex !in links.indices) return

        val current = links[index]
        val neighbour = links[neighbourIndex]
        // Swap the two position values. This also works when the positions have gaps (0, 2, 5).
        updatePosition(setlistId, current.songId, neighbour.position)
        updatePosition(setlistId, neighbour.songId, current.position)
    }
}
