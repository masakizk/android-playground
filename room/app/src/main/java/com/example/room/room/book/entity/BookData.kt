package com.example.room.room.book.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class BookData(
    @Embedded val author: Author,
    @Relation(parentColumn = "id", entityColumn = "authorId")
    val books: List<Book>
)