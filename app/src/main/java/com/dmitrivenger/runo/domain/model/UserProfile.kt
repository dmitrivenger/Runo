package com.dmitrivenger.runo.domain.model

data class UserProfile(
    val name: String = "",
    val age: Int = 0,
    val heightCm: Float = 0f,
    val weightKg: Float = 0f,
    val goal: RunGoal = RunGoal.STAY_ACTIVE,
    val darkMode: Boolean = false,
    val voiceFeedbackEnabled: Boolean = true,
    val useMetricUnits: Boolean = true,
    val voiceIntervalMeters: Int = 1000,
    val language: String = "en",
)

enum class RunGoal(val label: String) {
    STAY_ACTIVE("Stay active"),
    IMPROVE_PACE("Improve pace"),
    BUILD_ENDURANCE("Build endurance"),
    LOSE_WEIGHT("Lose weight"),
}
