package com.example.recyclerviewselection.models

import java.util.*

data class MyItem(
    val id: String = UUID.randomUUID().toString(),
    val value: String
)
