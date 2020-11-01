package com.example.daggerhilt.phone

class Phone(
        private val battery: Battery
) {
    fun batteryLevel(): String{
        return "Current Battery: ${battery.batteryLevel}%"
    }
}