package com.example.room

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.room.databinding.FragmentFirstBinding
import com.example.room.room.MyRoomDatabase
import com.example.room.room.book.entity.Author
import com.example.room.room.book.entity.Book
import com.example.room.room.club.entities.Club
import com.example.room.room.club.entities.Person
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding

    private val database by lazy {
        Room.databaseBuilder(
            requireContext().applicationContext,
            MyRoomDatabase::class.java,
            "my_database"
        ).build()
    }

    private val clubDao get() = database.clubMemberDao()
    private val bookDao get() = database.bookDao()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false).apply {

            buttonSaveBooks.setOnClickListener { lifecycleScope.launch { saveBooks() } }
            buttonFindBooks.setOnClickListener { lifecycleScope.launch { findBooks() } }

            buttonSaveClubs.setOnClickListener { lifecycleScope.launch { saveClubs() } }
            buttonFindClubs.setOnClickListener { lifecycleScope.launch { findClubs() } }

            buttonClearAll.setOnClickListener { lifecycleScope.launch(Dispatchers.IO) { database.clearAllTables() } }
        }
        return binding.root
    }

    private suspend fun saveClubs() = withContext(Dispatchers.IO) {
        val tennis = Club(id = 0, "tennis")
        val basketball = Club(id = 1, "basketball")
        clubDao.insertClub(tennis, basketball)
        clubDao.insertPerson(
            *listOf("リチャード", "ダン", "ジェイク")
                .map { Person(name = it, studentNumber = Random().nextInt(), clubId = 0) }
                .toTypedArray(),
            *listOf("ハーマン", "ライル", "アーニー")
                .map { Person(name = it, studentNumber = Random().nextInt(), clubId = 1) }
                .toTypedArray()
        )
    }

    private suspend fun findClubs() {
        val data = withContext(Dispatchers.IO) { clubDao.findAll() }
        withContext(Dispatchers.Main) {
            binding.textResult.text = StringBuilder().apply {
                data.groupBy { it.clubName }.forEach { (clubName, club) ->
                    append("Club: $clubName")
                    appendLine()
                    club.forEach {
                        append("\t${it.memberName}")
                        appendLine()
                    }
                    appendLine()
                }
            }
        }
    }

    private suspend fun saveBooks() = withContext(Dispatchers.IO) {
        val lewis = Author(id = 0, name = "ルイス・キャロル")
        val arthur = Author(id = 1, name = "アーサー・コナン・ドイル")
        bookDao.insertAuthor(lewis, arthur)
        bookDao.insertBook(
            *listOf("不思議の国のアリス", "鏡の国のアリス", "スナーク狩り", "シルヴィーとブルーノ")
                .map { Book(name = it, authorId = 0) }
                .toTypedArray(),
            *listOf("緋色の研究", "４つの署名", "シャーロック・ホームズの冒険", "シャーロック・ホームズの思い出")
                .map { Book(name = it, authorId = 1) }
                .toTypedArray()
        )
    }

    private suspend fun findBooks() {
        val books = withContext(Dispatchers.IO) {
            return@withContext bookDao.findAll()
        }
        withContext(Dispatchers.Main) {
            binding.textResult.text = StringBuilder().apply {
                books.forEach { book ->
                    append("Author: ${book.author.name}")
                    appendLine()
                    book.books.forEach { append("\t${it.name}\n") }
                    appendLine()
                }
            }
        }
    }

    companion object {
        private const val TAG = "FirstFragment"
    }
}