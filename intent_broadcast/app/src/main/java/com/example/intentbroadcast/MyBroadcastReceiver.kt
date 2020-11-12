package com.example.intentbroadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyBroadcastReceiver : BroadcastReceiver() {
    companion object{
        const val MESSAGE_KEY = "message"
        const val INTENT_APPLE = "com.example.broadcast.INTENT_APPLE"
    }

    override fun onReceive(context: Context, intent: Intent) {

        when(intent.action){
            Intent.ACTION_SEND -> {
                // 用意されているインテント
                val message = intent.getStringExtra(MESSAGE_KEY)
                Toast.makeText(context, "Received: $message", Toast.LENGTH_LONG).show()
            }

            INTENT_APPLE -> {
                // 自作のインテント
                Toast.makeText(context, "Received: APPLE!", Toast.LENGTH_LONG).show()
            }

            else -> {
                // インテントの指定なし
                val action = "Action: ${intent.action}"
                val uri = "Uri: ${intent.toUri(Intent.URI_INTENT_SCHEME)}"
                Toast.makeText(context, "Received\n$action\n$uri\n", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun showMessage(context: Context, message: String){
    }
}