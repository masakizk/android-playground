package com.example.daggerhilt.assistedinject

import androidx.lifecycle.ViewModel
import com.example.daggerhilt.calculator.Calculator
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

/**
 * Assisted Inject
 * 1. @AssistedInjectコンストラクタを付ける
 * 2. Factoryインターフェースを作成し、それを実装する
 * 3. @AssistedModuleのモジュールを作成
 * 4. FactoryをViewでDIしてもらう
 * 5. DIされたFactoryからviewModelを作成
 */
class AssistedInjectViewModel @AssistedInject constructor(
    @Assisted val userName: String,
    // 引数に＠Assistedのついていないものがないとエラーが起こる
    calculator: Calculator
) : ViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(userName: String): AssistedInjectViewModel
    }
}

fun AssistedInjectViewModel.Factory.create(
    userName: String
): AssistedInjectViewModel {
    return create(userName)
}

