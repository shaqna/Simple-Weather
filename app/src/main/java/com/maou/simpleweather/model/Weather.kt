package com.maou.simpleweather.model

data class Weather(
    val weather: List<WeatherStatus>,
    val main: WeatherMain,
    val visibility: Int,
    val wind: Wind,
    val name: String
)

data class WeatherStatus(
    val id: Int,
    val status: String,
    val description: String,
    val icon: String
)

data class WeatherMain(
    val temp: Double,
    val pressure: Int,
    val humidity: Int
)

data class Wind(
    val speed: Double,
)