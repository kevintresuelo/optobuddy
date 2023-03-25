package com.kevintresuelo.clinicus.models.services

interface ConfigurationService {
    suspend fun fetchConfiguration(): Boolean
}