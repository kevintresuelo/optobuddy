package com.kevintresuelo.clinicus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.kevintresuelo.clinicus.ui.theme.AppTheme
import com.kevintresuelo.lorem.components.updates.UpdateHandler
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var updateHandler: UpdateHandler? = null

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val widthSizeClass = windowSizeClass.widthSizeClass
            val heightSizeClass = windowSizeClass.heightSizeClass
            ClinicusApp(this, widthSizeClass, heightSizeClass)
        }

        updateHandler = UpdateHandler(this)
        updateHandler!!.checkForUpdates()
    }

    override fun onResume() {
        super.onResume()

        updateHandler?.let {
            it.appUpdateManager
                .appUpdateInfo
                .addOnSuccessListener {  appUpdateInfo ->
                    if (appUpdateInfo.updateAvailability()
                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                    ) {
                        it.appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            appUpdateInfo.availableVersionCode()
                        )
                    }
                }
        }
    }
}