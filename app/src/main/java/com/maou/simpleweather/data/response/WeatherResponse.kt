package com.maou.simpleweather.data.response

import com.squareup.moshi.Json

data class WeatherResponse(
    @field:Json(name = "weather")
    val weather: List<WeatherStatusResponse>,

    @field:Json(name = "main")
    val main: WeatherMainResponse,

    @field:Json(name = "visibility")
    val visibility: Int,

    @field:Json(name = "wind")
    val wind: WindResponse,

    @field:Json(name = "name")
    val name: String
)

data class WeatherStatusResponse(

    @field:Json(name = "id")
    val id: Int,
    @field:Json(name = "main")
    val main: String,

    @field:Json(name = "description")
    val description: String,

    @field:Json(name = "icon")
    val icon: String
)

data class WeatherMainResponse(
    @field:Json(name = "temp")
    val temp: Double,
    @field:Json(name = "pressure")
    val pressure: Int,
    @field:Json(name = "humidity")
    val humidity: Int
)

data class WindResponse(
    @field:Json(name = "speed")
    val speed: Double,
)
