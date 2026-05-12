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
    val caloriesBurned: Float = 0f,
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

    // Voice announcement tracking (at configured interval: 500m, 1km, 2km, 5km)
    private var lastAnnouncedMark = 0
    private var voiceSegmentStartTime = 0L
    private var voiceSegmentStartDist = 0f
    private var voiceIntervalMeters = 1000

    // Km-pace chart tracking (always at every 1 km)
    private var lastKmMark = 0
    private var kmSegmentStartTime = 0L
    private var kmSegmentStartDist = 0f

    // Real-time pace tracking (rolling window)
    private var lastLocationTimeMs = 0L
    private var lastLocationDist = 0f
    private val recentPaces = ArrayDeque<Float>()

    private var userWeightKg = 70f

    private var tts: TextToSpeech? = null
    private var voiceEnabled = true

    init {
        viewModelScope.launch {
            val profile = app.userPreferences.userProfile.first()
            voiceEnabled = profile.voiceFeedbackEnabled
            voiceIntervalMeters = profile.voiceIntervalMeters
            userWeightKg = if (profile.weightKg > 0) profile.weightKg else 70f
        }
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
        val now = System.currentTimeMillis()
        startTime = now
        voiceSegmentStartTime = now
        kmSegmentStartTime = now
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
                val now = System.currentTimeMillis()

                val kmPaces = current.kmPaces.toMutableMap()

                // Voice interval announcement
                val intervalMark = (distance / voiceIntervalMeters).toInt()
                if (intervalMark > lastAnnouncedMark) {
                    val segTime = (now - voiceSegmentStartTime) / 1000f
                    val segDist = distance - voiceSegmentStartDist
                    val segPace = if (segDist > 0) segTime / (segDist / 1000f) else 0f
                    lastAnnouncedMark = intervalMark
                    voiceSegmentStartTime = now
                    voiceSegmentStartDist = distance
                    announceDistance(intervalMark * voiceIntervalMeters / 1000f, segPace)
                }

                // Km pace chart recording
                val distanceKm = (distance / 1000).toInt()
                if (distanceKm > lastKmMark) {
                    val segTime = (now - kmSegmentStartTime) / 1000f
                    val segDist = distance - kmSegmentStartDist
                    val kmPace = if (segDist > 0) segTime / (segDist / 1000f) else 0f
                    kmPaces[distanceKm] = kmPace
                    lastKmMark = distanceKm
                    kmSegmentStartTime = now
                    kmSegmentStartDist = distance
                }

                // Rolling instantaneous pace (updated only when meaningful movement detected)
                val distDelta = distance - lastLocationDist
                if (distDelta >= 5f && lastLocationTimeMs > 0L) {
                    val timeDelta = (now - lastLocationTimeMs) / 1000f
                    val instantPace = (timeDelta / (distDelta / 1000f)).coerceIn(60f, 1800f)
                    recentPaces.addLast(instantPace)
                    if (recentPaces.size > 5) recentPaces.removeFirst()
                }
                if (distDelta >= 5f || lastLocationTimeMs == 0L) {
                    lastLocationTimeMs = now
                    lastLocationDist = distance
                }
                val smoothedPace = if (recentPaces.isNotEmpty())
                    recentPaces.average().toFloat() else current.currentPaceSecondsPerKm

                val elapsed = current.elapsedSeconds
                val liveCalories = calculateCalories(userWeightKg, elapsed, distance)

                _state.value = current.copy(
                    routePoints = newPoints,
                    distanceMeters = distance,
                    currentPaceSecondsPerKm = smoothedPace,
                    caloriesBurned = liveCalories,
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
        if (!pausing) {
            val now = System.currentTimeMillis()
            val dist = _state.value.distanceMeters
            voiceSegmentStartTime = now
            voiceSegmentStartDist = dist
            kmSegmentStartTime = now
            kmSegmentStartDist = dist
        }
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

    private fun announceDistance(distKm: Float, paceSeconds: Float) {
        if (!voiceEnabled) return
        val m = (paceSeconds / 60).toInt()
        val s = (paceSeconds % 60).toInt()
        val distLabel = when {
            distKm < 1f -> "${(distKm * 1000).toInt()} metres"
            distKm == distKm.toLong().toFloat() -> "${distKm.toLong()} kilometre${if (distKm.toLong() != 1L) "s" else ""}"
            else -> "$distKm kilometres"
        }
        tts?.speak("$distLabel. Pace: $m minutes $s seconds per kilometre.",
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
        if (durationSeconds <= 0 || distanceMeters <= 0) return 0f
        val weight = if (weightKg > 0) weightKg else 70f
        val durationHours = durationSeconds / 3600.0
        val speedKmh = (distanceMeters / 1000.0) / durationHours
        val met = when {
            speedKmh < 3.0  -> 2.5
            speedKmh < 5.0  -> 3.5
            speedKmh < 6.5  -> 5.0
            speedKmh < 8.0  -> 7.0
            speedKmh < 10.0 -> 8.5
            speedKmh < 12.0 -> 10.0
            speedKmh < 14.0 -> 11.5
            else             -> 13.0
        }
        return (met * weight * durationHours).toFloat()
    }
}
