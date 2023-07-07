package com.example.weather.ui.main.data

import com.example.weather.R
import com.example.weather.model.ForecastDAO
import com.example.weather.model.ResultResponse
import com.example.weather.model.WeatherDAO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

interface MainRepositoryInterface {
    fun getWeatherNow(name: String, units: String): Flow<ResultResponse<WeatherDAO>>
    fun getWeatherNowWithLocation(lat: String, long: String, units: String): Flow<ResultResponse<WeatherDAO>>
    fun getForecast(name: String, units: String): Flow<ResultResponse<ForecastDAO>>
}

class MainRepository(private val dataSource: MainDataSource): MainRepositoryInterface {

    override fun getWeatherNow(name: String, units: String) = flow {
        try {
            val response = dataSource.getWeatherNow(name = name, units = units)
            if (response.isSuccessful) {
                val result = response.body()
                emit(ResultResponse(success = result))
            } else {
                emit(ResultResponse(errorInt = R.string.city_not_found))
            }
        } catch (e: IOException) { emit(ResultResponse(error = e.message))
        } catch (e: Throwable) { emit(ResultResponse(error = e.message))
        } catch (e: UnknownHostException) { emit(ResultResponse(error = e.message))
        } catch (e: SocketTimeoutException) { emit(ResultResponse(error = e.message))
        } catch (e: Exception) { emit(ResultResponse(error = e.message)) }
    }

    override fun getWeatherNowWithLocation(lat: String, long: String, units: String) = flow {
        try {
            val response = dataSource.getWeatherNowWithLocation(lat = lat, long = long ,units = units)
            if (response.isSuccessful) {
                val result = response.body()
                emit(ResultResponse(success = result))
            } else {
                emit(ResultResponse(errorInt = R.string.location_not_found))
            }
        } catch (e: IOException) { emit(ResultResponse(error = e.message))
        } catch (e: Throwable) { emit(ResultResponse(error = e.message))
        } catch (e: UnknownHostException) { emit(ResultResponse(error = e.message))
        } catch (e: SocketTimeoutException) { emit(ResultResponse(error = e.message))
        } catch (e: Exception) { emit(ResultResponse(error = e.message)) }
    }

    override fun getForecast(name: String, units: String) = flow {
        try {
            val response = dataSource.getForecast(name = name, units = units)
            if (response.isSuccessful) {
                val result = response.body()
                emit(ResultResponse(success = result))
            } else {
                emit(ResultResponse(errorInt = R.string.city_not_found))
            }
        } catch (e: IOException) { emit(ResultResponse(error = e.message))
        } catch (e: Throwable) { emit(ResultResponse(error = e.message))
        } catch (e: UnknownHostException) { emit(ResultResponse(error = e.message))
        } catch (e: SocketTimeoutException) { emit(ResultResponse(error = e.message))
        } catch (e: Exception) { emit(ResultResponse(error = e.message)) }
    }
}