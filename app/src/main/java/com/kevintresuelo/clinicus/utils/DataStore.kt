package com.kevintresuelo.clinicus.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStore {

    companion object {
        const val PREFERENCE_NAME = "LoremDataStore"
    }

    class PreferenceKeys {

        companion object {
            const val PATIENT_UID = "patient_uid"
            const val PATIENT_NAME = "patient_name"
            const val PATIENT_CODE = "patient_code"
            const val PATIENT_DOCTOR_UID = "patient_doctor_uid"
            const val DEVICE_UID = "device_uid"
            const val HAS_FINISHED_ONBOARDING = "has_finished_onboarding"

            const val SETTINGS_DEFAULT_INSTRUCTIONS_VIEW_MODE = "settings_default_instructions_view_mode"
            const val SETTINGS_VOICEOVER_ENABLED = "settings_voiceover_enabled"
            const val SETTINGS_AUTOBRIGHTNESS_ENABLED = "settings_autobrightness_enabled"
            const val SETTINGS_NOTIFICATIONS_ENABLED = "settings_notifications_enabled"
            const val SETTINGS_NOTIFICATIONS_EXERCISE_REMINDER_HOUR = "settings_notifications_exercise_reminder_hour"
            const val SETTINGS_NOTIFICATIONS_EXERCISE_REMINDER_MINUTE = "settings_notifications_exercise_reminder_minute"
        }

    }

}

//Instance of DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = com.kevintresuelo.clinicus.utils.DataStore.PREFERENCE_NAME)

/**
 * Add string data to data Store
 */
suspend fun Context.writeString(key: String, value: String) {
    dataStore.edit { pref -> pref[stringPreferencesKey(key)] = value }
}


/**
 * Read string from the data store preferences
 */
fun Context.readString(key: String, default: String = ""): Flow<String> {
    return dataStore.data.map{ pref ->
        pref[stringPreferencesKey(key)] ?: default
    }
}

/**
 * Deleting a String from the data store
 */
suspend fun Context.deleteString(key: String) {
    dataStore.edit {
        it.remove(stringPreferencesKey(key))
    }
}

/**
 * Add Integer to the data store
 */
suspend fun Context.writeInt(key: String, value: Int) {
    dataStore.edit { pref -> pref[intPreferencesKey(key)] = value }
}

/**
 * Reading the Int value from the data store
 */
fun Context.readInt(key: String, default: Int = 0): Flow<Int> {
    return dataStore.data.map { pref ->
        pref[intPreferencesKey(key)] ?: default
    }
}

/**
 * Deleting an Int from the data store
 */
suspend fun Context.deleteInt(key: String) {
    dataStore.edit {
        it.remove(intPreferencesKey(key))
    }
}

/**
 * Adding Double to the data store
 */
suspend fun Context.writeDouble(key: String, value: Double) {
    dataStore.edit { pref -> pref[doublePreferencesKey(key)] = value }
}

/**
 * Reading the double value from the data store
 */
fun Context.readDouble(key: String, default: Double = 0.0): Flow<Double> {
    return dataStore.data.map { pref ->
        pref[doublePreferencesKey(key)] ?: default
    }
}

/**
 * Deleting a Double from the data store
 */
suspend fun Context.deleteDouble(key: String) {
    dataStore.edit {
        it.remove(doublePreferencesKey(key))
    }
}

/**
 * Add Long to the data store
 */
suspend fun Context.writeLong(key: String, value: Long) {
    dataStore.edit { pref -> pref[longPreferencesKey(key)] = value }
}

/**
 * Reading the long from the data store
 */
fun Context.readLong(key: String, default: Long = 0L): Flow<Long> {
    return dataStore.data.map { pref ->
        pref[longPreferencesKey(key)] ?: default
    }
}

/**
 * Deleting a Long from the data store
 */
suspend fun Context.deleteLong(key: String) {
    dataStore.edit {
        it.remove(longPreferencesKey(key))
    }
}


/**
 * Add Boolean to the data store
 */
suspend fun Context.writeBool(key: String, value: Boolean) {
    dataStore.edit { pref -> pref[booleanPreferencesKey(key)] = value }
}

/**
 * Reading the Boolean from the data store
 */
fun Context.readBool(key: String, default: Boolean = false): Flow<Boolean> {
    return dataStore.data.map { pref ->
        pref[booleanPreferencesKey(key)] ?: default
    }
}

/**
 * Deleting a Boolean from the data store
 */
suspend fun Context.deleteBool(key: String) {
    dataStore.edit {
        it.remove(booleanPreferencesKey(key))
    }
}