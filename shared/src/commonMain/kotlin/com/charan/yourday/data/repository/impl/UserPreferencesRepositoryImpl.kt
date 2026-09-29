package com.charan.yourday.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.charan.yourday.data.repository.UserPreferencesRepository
import com.charan.yourday.utils.WeatherUnitsEnums
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : UserPreferencesRepository {

    companion object {
        private val WEATHER_UNITS_KEY = stringPreferencesKey("weather_units")
        private val TODOIST_ACCESS_TOKEN_KEY = stringPreferencesKey("todoist_access_token")
        private val SHOULD_SHOW_ONBOARDING_KEY = booleanPreferencesKey("should_show_onboarding")
    }

    override val weatherUnits: Flow<String>
        get() = dataStore.data.map { preferences ->
            preferences[WEATHER_UNITS_KEY] ?: WeatherUnitsEnums.C.name
        }

    override val weatherUnitEnum: Flow<WeatherUnitsEnums>
        get() = dataStore.data.map { preferences ->
            preferences[WEATHER_UNITS_KEY]?.let {
                runCatching { WeatherUnitsEnums.valueOf(it) }.getOrDefault(WeatherUnitsEnums.C)
            } ?: WeatherUnitsEnums.C
        }

    override val todoistAccessToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[TODOIST_ACCESS_TOKEN_KEY]
        }

    override val shouldShowOnboarding: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            preferences[SHOULD_SHOW_ONBOARDING_KEY] ?: true
        }

    override suspend fun setWeatherUnits(weatherUnits: String) {
        dataStore.edit { preferences ->
            preferences[WEATHER_UNITS_KEY] = weatherUnits
        }
    }

    override suspend fun setWeatherUnit(weatherUnit: WeatherUnitsEnums) {
        dataStore.edit { preferences ->
            preferences[WEATHER_UNITS_KEY] = weatherUnit.name
        }
    }

    override suspend fun setTodoistAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TODOIST_ACCESS_TOKEN_KEY] = token
        }
    }

    override suspend fun clearTodoistAccessToken() {
        dataStore.edit { preferences ->
            preferences.remove(TODOIST_ACCESS_TOKEN_KEY)
        }
    }

    override suspend fun setShouldShowOnboarding(shouldShow: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOULD_SHOW_ONBOARDING_KEY] = shouldShow
        }
    }
}
