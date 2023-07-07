package com.example.weather.model

data class ResultResponse<T: Any>(val success: T? = null, val error: String? = null, val errorInt: Int? = null)