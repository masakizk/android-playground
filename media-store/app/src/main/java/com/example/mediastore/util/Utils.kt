package com.example.mediastore.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

object Utils {
    @Suppress("SameParameterValue")
    @SuppressLint("SimpleDateFormat")
    fun dateToTimestamp(day: Int, month: Int, year: Int): Long {
        return SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            TimeUnit.MICROSECONDS.toSeconds(formatter.parse("$day.$month.$year")?.time ?: 0)
        }
    }
}