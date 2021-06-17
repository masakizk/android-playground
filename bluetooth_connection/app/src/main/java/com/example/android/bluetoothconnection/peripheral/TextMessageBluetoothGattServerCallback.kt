package com.example.android.bluetoothconnection.peripheral

import android.bluetooth.*
import com.example.android.bluetoothconnection.databinding.ActivityPeripheralBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TextMessageBluetoothGattServerCallback(
    private val scope: CoroutineScope,
    private val binding: ActivityPeripheralBinding,
) : BluetoothGattServerCallback() {
    private val charValue = ByteArray(PeripheralActivity.UUID_VALUE_SIZE)

    private var _mGattServer: BluetoothGattServer? = null
    var mGattServer
        get() = _mGattServer!!
        set(value) {
            _mGattServer = value
        }

    override fun onCharacteristicReadRequest(
        device: BluetoothDevice?,
        requestId: Int,
        offset: Int,
        characteristic: BluetoothGattCharacteristic?
    ) {
        super.onCharacteristicReadRequest(device, requestId, offset, characteristic)
        if (_mGattServer == null) return

        if (offset > charValue.size) {
            mGattServer.sendResponse(
                device,
                requestId,
                BluetoothGatt.GATT_FAILURE,
                offset,
                null
            )
        } else {
            val value = ByteArray(charValue.size - offset)
            System.arraycopy(charValue, offset, value, 0, value.size)
            mGattServer.sendResponse(
                device,
                requestId,
                BluetoothGatt.GATT_SUCCESS,
                offset,
                value
            )
        }
    }

    override fun onCharacteristicWriteRequest(
        device: BluetoothDevice?,
        requestId: Int,
        characteristic: BluetoothGattCharacteristic?,
        preparedWrite: Boolean,
        responseNeeded: Boolean,
        offset: Int,
        value: ByteArray
    ) {
        super.onCharacteristicWriteRequest(
            device,
            requestId,
            characteristic,
            preparedWrite,
            responseNeeded,
            offset,
            value
        )

        if (_mGattServer == null) return

        if (characteristic?.uuid != PeripheralActivity.UUID_WRITE) {
            mGattServer.sendResponse(
                device,
                requestId,
                BluetoothGatt.GATT_FAILURE,
                offset,
                null
            )
            return
        }

        scope.launch(Dispatchers.Main) {
            binding.apply {
                textReceivedMessage.text = String(value)
                textDeviceAddress.text = device?.address
                textDeviceName.text = device?.name
            }
        }


        if (offset < charValue.size) {
            var len: Int = value.size
            if (offset + len > charValue.size) len = charValue.size - offset
            System.arraycopy(value, 0, charValue, offset, len)
            mGattServer.sendResponse(
                device,
                requestId,
                BluetoothGatt.GATT_SUCCESS,
                offset,
                null
            )
        } else {
            mGattServer.sendResponse(
                device,
                requestId,
                BluetoothGatt.GATT_FAILURE,
                offset,
                null
            )
        }
    }
}