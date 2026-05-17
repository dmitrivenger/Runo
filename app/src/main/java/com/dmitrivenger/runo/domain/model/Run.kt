package com.dmitrivenger.runo.domain.model

data class Run(
    val id: Long = 0,
    val startTime: Long = 0L,
    val endTime: Long = 0L,
    val distanceMeters: Float = 0f,
    val durationSeconds: Long = 0L,
    val averagePaceSecondsPerKm: Float = 0f,
    val caloriesBurned: Float = 0f,
    val routePoints: List<LatLng> = emptyList(),
    val kmPaces: Map<Int, Float> = emptyMap(),
    val paceAnalytics: Map<Int, Float> = emptyMap(), // key = 100m mark, value = pace sec/km for that segment
) {
    val distanceKm: Float get() = distanceMeters / 1000f

    fun formattedDuration(): String {
        val h = durationSeconds / 3600
        val m = (durationSeconds % 3600) / 60
        val s = durationSeconds % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
    }

    fun formattedPace(): String {
        if (averagePaceSecondsPerKm <= 0f) return "--:--"
        val m = (averagePaceSecondsPerKm / 60).toInt()
        val s = (averagePaceSecondsPerKm % 60).toInt()
        return "%d:%02d /km".format(m, s)
    }
}
