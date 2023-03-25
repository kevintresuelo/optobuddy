package com.kevintresuelo.lorem.components.updates

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.kevintresuelo.clinicus.BuildConfig
import com.kevintresuelo.clinicus.components.snackbar.SnackbarManager
import com.kevintresuelo.clinicus.utils.ext.findActivity
import java.math.BigDecimal
import java.math.RoundingMode
import com.kevintresuelo.clinicus.R.string as AppStrings

class UpdateHandler(private val context: Context) {

    private val daysBeforeOfferingFlexibleUpdate = 2
    private val daysBeforeOfferingImmediateUpdate = 5

    val appUpdateManager: AppUpdateManager by lazy {
        AppUpdateManagerFactory.create(context)
    }

    fun checkForUpdates() {

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {

                var updateType = AppUpdateType.FLEXIBLE

                if (
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                    && shouldOfferImmediateUpdate(appUpdateInfo.availableVersionCode(), appUpdateInfo.clientVersionStalenessDays())
                ) {
                    updateType = AppUpdateType.IMMEDIATE
                } else if (
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                    && shouldOfferFlexibleUpdate(appUpdateInfo.availableVersionCode(), appUpdateInfo.clientVersionStalenessDays())
                ) {
                    updateType = AppUpdateType.FLEXIBLE
                }

                context.findActivity()?.let { activity ->
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateType,
                        activity,
                        appUpdateInfo.availableVersionCode()
                    )
                }

                if (updateType == AppUpdateType.FLEXIBLE) {

                    val listener = InstallStateUpdatedListener { state ->
                        when (state.installStatus()) {
                            InstallStatus.DOWNLOADING -> {
                                SnackbarManager.showMessage(AppStrings.update_downloading)
                            }
                            InstallStatus.DOWNLOADED -> {
                                SnackbarManager.showMessageWithAction(
                                    AppStrings.update_downloaded,
                                    AppStrings.update_restart
                                ) {
                                    SnackbarManager.showMessageWithAction(AppStrings.update_downloaded, AppStrings.update_restart) {
                                        appUpdateManager.completeUpdate()
                                    }
                                }
                            }
                            InstallStatus.CANCELED -> {
                                SnackbarManager.showMessage(AppStrings.update_canceled)
                            }
                            InstallStatus.FAILED -> {
                                SnackbarManager.showMessage(AppStrings.update_failed)
                            }
                            else -> {

                            }
                        }

                    }

                    appUpdateManager.registerListener(listener)

                }

            }
        }
    }

    private fun shouldOfferImmediateUpdate(updateVersionCode: Int, clientVersionStalenessDays: Int?): Boolean {
        // Offer a flexible update when the following conditions are met
        return (
                // At least 5 days have passed since the update was made available
                ((clientVersionStalenessDays ?: -1) >= daysBeforeOfferingImmediateUpdate)
                        // There is a major jump between the current version and the available update
                        || isMajorVersionUpdate(updateVersionCode)
                )

    }

    private fun shouldOfferFlexibleUpdate(updateVersionCode: Int, clientVersionStalenessDays: Int?): Boolean {
        // Offer a flexible update when the following conditions are met
        return (
                // At least 2 days have passed since the update was made available
                ((clientVersionStalenessDays ?: -1) >= daysBeforeOfferingFlexibleUpdate)
                        // At least 5 version code difference between the current and the available version
                        || updateVersionCode - BuildConfig.VERSION_CODE >= 5
                )

    }

    private fun getMajorVersionCode(versionCode: Int): Int {
        return BigDecimal(versionCode).setScale(-4, RoundingMode.FLOOR).toInt()
    }

    private fun isMajorVersionUpdate(updateVersionCode: Int): Boolean {
        return (getMajorVersionCode(updateVersionCode) - getMajorVersionCode(BuildConfig.VERSION_CODE)) >= 10000
    }
    
}