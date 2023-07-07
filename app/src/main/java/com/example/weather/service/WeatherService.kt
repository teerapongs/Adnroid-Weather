package com.example.weather.service

import com.example.weather.model.ForecastDAO
import com.example.weather.model.WeatherDAO
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface WeatherService {
    @GET(WEATHER)
    suspend fun getWeatherNow(
        @Query("units") units: String,
        @Query("q") q: String,
        @Query("appid") appid: String = "cc25df5205e914c8bce2c166d21e7454"
    ): Response<WeatherDAO>

    @GET(WEATHER)
    suspend fun getWeatherNowWithLocation(
        @Query("units") units: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appid: String = "cc25df5205e914c8bce2c166d21e7454"
    ): Response<WeatherDAO>

    @GET(FORECAST)
    suspend fun getForecast(
        @Query("units") units: String,
        @Query("q") q: String,
        @Query("appid") appid: String = "cc25df5205e914c8bce2c166d21e7454"
    ): Response<ForecastDAO>

    companion object {
        private const val TIME_OUT = 90L

        fun create(): WeatherService {
            val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
            okHttpClientBuilder
                .certificatePinner(CertificatePinner.DEFAULT)
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                    val newRequest: Request = requestBuilder.apply {
                        header("Content-Type", "application/json")
                    }.build()
                    chain.proceed(newRequest)
                }
            okHttpClientBuilder.build()
            val url = BASE_URL
            val retrofit = Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
            return retrofit.create(WeatherService::class.java)
        }
    }
}