package com.example.android.bluetoothconnection.peripheral

import android.bluetooth.*
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.android.bluetoothconnection.databinding.ActivityPeripheralBinding
import java.util.*

class PeripheralActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityPeripheralBinding
    private lateinit var mBleManager: BluetoothManager
    private lateinit var mGattServer: BluetoothGattServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPeripheralBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBleManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        val gattServerCallback = TextMessageBluetoothGattServerCallback(lifecycleScope, mBinding)
        mGattServer = mBleManager.openGattServer(this, gattServerCallback)
        gattServerCallback.mGattServer = mGattServer

        mGattServer.addService(createGattService())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startAdvertising()
        }
    }

    private fun createGattService(): BluetoothGattService {
        val gattService = BluetoothGattService(
            UUID_SERVICE,
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        val characteristicWrite = BluetoothGattCharacteristic(
            UUID_WRITE,
            BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_WRITE
        )
        val characteristicRead = BluetoothGattCharacteristic(
            UUID_READ,
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        val characteristicNotify = BluetoothGattCharacteristic(
            UUID_NOTIFY,
            BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        gattService.addCharacteristic(characteristicWrite)
        gattService.addCharacteristic(characteristicRead)
        gattService.addCharacteristic(characteristicNotify)

        val descriptor = BluetoothGattDescriptor(
            UUID_DESC,
            BluetoothGattDescriptor.PERMISSION_WRITE or BluetoothGattDescriptor.PERMISSION_READ
        )
        characteristicNotify.addDescriptor(descriptor)

        return gattService
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startAdvertising() {
        val data = AdvertiseData.Builder()
            .setIncludeTxPowerLevel(true)
            .addServiceUuid(ParcelUuid(UUID_SERVICE))
            .build()

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
            .setTimeout(0)
            .setConnectable(true)
            .build()

        val response = AdvertiseData.Builder()
            .setIncludeDeviceName(true)
            .build()

        mBleManager.adapter.bluetoothLeAdvertiser.startAdvertising(
            settings,
            data,
            response,
            object : AdvertiseCallback() {
                override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                    super.onStartSuccess(settingsInEffect)
                    Log.d(TAG, "onStartSuccess: success")
                }

                override fun onStartFailure(errorCode: Int) {
                    super.onStartFailure(errorCode)
                    Log.d(TAG, "onStartFailure: failed in starting ble")
                }
            })
    }

    companion object {
        private const val TAG = "PeripheralActivity"

        val UUID_SERVICE: UUID = UUID.fromString("a9d158bb-9007-4fe3-b5d2-d3696a3eb067")
        val UUID_WRITE: UUID = UUID.fromString("52dc2801-7e98-4fc2-908a-66161b5959b0")
        val UUID_READ: UUID = UUID.fromString("52dc2802-7e98-4fc2-908a-66161b5959b0")
        val UUID_NOTIFY: UUID = UUID.fromString("52dc2803-7e98-4fc2-908a-66161b5959b0")
        val UUID_DESC: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        const val UUID_VALUE_SIZE = 500
    }
}