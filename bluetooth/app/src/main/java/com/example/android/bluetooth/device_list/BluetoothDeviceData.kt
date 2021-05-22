package com.example.android.bluetooth.device_list

data class BluetoothDeviceData(
    val name: String?,
    val address: String,
    val rssi: Int,
)