package com.example.weather.model

data class ForecastDAO(
    val cod: Int?,
    val message: String?,
    val cnt: Int?,
    val list: MutableList<Forecast>,
    val city: City,
    var tempType: Int?,
    var _tempType: String?
)

data class Forecast(
    val dt: Long?,
    val main: Main?,
    val weather: MutableList<Weather>?,
    val clouds: Clouds?,
    val wind: Wind?,
    val visibility: Long?,
    val sys: SYS?,
    val pop: Float?,
    val dt_txt: String?
)

data class City(
    val id: Long?,
    val name: String?,
    val coord: Coord?,
    val country: String?,
    val population: Long?,
    val timeZone: Long,
    val sunrise: Long?,
    val sunset: Long?
)