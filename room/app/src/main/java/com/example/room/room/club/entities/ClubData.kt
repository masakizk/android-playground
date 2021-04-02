package com.example.room.room.club.entities

import androidx.room.ColumnInfo

data class ClubData(
    @ColumnInfo(name = "club_name") val clubName: String,
    @ColumnInfo(name = "person_name") val memberName: String,
    @ColumnInfo(name = "student_id") val studentId: String
)