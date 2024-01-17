package com.maou.simpleweather.data.repository

import com.maou.simpleweather.BuildConfig
import com.maou.simpleweather.data.mapper.toModel
import com.maou.simpleweather.data.service.ApiService
import com.maou.simpleweather.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn

class WeatherRepository(
    private val apiService: ApiService
) {
    fun fetchWeather(
        lat: Double,
        lon: Double,
    ): Flow<Result<Weather>> {
        return flow {
            try {
                val response = apiService.getWeather(lat, lon, BuildConfig.APP_ID, "ID", "metric")
                emit(Result.success(response.toModel()))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }.catch { e ->
            emit(Result.failure(e))
        }.flowOn(Dispatchers.IO)
    }

}