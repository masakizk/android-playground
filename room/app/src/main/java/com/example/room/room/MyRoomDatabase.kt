package com.example.room.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.room.room.book.dao.BookDao
import com.example.room.room.book.entity.Author
import com.example.room.room.book.entity.Book
import com.example.room.room.club.dao.ClubDao
import com.example.room.room.club.entities.Club
import com.example.room.room.club.entities.Person

@Database(entities = [Club::class, Person::class, Book::class, Author::class], version = 3)
abstract class MyRoomDatabase : RoomDatabase() {
    abstract fun clubMemberDao(): ClubDao

    abstract fun bookDao(): BookDao
}