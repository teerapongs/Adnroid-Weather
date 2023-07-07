package com.example.weather.ui.main.data

import com.example.weather.service.WeatherService

class MainDataSource(private val service: WeatherService) {

    suspend fun getWeatherNow(name: String, units: String) = service.getWeatherNow(units = units, q = name)
    suspend fun getWeatherNowWithLocation(lat: String, long: String, units: String) = service.getWeatherNowWithLocation(lat = lat, lon = long, units = units)
    suspend fun getForecast(name: String, units: String) = service.getForecast(units = units, q = name)

}