package com.maou.simpleweather.data.service

import com.maou.simpleweather.data.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("/data/2.5/weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") appId: String,
        @Query("lang") language: String,
        @Query("units") units: String
    ): WeatherResponse
}