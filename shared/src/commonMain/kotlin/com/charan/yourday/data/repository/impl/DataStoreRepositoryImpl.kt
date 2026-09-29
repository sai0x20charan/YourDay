package com.charan.yourday.data.repository.impl

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.charan.yourday.data.model.TodoData
import com.charan.yourday.data.model.WeatherData
import com.charan.yourday.data.repository.DataStoreRepository
import com.charan.yourday.utils.WeatherUnitsEnums
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class DataStoreRepositoryImpl(
    private val dataStore : DataStore<Preferences>
) : DataStoreRepository {
    companion object {
        private val WEATHER_UNITS_KEY = stringPreferencesKey("weather_units")
        private val TODOIST_ACCESS_TOKEN_KEY = stringPreferencesKey("todoist_access_token")
        private val SHOULD_SHOW_ONBOARDING_KEY = booleanPreferencesKey("should_show_onboarding")
        private val WEATHER_DATA_KEY = stringPreferencesKey("weather_data")

        private val TODO_DATA_KEY = stringPreferencesKey("todo_data")
    }

    override val weatherUnit: Flow<WeatherUnitsEnums>
        get() = dataStore.data.map { preferences ->
            preferences[WEATHER_UNITS_KEY]?.let { WeatherUnitsEnums.valueOf(it) } ?: WeatherUnitsEnums.C
        }

    override suspend fun setWeatherUnit(unit: WeatherUnitsEnums) {
        dataStore.edit { preferences ->
            preferences[WEATHER_UNITS_KEY] = unit.name
        }
    }

    override val todoistAccessToken: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[TODOIST_ACCESS_TOKEN_KEY]
        }

    override suspend fun setTodoistAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TODOIST_ACCESS_TOKEN_KEY] = token
        }
    }

    override val shouldShowOnboarding: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            preferences[SHOULD_SHOW_ONBOARDING_KEY] ?: true
        }

    override suspend fun setShouldShowOnboarding(shouldShow: Boolean) {
        dataStore.edit { preferences ->
            preferences[SHOULD_SHOW_ONBOARDING_KEY] = shouldShow
        }
    }

    override val weatherData: Flow<WeatherData>
        get() = dataStore.data.map { preferences ->
            val weatherData = preferences[WEATHER_DATA_KEY]
            if(weatherData != null){
                Json.decodeFromString<WeatherData>(weatherData)
            } else {
                WeatherData()
            }
        }

    override suspend fun setWeatherData(weatherData: WeatherData) {
        val data = Json.encodeToString(weatherData)
        dataStore.edit { preferences ->
            preferences[WEATHER_DATA_KEY] = data
        }
    }

    override val todoData: Flow<List<TodoData>>
        get() = dataStore.data.map { preferences ->
            val todoData = preferences[TODO_DATA_KEY]
            if(todoData != null){
                Json.decodeFromString<List<TodoData>>(todoData)
            } else {
                emptyList()
            }
        }

    override suspend fun setTodoData(todoData: List<TodoData>) {
        val data = Json.encodeToString(todoData)
        dataStore.edit { preferences ->
            preferences[TODO_DATA_KEY] = data
        }
    }
}