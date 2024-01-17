package com.maou.simpleweather.data.controller

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.inject
import org.koin.core.component.KoinComponent

class LocationController(private val context: Context) : KoinComponent {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationRequest: LocationRequest by inject()

    private val locationCallback = LocationCallback { locationResult ->
        _locationState.value = locationResult
    }

    private val _locationState = MutableStateFlow<Location?>(null)

    val locationState: StateFlow<Location?>
        get() = _locationState


    @SuppressLint("MissingPermission")
    fun startRequestLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (exception: SecurityException) {
            Log.e("Location Controller", "Error : " + exception.message)
        }
    }

    fun stopRequestLocationUpdates() {
        _locationState.update {
            null
        }
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

}