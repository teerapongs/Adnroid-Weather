package com.example.weather.ui.forecast.adapter

import android.annotation.SuppressLint
import com.example.weather.base.BaseViewHolder
import com.example.weather.databinding.ContentListWholeDayForecastBinding
import com.example.weather.extensions.loadImageWhite
import com.example.weather.model.Forecast

class ForecastViewHolder(
    private val binding: ContentListWholeDayForecastBinding
): BaseViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun onBindData(data: Forecast?, tempType: String?) {
        binding.dateTextView.text = data?.dt_txt
        binding.temperatureTextView.text = "${data?.main?.temp} $tempType"
        binding.humidityTextView.text = "Humidity ${data?.main?.humidity} %"
        binding.descriptionTextView.text = "${data?.weather?.first()?.main}: ${data?.weather?.first()?.description}"
        binding.tempImageView loadImageWhite "https://openweathermap.org/img/wn/${data?.weather?.first()?.icon}@2x.png"
    }
}