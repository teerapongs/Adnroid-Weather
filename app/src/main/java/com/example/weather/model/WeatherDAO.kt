package com.example.weather.model

import com.google.gson.annotations.SerializedName

data class WeatherDAO(
    val id: Long?,
    val timeZone: Long,
    val name: String?,
    val cod: Int?,
    val base: String?,
    val message: String?,
    val coord: Coord?,
    val rain: Rain?,
    val dt: Long?,
    val main: Main?,
    val weather: MutableList<Weather>?,
    val clouds: Clouds?,
    val wind: Wind?,
    val visibility: Long?,
    val sys: SYS?,
)

data class Weather(
    val id: Long?,
    val main: String?,
    val description: String?,
    val icon: String?
)

data class Coord(val lon: Float?, val lat: Float?)

data class Main(
    val temp: Float?,
    val feels_like: Float?,
    val temp_min: Float?,
    val temp_max: Float?,
    val pressure: Float?,
    val humidity: Float?
)

data class Wind(val speed: Float?, val deg: Int?)

data class Rain(@SerializedName("1h") val oneH: Float?)

data class Clouds(val all: Int?)

data class SYS(
    val type: Int?,
    val id: Long?,
    val country: String?,
    val sunrise: Long?,
    val sunset: Long?,
    val pod: String?
)

