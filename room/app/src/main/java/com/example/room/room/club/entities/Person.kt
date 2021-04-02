package com.example.room.room.club.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "person")
data class Person(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clubId: Long,
    val name: String,
    val studentNumber: Int
)
