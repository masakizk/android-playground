package com.example.android.bluetooth.device_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.bluetooth.databinding.ItemDeviceBinding

class BluetoothDeviceViewHolder(
    private val mBinding: ItemDeviceBinding,
    private val mCallbackListener: CallbackListener
) : RecyclerView.ViewHolder(mBinding.root) {
    interface CallbackListener {
        fun onClick(data: BluetoothDeviceData)
    }

    companion object {
        fun from(parent: ViewGroup, callbackListener: CallbackListener): BluetoothDeviceViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemDeviceBinding.inflate(inflater, parent, false)
            return BluetoothDeviceViewHolder(binding, callbackListener)
        }
    }

    fun bind(data: BluetoothDeviceData) {
        mBinding.apply {
            root.setOnClickListener { mCallbackListener.onClick(data) }
            textDeviceName.text = data.name
            textMacAddress.text = data.address
            textRssi.text = data.rssi.toString()
        }
    }
}