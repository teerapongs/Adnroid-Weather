package com.example.weather

import android.app.Application
import com.example.weather.service.WeatherService
import com.example.weather.ui.main.MainViewModel
import com.example.weather.ui.main.data.MainDataSource
import com.example.weather.ui.main.data.MainRepository
import com.example.weather.ui.main.data.MainRepositoryInterface
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

val mainModule = module {
    single { WeatherService.create() }
    single { MainDataSource(get()) }
    single<MainRepositoryInterface> { MainRepository(get()) }
    viewModel { MainViewModel(get()) }
}

class WeatherApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(mainModule)
        }
    }
}