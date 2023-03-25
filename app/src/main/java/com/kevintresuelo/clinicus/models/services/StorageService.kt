package com.kevintresuelo.lorem.models.services

import com.kevintresuelo.lorem.models.Device
import com.kevintresuelo.lorem.models.Doctor
import com.kevintresuelo.lorem.models.Exercise
import com.kevintresuelo.lorem.models.Patient

interface StorageService {
    suspend fun getPatient(patientCode: String, doctorCode: String): Patient?
    suspend fun getPatientByUid(patientUid: String): Patient?
    suspend fun getDoctor(doctorCode: String): Doctor?
    suspend fun getDoctorByUid(doctorUid: String): Doctor?
    suspend fun saveExercise(exercise: Exercise): Unit
    suspend fun getDevice(deviceUid: String): Device?
    suspend fun saveDevice(device: Device): Unit
    suspend fun updateTokenWithPatient(tokenId: String, patientId: String): Unit
}