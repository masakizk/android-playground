package com.example.firebasestorage

import android.content.Context
import android.widget.Toast
import com.google.firebase.storage.StorageReference

object DeleteImage {
    fun deleteImage(imageRef: StorageReference, context: Context) {
        imageRef.delete()
            .addOnSuccessListener {
                Toast.makeText(context, "File deleted successfully", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error occurred: $it", Toast.LENGTH_LONG).show()
            }
    }
}