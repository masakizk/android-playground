package com.example.android.bluetoothconnection.devices

import android.bluetooth.BluetoothDevice
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter


class DeviceListAdapter(private val callbackListener: DeviceViewHolder.CallbackListener) : ListAdapter<BluetoothDevice, DeviceViewHolder>(DeviceListDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder.from(parent, callbackListener)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
