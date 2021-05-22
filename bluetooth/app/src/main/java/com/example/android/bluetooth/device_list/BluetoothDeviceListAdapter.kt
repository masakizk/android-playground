package com.example.android.bluetooth.device_list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class BluetoothDeviceListAdapter(
    private val mCallbackListener: BluetoothDeviceViewHolder.CallbackListener
) : ListAdapter<BluetoothDeviceData, BluetoothDeviceViewHolder>(BluetoothDeviceDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BluetoothDeviceViewHolder {
        return BluetoothDeviceViewHolder.from(parent, mCallbackListener)
    }

    override fun onBindViewHolder(holder: BluetoothDeviceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}