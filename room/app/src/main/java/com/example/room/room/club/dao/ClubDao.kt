package com.example.room.room.club.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.room.room.club.entities.Club
import com.example.room.room.club.entities.ClubData
import com.example.room.room.club.entities.Person

@Dao
interface ClubDao {
    @Transaction
    @Query("SELECT club.name AS club_name, person.name AS person_name, person.studentNumber AS student_id FROM club LEFT JOIN person ON club.id = person.clubId ORDER BY person.studentNumber")
    fun findAll(): List<ClubData>

    @Insert
    fun insertPerson(vararg person: Person)

    @Insert
    fun insertClub(vararg club: Club)
}