package com.example.android.service

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class MyLogger(private val context: Context, private val tag: String) {
    fun i(message: String) {
        context.openFileOutput(LOG_FILE_NAME, Context.MODE_PRIVATE or Context.MODE_APPEND).use {
            val formatter = SimpleDateFormat("H:m:s", Locale.JAPAN)
            val now = Calendar.getInstance()
            val dateFmt = formatter.format(now.time)
            it.write("$dateFmt $message\n".toByteArray())
        }
    }

    fun getLogs(): String {
        return kotlin.runCatching {
            context.openFileInput(LOG_FILE_NAME)
                .bufferedReader()
                .readLines()
                .fold("") { prev, text -> "$prev\n$text" }
        }.fold(
            onSuccess = { it },
            onFailure = { "" }
        )
    }

    fun clear() {
        context.openFileOutput(LOG_FILE_NAME, Context.MODE_PRIVATE).use {

        }
    }

    companion object {
        const val LOG_FILE_NAME = "log.txt"
    }
}