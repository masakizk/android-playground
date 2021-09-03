package com.example.pushreminder

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


data class RequestBody(
    val userId: String
)

/**
 * Androidクライアント > Cloud Functionsを起動
 * Cloud FUnctions   > Cloud Messagingにより、更新を通知
 */
class CloudFunctionsApi {
    interface ApiService {
        @POST("/notify")
        fun run(
            @Header("Authentication") authentication: String,
            @Body body: RequestBody
        ): Call<Unit>
    }

    suspend fun trigger(id: String) = withContext(Dispatchers.IO) {
//        val functions = Firebase.functions
//        val data = hashMapOf(
//            "userId" to id
//        )
//        functions
//            .getHttpsCallable("notify")
//            .call(data)
//            .await()
        val client = OkHttpClient()
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()
        val service = retrofit.create(ApiService::class.java)

        val currentUser = FirebaseAuth.getInstance().currentUser ?: return@withContext
        val token = currentUser
            .getIdToken(false)
            .await()
            .token ?: return@withContext
        Log.d(TAG, "trigger: $token")
        val auth = "bearer $token"

        val request = service.run(auth, RequestBody(id))
        Log.d(TAG, "trigger: ${request.request().headers} ${request.request().body} ${request.request().isHttps}")
        val response = request.awaitResponse()
        Log.d(
            TAG,
            "trigger: code: ${response.code()} message: ${response.body()} ${response.raw()}"
        )
    }

    companion object {
        private const val TAG = "CloudFunctionsApi"
        private const val BASE_URL = "https://us-central1-fir-playground-f6cd9.cloudfunctions.net"
    }
}