package com.example.mediastore.model

import android.net.Uri
import java.util.*

data class MediaStoreImage(
    val id: Long,
    val title: String,
    val dateModified: Date,
    val uri: Uri
)