package com.kevintresuelo.clinicus.models

import com.google.firebase.firestore.DocumentId

data class Device (
    @DocumentId val documentId: String = "",
    val fcm_token: String = "",
    val patient_uid: String = "",
    val device: String = "",
    val last_accessed: Long = 0
)