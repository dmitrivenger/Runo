package com.dmitrivenger.runo.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dmitrivenger.runo.domain.model.LatLng
import com.dmitrivenger.runo.domain.model.Run
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "runs")
data class RunEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTime: Long,
    val endTime: Long,
    val distanceMeters: Float,
    val durationSeconds: Long,
    val averagePaceSecondsPerKm: Float,
    val caloriesBurned: Float,
    val routePointsJson: String,
    val kmPacesJson: String,
    @ColumnInfo(defaultValue = "{}") val paceAnalyticsJson: String = "{}",
) {
    fun toDomain(): Run {
        val gson = Gson()
        val latLngType = object : TypeToken<List<List<Double>>>() {}.type
        val rawPoints: List<List<Double>> = gson.fromJson(routePointsJson, latLngType) ?: emptyList()
        val points = rawPoints.map { LatLng(it[0], it[1]) }

        val paceType = object : TypeToken<Map<Int, Float>>() {}.type
        val kmPaces: Map<Int, Float> = gson.fromJson(kmPacesJson, paceType) ?: emptyMap()
        val paceAnalytics: Map<Int, Float> = gson.fromJson(paceAnalyticsJson, paceType) ?: emptyMap()

        return Run(
            id = id,
            startTime = startTime,
            endTime = endTime,
            distanceMeters = distanceMeters,
            durationSeconds = durationSeconds,
            averagePaceSecondsPerKm = averagePaceSecondsPerKm,
            caloriesBurned = caloriesBurned,
            routePoints = points,
            kmPaces = kmPaces,
            paceAnalytics = paceAnalytics,
        )
    }

    companion object {
        fun fromDomain(run: Run): RunEntity {
            val gson = Gson()
            val rawPoints = run.routePoints.map { listOf(it.latitude, it.longitude) }
            return RunEntity(
                id = run.id,
                startTime = run.startTime,
                endTime = run.endTime,
                distanceMeters = run.distanceMeters,
                durationSeconds = run.durationSeconds,
                averagePaceSecondsPerKm = run.averagePaceSecondsPerKm,
                caloriesBurned = run.caloriesBurned,
                routePointsJson = gson.toJson(rawPoints),
                kmPacesJson = gson.toJson(run.kmPaces),
                paceAnalyticsJson = gson.toJson(run.paceAnalytics),
            )
        }
    }
}
