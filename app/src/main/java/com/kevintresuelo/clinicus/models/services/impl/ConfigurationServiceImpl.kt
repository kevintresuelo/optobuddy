package com.kevintresuelo.clinicus.models.services.impl

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.kevintresuelo.clinicus.BuildConfig
import com.kevintresuelo.lorem.models.services.ConfigurationService
import com.kevintresuelo.lorem.models.services.trace
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import com.kevintresuelo.clinicus.R.xml as AppConfig

class ConfigurationServiceImpl @Inject constructor() : ConfigurationService {
    private val remoteConfig
        get() = Firebase.remoteConfig

    init {
        if (BuildConfig.DEBUG) {
            val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 0 }
            remoteConfig.setConfigSettingsAsync(configSettings)
        }

        remoteConfig.setDefaultsAsync(AppConfig.remote_config_defaults)
    }

    override suspend fun fetchConfiguration(): Boolean =
        trace(FETCH_CONFIG_TRACE) { remoteConfig.fetchAndActivate().await() }

    companion object {
        private const val FETCH_CONFIG_TRACE = "fetchConfig"
    }
}