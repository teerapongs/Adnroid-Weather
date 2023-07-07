package com.example.weather.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.R
import com.example.weather.data.CLEAR
import com.example.weather.data.CLOUDS
import com.example.weather.data.DRIZZLE
import com.example.weather.data.RAIN
import com.example.weather.data.SNOW
import com.example.weather.data.THUNDERSTORM
import com.example.weather.data.TempState
import com.example.weather.model.ForecastDAO
import com.example.weather.model.MainFormStateDAO
import com.example.weather.model.RecommendationDAO
import com.example.weather.model.ResultResponse
import com.example.weather.model.WeatherDAO
import com.example.weather.ui.main.data.MainRepositoryInterface
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepositoryInterface): ViewModel() {

    private val _getWeatherNowSuccess = MutableLiveData<ResultResponse<WeatherDAO>>()
    val getWeatherNowSuccess: LiveData<ResultResponse<WeatherDAO>> = _getWeatherNowSuccess

    private val _getForecastSuccess = MutableLiveData<ResultResponse<ForecastDAO>>()
    val getForecastSuccess: LiveData<ResultResponse<ForecastDAO>> = _getForecastSuccess

    private val actionState = MutableLiveData<TempState>()
    val mainFormState = MutableLiveData<MainFormStateDAO>()
    val imageBackground = MutableLiveData<Int>()
    val recommendation = MutableLiveData<RecommendationDAO>()

    init {
        actionState.value = TempState.CELSIUS
        mainFormState.value = MainFormStateDAO(units = R.string.metric, tempType = R.string.celsius_type, switch = R.string.fahrenheit)
    }

    fun switchState() {
        actionState.value = if (actionState.value == TempState.CELSIUS) TempState.FAHRENHEIT else TempState.CELSIUS
        mainFormState.value = MainFormStateDAO(
            units = if (actionState.value == TempState.CELSIUS) R.string.metric else R.string.imperial,
            tempType = if (actionState.value == TempState.CELSIUS) R.string.celsius_type else R.string.fahrenheit_type,
            switch = if (actionState.value == TempState.CELSIUS) R.string.fahrenheit else R.string.celsius
        )
    }

    fun getWeatherNow(name: String, units: String) {
        viewModelScope.launch {
            repository.getWeatherNow(name = name, units = units).collect { response ->
                getWeatherResponse(response)
            }
        }
    }

    fun getWeatherNowWithLocation(lat: Double, long: Double, units: String) {
        viewModelScope.launch {
            repository.getWeatherNowWithLocation(lat = lat.toString(), long = long.toString(), units = units).collect { response ->
                getWeatherResponse(response)
            }
        }
    }

    private fun getWeatherResponse(response: ResultResponse<WeatherDAO>) {
        when(response.success != null) {
            true -> {
                if (response.success.cod == 200) {
                    val result = response.success
                    _getWeatherNowSuccess.value = ResultResponse(success = result)
                    checkWeatherMain(result.weather?.first()?.main, false)
                }
                else _getWeatherNowSuccess.value = ResultResponse(error = response.success.message)
            }
            else -> {
                _getWeatherNowSuccess.value = ResultResponse(error = response.error, errorInt = response.errorInt)
            }
        }
    }

    fun getForecast(name: String, units: String) {
        viewModelScope.launch {
            repository.getForecast(name = name, units = units).collect { response ->
                when(response.success != null) {
                    true -> {
                        if (response.success.cod == 200) {
                            val result = response.success
                            result.apply {
                                tempType = if (actionState.value == TempState.CELSIUS) R.string.celsius_type else R.string.fahrenheit_type
                            }.run {
                                _getForecastSuccess.value = ResultResponse(success = result)
                            }
                            result.list.last().run {
                                checkWeatherMain(weather?.first()?.main, true)
                            }
                        }
                        else _getForecastSuccess.value = ResultResponse(error = response.success.message)
                    }
                    else -> {
                        _getForecastSuccess.value = ResultResponse(error = response.error, errorInt = response.errorInt)
                    }
                }
            }
        }
    }

    private fun checkWeatherMain(main: String?, isForecast: Boolean) {
        when (main) {
            CLOUDS -> {
                if (isForecast) recommendation.value = RecommendationDAO(title = R.string.recommend_cloud, description = R.string.recommend_cloud_description)
                else imageBackground.value = R.mipmap.img_cloudy
            }
            THUNDERSTORM -> {
                if (isForecast) recommendation.value = RecommendationDAO(title = R.string.recommend_thunderstorm, description = R.string.recommend_thunderstorm_description)
                else imageBackground.value = R.mipmap.img_strom
            }
            DRIZZLE -> {
                imageBackground.value = R.mipmap.img_rain
                if (isForecast) recommendation.value = RecommendationDAO(title = R.string.recommend_drizzle, description = R.string.recommend_drizzle_description)
            }
            RAIN -> {
                if (isForecast) recommendation.value = RecommendationDAO(title = R.string.recommend_rain, description = R.string.recommend_rain_description)
                else imageBackground.value = R.mipmap.img_rain
            }
            SNOW -> {
                if (isForecast) recommendation.value = RecommendationDAO(title = R.string.recommend_snow, description = R.string.recommend_snow_description)
                else imageBackground.value = R.mipmap.img_snow
            }
            CLEAR -> {
                if (isForecast) recommendation.value = RecommendationDAO(title = R.string.recommend_clear, description = R.string.recommend_clear_description)
                else imageBackground.value = R.mipmap.img_sunny
            }
            else -> {
                if (isForecast) recommendation.value = RecommendationDAO(title = R.string.recommend_foggy, description = R.string.recommend_foggy_description)
                else imageBackground.value = R.mipmap.img_foggy
            }
        }
    }
}