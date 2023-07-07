package com.example.weather.manager

import android.app.Activity
import android.app.AlertDialog
import com.example.weather.R

class DialogManager {
    private var alertDialog: AlertDialog? = null

    fun showDialogWarning(
        activity: Activity,
        title: String,
        description: String
    ) {

        clearAlertDialog()
        alertDialog ?: run {
            alertDialog = AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(description)
                .setPositiveButton(activity.getString(R.string.ok), null)
                .create()
        }

        if (!(activity).isFinishing) {
            alertDialog?.apply { show() }
        }
    }

    private fun clearAlertDialog() {
        alertDialog?.dismiss()
        alertDialog = null
    }
}