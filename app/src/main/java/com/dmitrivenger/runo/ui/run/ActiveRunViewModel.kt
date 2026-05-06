package com.dmitrivenger.runo.ui.run

import android.app.Application
import android.content.Intent
import android.speech.tts.TextToSpeech
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dmitrivenger.runo.RunoApplication
import com.dmitrivenger.runo.data.preferences.UserPreferences
import com.dmitrivenger.runo.domain.model.LatLng
import com.dmitrivenger.runo.domain.model.Run
import com.dmitrivenger.runo.domain.model.UserProfile
import com.dmitrivenger.runo.service.RunTrackingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class ActiveRunState(
    val elapsedSeconds: Long = 0L,
    val distanceMeters: Float = 0f,
    val currentPaceSecondsPerKm: Float = 0f,
    val routePoints: List<LatLng> = emptyList(),
    val kmPaces: Map<Int, Float> = emptyMap(),
    val isPaused: Boolean = false,
)

class ActiveRunViewModel(application: Application) : AndroidViewModel(application) {

    private val app get() = getApplication<RunoApplication>()

    private val _state = MutableStateFlow(ActiveRunState())
    val state: StateFlow<ActiveRunState> = _state.asStateFlow()

    private var startTime = 0L
    private var timerJob = viewModelScope.launch { runTimer() }
    private var lastKmMark = 0
    private var segmentStartTime = 0L
    private var segmentStartDistance = 0f
    private var tts: TextToSpeech? = null
    private var voiceEnabled = true

    init {
        viewModelScope.launch { voiceEnabled = app.userPreferences.userProfile.first().voiceFeedbackEnabled }
        setupTts()
        startService()
        observeLocation()
    }

    private fun setupTts() {
        tts = TextToSpeech(app) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale.getDefault()
        }
    }

    private fun startService() {
        startTime = System.currentTimeMillis()
        segmentStartTime = startTime
        app.startForegroundService(Intent(app, RunTrackingService::class.java).apply {
            action = RunTrackingService.ACTION_START
        })
    }

    private fun observeLocation() {
        viewModelScope.launch {
            RunTrackingService.locationFlow.collectLatest { latLng ->
                if (latLng == null || _state.value.isPaused) return@collectLatest
                val current = _state.value
                val newPoints = current.routePoints + latLng
                val distance = if (newPoints.size >= 2) calculateTotalDistance(newPoints) else 0f

                val distanceKm = (distance / 1000).toInt()
                val kmPaces = current.kmPaces.toMutableMap()
                if (distanceKm > lastKmMark) {
                    val segmentTime = (System.currentTimeMillis() - segmentStartTime) / 1000f
                    val segmentDist = distance - segmentStartDistance
                    val pace = if (segmentDist > 0) segmentTime / (segmentDist / 1000f) else 0f
                    kmPaces[distanceKm] = pace
                    lastKmMark = distanceKm
                    segmentStartTime = System.currentTimeMillis()
                    segmentStartDistance = distance
                    announceKm(distanceKm, pace)
                }

                val elapsed = current.elapsedSeconds
                val pace = if (distance > 0 && elapsed > 0) elapsed.toFloat() / (distance / 1000f) else 0f

                _state.value = current.copy(
                    routePoints = newPoints,
                    distanceMeters = distance,
                    currentPaceSecondsPerKm = pace,
                    kmPaces = kmPaces,
                )
            }
        }
    }

    private suspend fun runTimer() {
        while (true) {
            kotlinx.coroutines.delay(1000)
            if (!_state.value.isPaused) {
                _state.value = _state.value.copy(elapsedSeconds = _state.value.elapsedSeconds + 1)
            }
        }
    }

    fun togglePause() {
        val pausing = !_state.value.isPaused
        _state.value = _state.value.copy(isPaused = pausing)
        if (!pausing) segmentStartTime = System.currentTimeMillis()
    }

    suspend fun finishRun(userProfile: UserProfile): Long {
        val current = _state.value
        stopService()
        val calories = calculateCalories(userProfile.weightKg, current.elapsedSeconds, current.distanceMeters)
        val avgPace = if (current.distanceMeters > 0 && current.elapsedSeconds > 0)
            current.elapsedSeconds.toFloat() / (current.distanceMeters / 1000f) else 0f
        return app.runRepository.saveRun(
            Run(
                startTime = startTime,
                endTime = System.currentTimeMillis(),
                distanceMeters = current.distanceMeters,
                durationSeconds = current.elapsedSeconds,
                averagePaceSecondsPerKm = avgPace,
                caloriesBurned = calories,
                routePoints = current.routePoints,
                kmPaces = current.kmPaces,
            )
        )
    }

    private fun stopService() {
        timerJob.cancel()
        tts?.shutdown()
        app.startService(Intent(app, RunTrackingService::class.java).apply {
            action = RunTrackingService.ACTION_STOP
        })
    }

    private fun announceKm(km: Int, paceSeconds: Float) {
        if (!voiceEnabled) return
        val m = (paceSeconds / 60).toInt()
        val s = (paceSeconds % 60).toInt()
        tts?.speak("$km kilometre. Pace: $m minutes $s seconds per kilometre.",
            TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onCleared() {
        super.onCleared()
        stopService()
    }

    private fun calculateTotalDistance(points: List<LatLng>): Float {
        var total = 0f
        for (i in 1 until points.size) total += haversineMeters(points[i - 1], points[i])
        return total
    }

    private fun haversineMeters(a: LatLng, b: LatLng): Float {
        val r = 6371000.0
        val lat1 = Math.toRadians(a.latitude)
        val lat2 = Math.toRadians(b.latitude)
        val dLat = Math.toRadians(b.latitude - a.latitude)
        val dLon = Math.toRadians(b.longitude - a.longitude)
        val h = sin(dLat / 2).sq() + cos(lat1) * cos(lat2) * sin(dLon / 2).sq()
        return (2 * r * atan2(sqrt(h), sqrt(1 - h))).toFloat()
    }

    private fun Double.sq() = this * this

    private fun calculateCalories(weightKg: Float, durationSeconds: Long, distanceMeters: Float): Float {
        val weight = if (weightKg > 0) weightKg else 70f
        val durationHours = durationSeconds / 3600.0
        val speedKmh = if (durationHours > 0) (distanceMeters / 1000.0) / durationHours else 8.0
        val met = when {
            speedKmh < 8 -> 8.0; speedKmh < 10 -> 10.0; speedKmh < 12 -> 11.5; else -> 13.0
        }
        return (met * weight * durationHours).toFloat()
    }
}
