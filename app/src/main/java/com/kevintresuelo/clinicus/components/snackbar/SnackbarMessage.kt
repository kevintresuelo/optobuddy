package com.kevintresuelo.clinicus.components.snackbar

import android.content.res.Resources
import androidx.annotation.StringRes
import com.kevintresuelo.clinicus.R.string as AppText

sealed class SnackbarMessage {
    class StringSnackbar(val message: String) : SnackbarMessage()
    class ResourceSnackbar(@StringRes val message: Int) : SnackbarMessage()
    class ActionSnackbar(@StringRes val message: Int, @StringRes val actionText: Int, val action: () -> Unit) : SnackbarMessage()

    companion object {
        fun SnackbarMessage.toMessage(resources: Resources): String {
            return when (this) {
                is StringSnackbar -> this.message
                is ResourceSnackbar -> resources.getString(this.message)
                is ActionSnackbar -> resources.getString(this.message)
            }
        }

        fun SnackbarMessage.toActionText(resources: Resources): String? {
            return when (this) {
                is ActionSnackbar -> resources.getString(this.actionText)
                else -> null
            }
        }

        fun SnackbarMessage.toAction(): Unit? {
            return when (this) {
                is ActionSnackbar -> this.action()
                else -> null
            }
        }

        fun Throwable.toSnackbarMessage(): SnackbarMessage {
            val message = this.message.orEmpty()
            return if (message.isNotBlank()) StringSnackbar(message)
            else ResourceSnackbar(AppText.generic_error)
        }
    }
}