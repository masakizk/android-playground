package com.example.room.room.club.entities

import androidx.room.*

@Entity(tableName = "club")
data class Club(
    @PrimaryKey val id: Long,
    val name: String
)