package com.maou.simpleweather.data.mapper

import com.maou.simpleweather.data.response.WeatherMainResponse
import com.maou.simpleweather.data.response.WeatherResponse
import com.maou.simpleweather.data.response.WeatherStatusResponse
import com.maou.simpleweather.data.response.WindResponse
import com.maou.simpleweather.model.Weather
import com.maou.simpleweather.model.WeatherMain
import com.maou.simpleweather.model.WeatherStatus
import com.maou.simpleweather.model.Wind

fun WeatherResponse.toModel(): Weather =
    Weather(
        weather.toListWeatherStatusModel(),
        main.toWeatherMainModel(),
        visibility,
        wind.toWindModel(),
        name
    )

private fun List<WeatherStatusResponse>.toListWeatherStatusModel(): List<WeatherStatus> =
    map {
        it.toWeatherStatusModel()
    }

private fun WeatherStatusResponse.toWeatherStatusModel() : WeatherStatus =
    WeatherStatus(
        id = id,
        status =  main,
        description = description,
        icon = icon
    )

private fun WeatherMainResponse.toWeatherMainModel(): WeatherMain =
    WeatherMain(
        temp, pressure, humidity
    )

private fun WindResponse.toWindModel(): Wind =
    Wind(
        speed
    )