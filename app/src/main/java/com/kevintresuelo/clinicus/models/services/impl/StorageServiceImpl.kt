package com.kevintresuelo.clinicus.models.services.impl

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.kevintresuelo.clinicus.models.Device
import com.kevintresuelo.clinicus.models.services.StorageService
import com.kevintresuelo.clinicus.models.services.trace
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore) :
    StorageService {
    override suspend fun getDevice(deviceUid: String): Device? =
        deviceCollection().document(deviceUid).get().await().toObject()

    override suspend fun saveDevice(device: Device): Unit =
        trace(SAVE_DEVICE_TRACE) { deviceCollection().document(device.documentId).set(device).await() }

    private fun deviceCollection(): CollectionReference =
        firestore.collection(DEVICE_COLLECTION)

    companion object {
        private const val DEVICE_COLLECTION = "devices"

        private const val SAVE_DEVICE_TRACE = "saveDevice"
    }
}