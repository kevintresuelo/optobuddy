package com.kevintresuelo.clinicus.models.services

import com.kevintresuelo.clinicus.models.Device

interface StorageService {
    suspend fun getDevice(deviceUid: String): Device?
    suspend fun saveDevice(device: Device): Unit
}