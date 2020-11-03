### 準備

- Firebaseの初期化(省略)
- Firebase Authの依存を追加

    ```groovy
    // Import the BoM for the Firebase platform
    implementation platform('com.google.firebase:firebase-bom:25.12.0')

    // Declare the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation 'com.google.firebase:firebase-auth'

    // Also declare the dependency for the Google Play services library and specify its version
    implementation 'com.google.android.gms:play-services-auth:18.1.0'
    ```

- `SHA1`フィンガープリントを登録

    ```bash
    keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore
    ```

- Googleログインを有効化
- OAuth2.0のウェブクライアントIDを取得
    google-service.jsonに記載されているので取得しなくても良い(stringリソースが生成される)

## 認証する

---

```kotlin
// Googleサインイン
private lateinit var googleSignInClient: GoogleSignInClient
// Firebase認証
private lateinit var auth: FirebaseAuth
```

```kotlin
/* onCreate */

// Googleサインイン
val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				    .requestEmail()
				    .requestIdToken(getString(R.string.default_web_client_id))
				    .build()

googleSignInClient = GoogleSignIn.getClient(this, gso)

// Firebase認証
auth = FirebaseAuth.getInstance()
```

### ログインアクティビティを表示

```kotlin
val signInIntent = googleSignInClient.signInIntent
startActivityForResult(signInIntent, RC_SIGN_IN)
```

### Googleにサインイン

ユーザーがGoogleのログインに成功したら

`GoogleSignInAccount`オブジェクトからIDトークンを取得

```kotlin
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
      super.onActivityResult(requestCode, resultCode, data)
      when (requestCode) {
          RC_SIGN_IN -> {
              val task = GoogleSignIn.getSignedInAccountFromIntent(data)
              try {
	                // ログイン成功
                  val account = task.getResult(ApiException::class.java)!!
                  signInGoogle(account.idToken!!)
              } catch (e: ApiException) {
									// ログイン失敗
              }
          }
      }
  }
```

### Firebaseへ認証

取得したIDトークンを利用してFirebase認証情報と交換

Firebase認証情報を使用して**Firebaseでの認証**を行う

```kotlin
// signInGoogle
val credential = GoogleAuthProvider.getCredential(idToken, null)
auth.signInWithCredential(credential)
    .addOnCompleteListener { task ->
        if (task.isSuccessful) onSignInCompleted()
        else onSignInFailed()
    }
```

### サインアウトする

```kotlin
auth.signOut()
googleSignInClient
    .signOut()
    .addOnCompleteListener { /* サインアウト完了時に呼ばれる */ }
```