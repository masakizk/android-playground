package com.example.android.bluetooth.device_list

import androidx.recyclerview.widget.DiffUtil

class BluetoothDeviceDiffUtil : DiffUtil.ItemCallback<BluetoothDeviceData>() {
    override fun areItemsTheSame(
        oldItem: BluetoothDeviceData,
        newItem: BluetoothDeviceData
    ): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(
        oldItem: BluetoothDeviceData,
        newItem: BluetoothDeviceData
    ): Boolean {
        return oldItem == newItem
    }

}