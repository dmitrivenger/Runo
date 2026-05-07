package com.dmitrivenger.runo.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dmitrivenger.runo.domain.model.RunGoal
import com.dmitrivenger.runo.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    private object Keys {
        val NAME = stringPreferencesKey("name")
        val AGE = intPreferencesKey("age")
        val HEIGHT_CM = floatPreferencesKey("height_cm")
        val WEIGHT_KG = floatPreferencesKey("weight_kg")
        val GOAL = stringPreferencesKey("goal")
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val VOICE_FEEDBACK = booleanPreferencesKey("voice_feedback")
        val USE_METRIC = booleanPreferencesKey("use_metric")
        val VOICE_INTERVAL_KM = intPreferencesKey("voice_interval_km")
    }

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { prefs ->
        UserProfile(
            name = prefs[Keys.NAME] ?: "",
            age = prefs[Keys.AGE] ?: 0,
            heightCm = prefs[Keys.HEIGHT_CM] ?: 0f,
            weightKg = prefs[Keys.WEIGHT_KG] ?: 0f,
            goal = RunGoal.entries.find { it.name == prefs[Keys.GOAL] } ?: RunGoal.STAY_ACTIVE,
            darkMode = prefs[Keys.DARK_MODE] ?: true,
            voiceFeedbackEnabled = prefs[Keys.VOICE_FEEDBACK] ?: true,
            useMetricUnits = prefs[Keys.USE_METRIC] ?: true,
            voiceIntervalKm = prefs[Keys.VOICE_INTERVAL_KM] ?: 1,
        )
    }

    val isOnboardingDone: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_DONE] == true
    }

    suspend fun saveProfile(profile: UserProfile) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NAME] = profile.name
            prefs[Keys.AGE] = profile.age
            prefs[Keys.HEIGHT_CM] = profile.heightCm
            prefs[Keys.WEIGHT_KG] = profile.weightKg
            prefs[Keys.GOAL] = profile.goal.name
            prefs[Keys.DARK_MODE] = profile.darkMode
            prefs[Keys.VOICE_FEEDBACK] = profile.voiceFeedbackEnabled
            prefs[Keys.USE_METRIC] = profile.useMetricUnits
            prefs[Keys.VOICE_INTERVAL_KM] = profile.voiceIntervalKm
        }
    }

    suspend fun setOnboardingDone() {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_DONE] = true
        }
    }
}
