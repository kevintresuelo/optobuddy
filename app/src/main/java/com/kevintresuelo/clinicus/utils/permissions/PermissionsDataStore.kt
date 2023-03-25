package com.kevintresuelo.clinicus.utils.permissions

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.kevintresuelo.clinicus.utils.permissions.PermissionsDataStore.Companion.PERMISSIONS_PREFERENCE_NAME
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class PermissionsDataStore() {

    companion object {
        const val PERMISSIONS_PREFERENCE_NAME = "PermissionsDataStore"
    }

}

val Context.permissions: DataStore<Preferences> by preferencesDataStore(name = PERMISSIONS_PREFERENCE_NAME)

/**
 * Add string data to data Store
 */
fun Context.isPermissionRequestedBefore(permission: String): Boolean {
    val value: Boolean

    runBlocking {
        value = readPermissionBool(permission).first()
    }

    return value
}

suspend fun Context.setPermissionRequested(permission: String) {
    writePermissionBool(permission, true)
}

suspend fun Context.setPermissionAllowed(permission: String) {
    writePermissionBool(permission, false)
}

/**
 * Add Boolean to the data store
 */
suspend fun Context.writePermissionBool(key: String, value: Boolean) {
    permissions.edit { pref -> pref[booleanPreferencesKey(key)] = value }
}

/**
 * Reading the Boolean from the data store
 */
fun Context.readPermissionBool(key: String): Flow<Boolean> {
    return permissions.data.map { pref ->
        pref[booleanPreferencesKey(key)] ?: false
    }
}