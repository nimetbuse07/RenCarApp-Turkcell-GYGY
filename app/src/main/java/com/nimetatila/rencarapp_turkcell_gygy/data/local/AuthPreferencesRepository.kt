package com.nimetatila.rencarapp_turkcell_gygy.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Singleton
class AuthPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val USER_PHONE_KEY = stringPreferencesKey("user_phone")
        private val USER_FULL_NAME_KEY = stringPreferencesKey("user_full_name")
        private val USER_ROLE_KEY = stringPreferencesKey("user_role")
    }

    val accessToken: Flow<String?> = context.authDataStore.data.map { it[ACCESS_TOKEN_KEY] }
    val refreshToken: Flow<String?> = context.authDataStore.data.map { it[REFRESH_TOKEN_KEY] }

    val userRole: Flow<String?> = context.authDataStore.data.map { it[USER_ROLE_KEY] }
    val userPhone: Flow<String?> = context.authDataStore.data.map { it[USER_PHONE_KEY] }
    val userFullName: Flow<String?> = context.authDataStore.data.map { it[USER_FULL_NAME_KEY] }

    suspend fun saveAuthData(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        phone: String,
        fullName: String,
        role: String
    ) {
        context.authDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_ID_KEY] = userId
            preferences[USER_EMAIL_KEY] = email
            preferences[USER_PHONE_KEY] = phone
            preferences[USER_FULL_NAME_KEY] = fullName
            preferences[USER_ROLE_KEY] = role
        }
    }

    suspend fun clearAuthData() {
        context.authDataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_EMAIL_KEY)
            preferences.remove(USER_PHONE_KEY)
            preferences.remove(USER_FULL_NAME_KEY)
            preferences.remove(USER_ROLE_KEY)
        }
    }

    suspend fun updateRole(role: String) {
        context.authDataStore.edit { preferences ->
            preferences[USER_ROLE_KEY] = role
        }
    }
}
