package com.example.android.bluetoothconnection.devices

import android.bluetooth.BluetoothDevice
import androidx.recyclerview.widget.DiffUtil

class DeviceListDiffUtil : DiffUtil.ItemCallback<BluetoothDevice>() {
    override fun areItemsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
        return oldItem.address == newItem.address
    }

    override fun areContentsTheSame(oldItem: BluetoothDevice, newItem: BluetoothDevice): Boolean {
        return oldItem.address == newItem.address && oldItem.name == newItem.name
    }

}