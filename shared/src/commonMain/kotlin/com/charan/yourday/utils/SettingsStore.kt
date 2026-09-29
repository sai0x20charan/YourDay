package com.charan.yourday.utils

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class UserPreferencesStore(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private const val TODOIST_ACCESS_TOKEN = "todoist_access_token"
        private const val TODO_PROVIDER = "todo_provider"
        private const val WEATHER_UNIT = "weather_units"
        private const val SHOULD_SHOW_ONBOARDING = "should_show_onboarding"
        private val weatherUnitsPref = stringPreferencesKey(WEATHER_UNIT)
        private val todoTokenPref = stringPreferencesKey(TODOIST_ACCESS_TOKEN)
        private val should_show_onboarding = booleanPreferencesKey(SHOULD_SHOW_ONBOARDING)
    }

    val weatherUnits: Flow<String?> = dataStore.data.map { preferences ->
        preferences[weatherUnitsPref] ?: WeatherUnits.C
    }

    suspend fun setWeatherUnits(units: String) {
        dataStore.edit { preferences ->
            preferences[weatherUnitsPref] = units
        }
    }

    val todoistAccessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[todoTokenPref]
    }
    val shouldShowOnboarding: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[should_show_onboarding] ?: true
    }

    suspend fun setTodoistAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[todoTokenPref] = token
        }
    }

    suspend fun setShouldShowOnboarding(shouldShow : Boolean){
        dataStore.edit { preferences ->
            preferences[should_show_onboarding] = shouldShow
        }
    }
    suspend fun deleteTodoistToken(){
        dataStore.edit {  preference ->
            preference.remove(todoTokenPref)

        }
    }


}
