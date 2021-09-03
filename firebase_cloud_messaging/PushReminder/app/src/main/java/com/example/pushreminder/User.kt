package com.example.pushreminder

data class User(
    val id: String = "",
    val name: String = "",
    val tokens: List<String> = emptyList(),
    val message: String = ""
)