package com.kevintresuelo.lorem.models.services.impl

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.kevintresuelo.lorem.models.Device
import com.kevintresuelo.lorem.models.Doctor
import com.kevintresuelo.lorem.models.Exercise
import com.kevintresuelo.lorem.models.Patient
import com.kevintresuelo.lorem.models.services.StorageService
import com.kevintresuelo.lorem.models.services.trace
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore) :
    StorageService {

    override suspend fun getPatient(patientCode: String, doctorCode: String): Patient? {
        val doctor: Doctor? = getDoctor(doctorCode) ?: return null
        var patient: Patient? = null;

        if (doctor != null) {
            patientCollection()
                .whereEqualTo("code", patientCode)
                .whereEqualTo("doctor_uid", doctor.uid)
                .get()
                .addOnSuccessListener { snapshots ->
                    patient = if (snapshots.size() == 1) {
                        snapshots.documents.firstOrNull()?.toObject<Patient>()
                    } else {
                        null;
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("lorem", "Error getting documents: ", exception)
                }
                .await()
        }

        return patient
    }

    override suspend fun getPatientByUid(patientUid: String): Patient? =
        patientCollection().document(patientUid).get().await().toObject()

    override suspend fun getDoctor(doctorCode: String): Doctor? {
        var doctor: Doctor? = null

        doctorCollection()
            .whereEqualTo("code", doctorCode)
            .get()
            .addOnSuccessListener { snapshots ->
                doctor = if (snapshots.size() == 1) {
                    Log.d("lorem", "true")
                    snapshots.documents.firstOrNull()?.toObject<Doctor>()
                } else {
                    Log.d("lorem", "false")
                    null;
                }
            }
            .addOnFailureListener { exception ->
                Log.w("lorem", "Error getting documents: ", exception)
            }
            .await()

        return doctor
    }

    override suspend fun getDoctorByUid(doctorUid: String): Doctor? =
        doctorCollection().document(doctorUid).get().await().toObject()

    override suspend fun saveExercise(exercise: Exercise): Unit =
        trace(UPDATE_EXERCISE_TRACE) { exerciseCollection().document(exercise.documentId).set(exercise).await() }


    override suspend fun getDevice(deviceUid: String): Device? =
        deviceCollection().document(deviceUid).get().await().toObject()

    override suspend fun saveDevice(device: Device): Unit =
        trace(SAVE_DEVICE_TRACE) { deviceCollection().document(device.documentId).set(device).await() }

    override suspend fun updateTokenWithPatient(tokenId: String, patientId: String): Unit =
        trace(SAVE_DEVICE_TRACE) {
            deviceCollection().document(tokenId).update("patient_uid", patientId).await()
        }


    private fun patientCollection(): CollectionReference =
        firestore.collection(PATIENT_COLLECTION)

    private fun doctorCollection(): CollectionReference =
        firestore.collection(DOCTOR_COLLECTION)

    private fun exerciseCollection(): CollectionReference =
        firestore.collection(EXERCISE_COLLECTION)

    private fun deviceCollection(): CollectionReference =
        firestore.collection(DEVICE_COLLECTION)

    companion object {
        private const val PATIENT_COLLECTION = "patients"
        private const val DOCTOR_COLLECTION = "doctors"
        private const val EXERCISE_COLLECTION = "exercises"
        private const val DEVICE_COLLECTION = "devices"

        private const val SAVE_EXERCISE_TRACE = "saveExercise"
        private const val UPDATE_EXERCISE_TRACE = "updateExercise"

        private const val SAVE_DEVICE_TRACE = "saveDevice"
    }
}