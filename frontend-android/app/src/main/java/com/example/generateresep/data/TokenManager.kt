package com.example.generateresep.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val TOKEN_KEY = stringPreferencesKey("auth_token")
    private val USERNAME_KEY = stringPreferencesKey("username")

    val authToken: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val username: Flow<String?> = context.dataStore.data.map { it[USERNAME_KEY] }

    suspend fun saveToken(token: String, user: String) {
        context.dataStore.edit {
            it[TOKEN_KEY] = token
            it[USERNAME_KEY] = user
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit {
            it.clear()
        }
    }
}
