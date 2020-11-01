package com.example.daggerhilt.database

import com.example.daggerhilt.calculator.Calculator

class MathematicalDatabase(
        private val calculator: Calculator
) {
    fun loadMessage(): String {
        return "Mathematical Database: 1+10=${calculator.add(1, 10)}"
    }
}