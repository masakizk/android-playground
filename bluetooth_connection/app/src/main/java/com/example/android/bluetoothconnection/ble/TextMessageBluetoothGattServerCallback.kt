package com.example.android.bluetoothconnection.ble

import android.bluetooth.*
import android.util.Log
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TextMessageBluetoothGattServerCallback(
    private val scope: CoroutineScope,
    private val textViewMessage: TextView,
    private val textViewSenderAddress: TextView,
) : BluetoothGattServerCallback() {
    private val charValue = ByteArray(BlePeripheralActivity.UUID_VALUE_SIZE)

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
        Log.d(
            TAG,
            "onCharacteristicReadRequest: [$requestId] $offset-${offset + value.size} ${String(value)}"
        )
        if (_mGattServer == null) return

        if (characteristic?.uuid != BlePeripheralActivity.UUID_WRITE) {
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
            textViewMessage.text = String(value)
            textViewSenderAddress.text = device?.address
        }

        mGattServer.sendResponse(
            device,
            requestId,
            BluetoothGatt.GATT_SUCCESS,
            offset,
            value
        )
    }

    companion object {
        private const val TAG = "TextMessageBluetoothGat"
    }
}