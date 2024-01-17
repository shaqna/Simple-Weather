package com.maou.simpleweather.data.controller

import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult

class LocationCallback(
    private val onLocationUpdate: (Location) -> Unit
): LocationCallback() {
    override fun onLocationResult(locationResult: LocationResult) {
        super.onLocationResult(locationResult)

        for(location in locationResult.locations) {
            onLocationUpdate(location)
        }
    }
}