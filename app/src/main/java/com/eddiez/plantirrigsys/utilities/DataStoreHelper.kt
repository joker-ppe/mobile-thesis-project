package com.eddiez.plantirrigsys.utilities

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreHelper @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun <T> saveData(keyName: String, value: T) {
        val preferencesKey = when (value) {
            is String -> stringPreferencesKey(keyName)
            is Int -> intPreferencesKey(keyName)
            is Boolean -> booleanPreferencesKey(keyName)
            is Float -> floatPreferencesKey(keyName)
            is Long -> longPreferencesKey(keyName)
            else -> throw IllegalArgumentException("This type can't be saved into DataStore")
        }

        dataStore.edit { preferences ->
            preferences[preferencesKey as Preferences.Key<T>] = value
        }
    }

    fun <T> readData(keyName: String, defaultValue: T): Flow<T> {
        val preferencesKey = when (defaultValue) {
            is String -> stringPreferencesKey(keyName)
            is Int -> intPreferencesKey(keyName)
            is Boolean -> booleanPreferencesKey(keyName)
            is Float -> floatPreferencesKey(keyName)
            is Long -> longPreferencesKey(keyName)
            else -> throw IllegalArgumentException("This type can't be read from DataStore")
        }

        return dataStore.data.map { preferences ->
            preferences[preferencesKey as Preferences.Key<T>] ?: defaultValue
        }
    }

    suspend fun clearData() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
