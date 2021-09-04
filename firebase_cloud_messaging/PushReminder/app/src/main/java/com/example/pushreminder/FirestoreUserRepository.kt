package com.example.pushreminder

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreUserRepository(private val firestore: FirebaseFirestore) {

    suspend fun find(id: String): User? {
        val document = getUserDocument(id)
        return document.get().await().toObject(User::class.java)
    }

    suspend fun save(user: User) {
        val document = getUserDocument(user.id)
        document.set(user).await()
    }

    suspend fun addToken(id: String, token: String) {
        val document = getUserDocument(id)
        document.update("tokens", FieldValue.arrayUnion(token)).await()
    }

    suspend fun setMessage(id: String, message: String) {
        val document = getUserDocument(id)
        document.update("message", message)
    }

    private fun getUserDocument(id: String) = firestore.collection("users").document(id)
}