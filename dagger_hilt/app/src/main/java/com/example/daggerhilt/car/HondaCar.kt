package com.example.daggerhilt.car

class HondaCar(
        private val engine: Engine
): Car {
    override fun drive(): String {
        return "driving at ${engine.speed}km/h"
    }
}