package com.example.pushreminder

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived: ${message.data}")
        notify(message.data.toString())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        notify("NEW TOKEN\n$token")
    }

    private fun notify(message: String?) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingServ"
    }
}