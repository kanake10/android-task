package com.example.vero.iteractor

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class AuthDataStore(private val dataStore: DataStore<Preferences>) {

    companion object {
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { it[AUTH_TOKEN_KEY] = token }
    }

    val authToken: Flow<String?> = dataStore.data
        .map { it[AUTH_TOKEN_KEY] }
}