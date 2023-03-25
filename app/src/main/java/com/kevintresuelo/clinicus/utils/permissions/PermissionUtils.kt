package com.kevintresuelo.clinicus.utils.permissions

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.kevintresuelo.clinicus.utils.permissions.PermissionStatus
import com.kevintresuelo.clinicus.utils.permissions.isPermissionRequestedBefore
import com.kevintresuelo.clinicus.utils.permissions.setPermissionAllowed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PermissionUtils(val context: Context) {

    @OptIn(ExperimentalPermissionsApi::class)
    fun checkPermission(
        permission: PermissionState,
    ): PermissionStatus {
        var permissionStatus: PermissionStatus

        val scope = CoroutineScope(Job() + Dispatchers.IO)

        if (permission.status.isGranted) {
            scope.launch {
                context.setPermissionAllowed(permission.permission)
            }
            permissionStatus = PermissionStatus.GRANTED
        } else {
            val shouldShowRationale = permission.status.shouldShowRationale
            val isRequestedBefore = context.isPermissionRequestedBefore(permission.permission)

            permissionStatus = when {
                isRequestedBefore && !shouldShowRationale -> {
                    PermissionStatus.DENIED_FOREVER
                }
                shouldShowRationale -> {
                    PermissionStatus.DENIED
                }
                else -> {
                    PermissionStatus.UNKNOWN
                }
            }
        }

        return permissionStatus
    }

    companion object {
        fun askUserToGrantPermissionExplicitly(context: Context) {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        }

        fun checkNotificationPermission(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        }

        fun checkWriteSettingsPermission(context: Context): Boolean {
            return Settings.System.canWrite(context)
        }

        fun checkBatteryOptimizations(context: Context): Boolean {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            return powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }

        fun askUserToDisableBatteryOptimization(context: Context) {
            val intent = Intent()
            intent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            context.startActivity(intent)
        }

        fun askUserToGrantWriteSettings(context: Context) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_WRITE_SETTINGS
            context.startActivity(intent)
        }
    }

}

@Composable
fun PermissionRequiredAlert(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmText: @Composable RowScope.() -> Unit = { Text(text = stringResource(id = android.R.string.ok)) },
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                content = confirmText,
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                content = { Text(text = stringResource(id = android.R.string.cancel)) }
            )
        },
        icon = icon,
        title = title,
        text = text,
    )
}