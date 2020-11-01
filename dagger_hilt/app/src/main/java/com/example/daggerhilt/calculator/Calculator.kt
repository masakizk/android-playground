package com.example.daggerhilt.calculator

import javax.inject.Inject

// すべてのクラスにおいてコンストラクタインジェクションが可能な場合は
// モジュールを作成しなくても良い
class Calculator @Inject constructor(
        private val keyboard: Keyboard
) {
    fun add(a: Int, b: Int): Int {
        return a + b
    }
}