package com.example.weather.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.data.UserData
import com.example.weather.extensions.toast
import com.example.weather.manager.DialogManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder

open class BaseActivity: AppCompatActivity() {

    private val dialogManager: DialogManager = DialogManager()
    lateinit var userData: UserData
    lateinit var mGson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userData = UserData(
            pref = getSharedPreferences(UserData.PREFERENCES_NAME, MODE_PRIVATE),
            gson = GsonBuilder().create()
        )
        mGson = GsonBuilder().create()
    }

    fun uiShowDialogWarning(title: String, description: String) {
        dialogManager.showDialogWarning(
            this@BaseActivity,
            title = title,
            description = description
        )
    }

    fun uiShowError(message: String?) {
        toast(message)
    }
}