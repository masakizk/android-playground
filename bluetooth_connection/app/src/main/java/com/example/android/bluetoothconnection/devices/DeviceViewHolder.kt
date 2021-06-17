package com.example.android.bluetoothconnection.devices

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.bluetoothconnection.databinding.ItemDeviceBinding

class DeviceViewHolder(
    private val mBinding: ItemDeviceBinding,
    private val mCallbackListener: CallbackListener
) :
    RecyclerView.ViewHolder(mBinding.root) {

    interface CallbackListener {
        fun onClick(device: BluetoothDevice)
    }

    companion object {
        fun from(parent: ViewGroup, callbackListener: CallbackListener): DeviceViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemDeviceBinding.inflate(inflater, parent, false)
            return DeviceViewHolder(binding, callbackListener)
        }
    }

    fun bind(device: BluetoothDevice) {
        mBinding.apply {
            textDeviceName.text = device.name
            textDeviceAddress.text = device.address
            root.setOnClickListener { mCallbackListener.onClick(device) }
        }
    }
}