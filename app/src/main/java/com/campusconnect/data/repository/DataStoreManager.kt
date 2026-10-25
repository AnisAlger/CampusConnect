package com.campusconnect.data.repository

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.campusconnect.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_preferences")

class DataStoreManager(private val context: Context) {

    companion object {
        val DEFAULT_OPENROUTER_KEY: String = BuildConfig.OPENROUTER_API_KEY
        const val DEFAULT_MODEL = "openai/gpt-3.5-turbo-16k"

        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val USER_NAME = stringPreferencesKey("user_name")
        val OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
        val OPENROUTER_MODEL = stringPreferencesKey("openrouter_model")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[DARK_MODE] = enabled }
    }

    val userName: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: "Étudiant"
    }

    suspend fun setUserName(name: String) {
        context.dataStore.edit { it[USER_NAME] = name }
    }

    val openRouterApiKey: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[OPENROUTER_API_KEY] ?: DEFAULT_OPENROUTER_KEY
    }

    suspend fun setOpenRouterApiKey(apiKey: String) {
        context.dataStore.edit { it[OPENROUTER_API_KEY] = apiKey.trim() }
    }

    val openRouterModel: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[OPENROUTER_MODEL] ?: DEFAULT_MODEL
    }

    suspend fun setOpenRouterModel(model: String) {
        context.dataStore.edit { it[OPENROUTER_MODEL] = model.trim() }
    }
}
