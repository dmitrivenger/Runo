package com.dmitrivenger.runo.domain.model

data class LocationUpdate(
    val latLng: LatLng,
    val speedMps: Float,    // m/s from GPS Doppler — accurate even at walking pace
    val accuracyM: Float,   // horizontal accuracy radius in metres
)
