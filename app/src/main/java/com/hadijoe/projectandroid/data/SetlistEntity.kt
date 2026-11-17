package com.hadijoe.projectandroid.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hadijoe.projectandroid.model.Setlist

// One row of the "setlists" table.
@Entity(tableName = "setlists")
data class SetlistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

fun SetlistEntity.toSetlist() = Setlist(id, name)
