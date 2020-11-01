package com.example.daggerhilt.logger

import android.content.Context
import android.widget.Toast
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class MyLogger @Inject constructor(
        @ActivityContext private val context: Context
) {
    fun log(message: String) {
        Toast.makeText(context, "MyLogger: $message", Toast.LENGTH_LONG).show()
    }
}