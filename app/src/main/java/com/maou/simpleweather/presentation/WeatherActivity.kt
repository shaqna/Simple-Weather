package com.maou.simpleweather.presentation

import android.content.IntentSender
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.model.LatLng
import com.maou.simpleweather.R
import com.maou.simpleweather.databinding.ActivityWeatherBinding
import com.maou.simpleweather.helper.GpsHelper
import com.maou.simpleweather.helper.PermissionHelper
import com.maou.simpleweather.model.Weather
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class WeatherActivity : AppCompatActivity(),
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private val binding: ActivityWeatherBinding by lazy {
        ActivityWeatherBinding.inflate(layoutInflater)
    }

    private val viewModel: WeatherViewModel by viewModel()
    private val locationRequest: LocationRequest by inject()
    private val permissionHelper: PermissionHelper by inject()
    private val gpsHelper: GpsHelper by inject()

    private val resolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                Log.i("WeatherActivity", "onActivityResult: All location settings are satisfied.")
                viewModel.startRequestLocationUpdates()
            }

            RESULT_CANCELED ->
                Toast.makeText(
                    this@WeatherActivity,
                    "Anda harus mengaktifkan GPS untuk menggunakan aplikasi ini!",
                    Toast.LENGTH_SHORT
                ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadKoinModules(WeatherViewModel.inject())

        requestLocationPermissions()
        observeDeviceLocation()
        observeWeather()
    }

    private fun fetchWeather(location: Location) {
        val userLocation = LatLng(location.latitude, location.longitude)
        viewModel.fetchWeather(userLocation.latitude, userLocation.longitude)
    }

    private fun observeWeather() {
        viewModel.state
            .onEach {
                handleWeatherState(it)
            }
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .launchIn(lifecycleScope)
    }

    private fun handleWeatherState(state: WeatherUiState) {
        when (state) {
            WeatherUiState.Init -> Unit
            is WeatherUiState.Loading -> {
                Log.d("OnLoading", state.isLoading.toString())
                showPlaceHolder()
            }

            is WeatherUiState.ShowMessage -> {
                Log.d("OnMessage", state.message)
            }

            is WeatherUiState.Success -> {
                hidePlaceHolder()
                setWeather(state.weather)
            }
        }
    }

    private fun showPlaceHolder() {
        with(binding) {
            weatherPlaceholder.weatherShimmerLayout.visibility = View.VISIBLE
            weatherPlaceholder.weatherShimmerLayout.startShimmer()
        }
    }

    private fun hidePlaceHolder() {
        with(binding) {
            weatherPlaceholder.weatherShimmerLayout.stopShimmer()
            weatherPlaceholder.weatherShimmerLayout.visibility = View.GONE
        }
    }

    private fun setWeather(weather: Weather) {
        binding.weatherMain.root.visibility = View.VISIBLE
        with(binding.weatherMain) {
            tvCity.text = weather.name
            tvTemperature.text = getString(R.string.temperature, weather.main.temp)
            tvWeatherDescription.text = weather.weather[0].description
            tvWindSpeedValue.text = weather.wind.speed.toString()
            tvHumidityValue.text = weather.main.humidity.toString()
            tvVisibilityValue.text = weather.visibility.toString()
            tvAirPressureValue.text = weather.main.pressure.toString()

            setWeatherIcon(weather.weather[0].icon)
        }
    }

    private fun setWeatherIcon(dayIcon: String) {
        val datIconUrl = "https://openweathermap.org/img/wn/$dayIcon@2x.png"
        Glide.with(this@WeatherActivity).load(datIconUrl).into(binding.weatherMain.ivWetherIcon)
    }

    private fun observeDeviceLocation() {
        viewModel.locationState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach { state ->
                handleLocationState(state)
            }.launchIn(lifecycleScope)
    }

    private fun handleLocationState(state: LocationUiState) {
        when (state) {
            is LocationUiState.Loading -> {
                showPlaceHolder()
            }

            is LocationUiState.OnLocationResult -> {
                hidePlaceHolder()
                state.location?.let { currentLocation ->
                    fetchWeather(currentLocation)
                    viewModel.stopRequestLocationUpdates()

                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        if (gpsHelper.isGpsEnable()) {
            Log.d("WeatherActivity", "requestLocationPermissions: GPS is active")
            viewModel.startRequestLocationUpdates()
        } else {
            enableGps()
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d("WeatherActivity", "onPermissionsDenied: $requestCode : ${perms.size}")
    }

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d("WeatherActivity", "onRationaleAccepted: $requestCode")
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d("WeatherActivity", "onRationaleDenied: $requestCode")
    }

    @AfterPermissionGranted(RC_LOCATION_PERM)
    private fun requestLocationPermissions() {
        if (permissionHelper.hasLocationPermission()) {
            // do enable GPS
            if (gpsHelper.isGpsEnable()) {
                Log.d("WeatherActivity", "requestLocationPermissions: GPS is active")
                viewModel.startRequestLocationUpdates()
            } else {
                enableGps()
            }
        } else {
            permissionHelper.requestLocationPermission(this, RC_LOCATION_PERM)
        }
    }

    private fun enableGps() {
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                Log.d(
                    "WeatherActivity",
                    "LocationSettingsResponse: ${it.locationSettingsStates?.isGpsPresent}"
                )
                viewModel.startRequestLocationUpdates()
            }
            .addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(e.resolution).build()
                        )

                    } catch (sendEx: IntentSender.SendIntentException) {
                        Toast.makeText(this@WeatherActivity, sendEx.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    companion object {
        private const val RC_LOCATION_PERM = 111
    }

}