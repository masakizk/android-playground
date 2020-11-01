package com.example.daggerhilt.car

// コンストラクタインジェクションのできないdataクラスに対しても
// Provideを使用すれば注入できる
data class HondaEngine(
        override val speed: Int = 100
) : Engine