package com.kevintresuelo.lorem.models.services

interface ConfigurationService {
    suspend fun fetchConfiguration(): Boolean
}