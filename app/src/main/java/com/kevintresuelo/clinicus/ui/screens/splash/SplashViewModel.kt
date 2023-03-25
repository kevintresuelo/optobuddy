package com.kevintresuelo.clinicus.ui.screens.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kevintresuelo.clinicus.SPLASH_SCREEN
import com.kevintresuelo.clinicus.components.snackbar.SnackbarManager
import com.kevintresuelo.clinicus.models.Device
import com.kevintresuelo.clinicus.ui.screens.ClinicusViewModel
import com.kevintresuelo.clinicus.utils.*
import com.kevintresuelo.lorem.models.services.ConfigurationService
import com.kevintresuelo.lorem.models.services.ContextService
import com.kevintresuelo.lorem.models.services.LogService
import com.kevintresuelo.lorem.models.services.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.kevintresuelo.clinicus.R.string as AppStrings

@HiltViewModel
class SplashViewModel @Inject constructor(
    configurationService: ConfigurationService,
    private val storageService: StorageService,
    private val contextService: ContextService,
    logService: LogService
) : ClinicusViewModel(logService) {

    var doneLoadingPatient: Boolean by mutableStateOf(false)
    var doneLoadingOnboarding: Boolean by mutableStateOf(false)

    private var patientUid: String? by mutableStateOf(null)
    private var hasFinishedOnboarding: Boolean by mutableStateOf(false)

    init {
        createNotificationChannels(contextService.getApplicationContext())

        launchCatching {
            contextService.getApplicationContext().readString(DataStore.PreferenceKeys.DEVICE_UID).collect {
                val device = if (it.isBlank()) null else storageService.getDevice(it)

                if (device == null) {
                    val id = autoId()
                    storageService.saveDevice(
                        Device(
                            documentId = id,
                            device = getDeviceDetails(),
                            last_accessed = System.currentTimeMillis()
                        )
                    )
                    contextService.getApplicationContext().writeString(DataStore.PreferenceKeys.DEVICE_UID, id)
                } else {
                    storageService.saveDevice(
                        device.copy(
                            last_accessed = System.currentTimeMillis()
                        )
                    )
                }
            }
        }

        launchCatching {
            contextService.getApplicationContext().readString(DataStore.PreferenceKeys.PATIENT_UID).collect {
                patientUid = it

                if(it.isNotBlank()) {
                    syncWithServer(it)
                }

                doneLoadingPatient = true
            }
        }

        launchCatching {
            contextService.getApplicationContext().readBool(DataStore.PreferenceKeys.HAS_FINISHED_ONBOARDING).collect {
                hasFinishedOnboarding = it

                doneLoadingOnboarding = true
            }
        }

        launchCatching {
            configurationService.fetchConfiguration()
        }
    }

    fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        //openAndPopUp(CATALOG_SCREEN, SPLASH_SCREEN)
    }

    private fun createNotificationChannels(context: Context) {
        val defaultChannelId = NotificationChannels.CHANNEL_DEFAULT
        val defaultChannelName = context.getString(AppStrings.notification_channel_default_name)
        val defaultChannelDescription = context.getString(AppStrings.notification_channel_default_description)
        val defaultChannelImportance = NotificationManager.IMPORTANCE_DEFAULT
        val defaultChannel = NotificationChannel(defaultChannelId, defaultChannelName, defaultChannelImportance).apply {
            description = defaultChannelDescription
        }

        val updatesChannelId = NotificationChannels.CHANNEL_UPDATES
        val updatesChannelName = context.getString(AppStrings.notification_channel_updates_name)
        val updatesChannelDescription = context.getString(AppStrings.notification_channel_updates_description)
        val updatesChannelImportance = NotificationManager.IMPORTANCE_HIGH
        val updatesChannel = NotificationChannel(updatesChannelId, updatesChannelName, updatesChannelImportance).apply {
            description = updatesChannelDescription
        }

        val exercisesChannelId = NotificationChannels.CHANNEL_EXERCISES
        val exercisesChannelName = context.getString(AppStrings.notification_channel_exercises_name)
        val exercisesChannelDescription = context.getString(AppStrings.notification_channel_exercises_description)
        val exercisesChannelImportance = NotificationManager.IMPORTANCE_HIGH
        val exercisesChannel = NotificationChannel(exercisesChannelId, exercisesChannelName, exercisesChannelImportance).apply {
            description = exercisesChannelDescription
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(defaultChannel)
        notificationManager.createNotificationChannel(updatesChannel)
        notificationManager.createNotificationChannel(exercisesChannel)
    }
}