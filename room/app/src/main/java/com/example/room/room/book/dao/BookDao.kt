package com.example.room.room.book.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.room.room.book.entity.Author
import com.example.room.room.book.entity.Book
import com.example.room.room.book.entity.BookData

@Dao
interface BookDao {
    @Transaction
    @Query("SELECT * FROM authors")
    fun findAll(): List<BookData>

    @Insert
    fun insertAuthor(vararg author: Author)

    @Insert
    fun insertBook(vararg book: Book)
}