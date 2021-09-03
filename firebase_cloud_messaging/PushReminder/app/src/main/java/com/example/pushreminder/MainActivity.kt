package com.example.pushreminder

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.pushreminder.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding

    // サインイン
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private val mUserRepository: FirestoreUserRepository by lazy {
        FirestoreUserRepository(
            FirebaseFirestore.getInstance()
        )
    }

    private var mUser: User? = null
    private var mToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater).apply {
            btSignIn.setOnClickListener { showSignInDialog() }
            btToken.setOnClickListener { getToken() }
            btMessage.setOnClickListener { sendMessage() }
            btSignOut.setOnClickListener { signOut() }
        }

        setContentView(mBinding.root)
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.let { setUserInfo(it) }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    /*
     * サインイン関連
     */
    private val signInForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.let { signInGoogle(it) }
        }

    private fun showSignInDialog() {
        val signInIntent = googleSignInClient.signInIntent
        signInForResult.launch(signInIntent)
    }

    private fun signInGoogle(result: Intent) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result)
        val account = task.getResult(ApiException::class.java)!!
        val idToken = account.idToken ?: return
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnFailureListener {
                Toast.makeText(this, "Failed in sign in", Toast.LENGTH_LONG).show()
            }
            .addOnSuccessListener { setUserInfo(it.user) }
    }

    private fun signOut() {
        auth.signOut()
        lifecycleScope.launch {
            googleSignInClient.signOut().await()
        }.invokeOnCompletion {
            setUserInfo(null)
        }
    }

    private fun setUserInfo(firebaseUser: FirebaseUser?) {
        mBinding.apply {

            btSignIn.isEnabled = firebaseUser == null
            btToken.isEnabled = firebaseUser != null
            btMessage.isEnabled = firebaseUser != null
            btSignOut.isEnabled = firebaseUser != null

            if (firebaseUser == null) {
                etToken.setText("")
                txUserInfo.text = ""
            } else {
                txUserInfo.text = "${firebaseUser.displayName} ${firebaseUser.email}"
            }
        }

        if (firebaseUser == null) return

        val user = User(
            id = firebaseUser.uid,
            name = firebaseUser.displayName ?: "anonymous",
            tokens = emptyList()
        )
        mUser = user
        lifecycleScope.launch(Dispatchers.IO) {
            val savedUser = mUserRepository.find(user.id)
            if (savedUser == null) mUserRepository.save(user)

            withContext(Dispatchers.Main) {
                mBinding.etMessage.setText(savedUser?.message)
            }
        }
    }

    /*
    トークン取得
     */
    private fun getToken() {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener {
                Log.d(TAG, "getToken: $it")

                mBinding.etToken.setText(it)
                mBinding.btToken.isEnabled = false
                mToken = it

                val userId = mUser?.id ?: return@addOnSuccessListener
                lifecycleScope.launch(Dispatchers.IO) {
                    mUserRepository.addToken(userId, it)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed in getting token", Toast.LENGTH_LONG).show()
            }
    }

    private fun sendMessage() {
        val user = mUser ?: return
        val message = mBinding.etMessage.text.toString()
        lifecycleScope.launch {
            mUserRepository.setMessage(user.id, message)

            // Google Cloud Functionsを起動
            val api = CloudFunctionsApi()
            api.trigger(user.id)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}