package com.maou.simpleweather.presentation

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maou.simpleweather.data.controller.LocationController
import com.maou.simpleweather.data.repository.WeatherRepository
import com.maou.simpleweather.model.Weather
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModelOf

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationController: LocationController
) : ViewModel() {

    private val _state: MutableStateFlow<WeatherUiState> = MutableStateFlow(WeatherUiState.Init)
    val state: StateFlow<WeatherUiState> get() = _state

    private val _locationState = MutableStateFlow<LocationUiState>(LocationUiState.Loading)
    val locationState: StateFlow<LocationUiState> = _locationState

    private fun showLoading() {
        _state.value = WeatherUiState.Loading(true)
    }

    private fun hideLoading() {
        _state.value = WeatherUiState.Loading(false)
    }

    private fun showMessage(message: String) {
        _state.value = WeatherUiState.ShowMessage(message)
    }

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherRepository.fetchWeather(lat, lon)
                .onStart {
                    showLoading()
                }
                .catch {
                    hideLoading()
                    showMessage(it.message.toString())
                }
                .collect { result ->
                    result.fold(
                        onSuccess = {
                            _state.value = WeatherUiState.Success(it)
                        },
                        onFailure = {
                            showMessage(it.message.toString())
                        }
                    )
                }
        }
    }

    fun startRequestLocationUpdates() {
        locationController.startRequestLocationUpdates()
        viewModelScope.launch {
            locationController.locationState.collectLatest {
                it?.let {location ->
                    _locationState.value = LocationUiState.OnLocationResult(location)
                }
            }
        }
    }

    fun stopRequestLocationUpdates() {
        locationController.stopRequestLocationUpdates()
    }

    companion object {
        fun inject() = module {
            viewModelOf(::WeatherViewModel)
        }
    }
}

sealed class WeatherUiState {
    data object Init : WeatherUiState()
    data class Loading(val isLoading: Boolean) : WeatherUiState()
    data class Success(val weather: Weather) : WeatherUiState()
    data class ShowMessage(val message: String) : WeatherUiState()
}

sealed class LocationUiState {
    data object Loading: LocationUiState()
    data class OnLocationResult(val location: Location? = null): LocationUiState()
}