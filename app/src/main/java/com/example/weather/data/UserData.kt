package com.example.weather.data

import android.content.SharedPreferences
import com.example.weather.model.LocationDAO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

class UserData(
    private val pref: SharedPreferences,
    private val gson: Gson
) {

    fun setLastLocation(location: LocationDAO) = pref.edit().putString(KEY_LAST_LOCATION, gson.toJson(location)).apply()

    fun getLastLocation(): LocationDAO? {
        val jsonString: String? = pref.getString(KEY_LAST_LOCATION, JSONObject().toString())
        jsonString?.let {
            return when(it.isEmpty()) {
                true -> return null
                else -> {
                    val typeList = object : TypeToken<LocationDAO>() {}.type
                    gson.fromJson(it, typeList)
                }
            }
        }
        return null
    }

    companion object {
        const val PREFERENCES_NAME = "user_data"

        private const val KEY_LAST_LOCATION = "key_last_location"
    }
}