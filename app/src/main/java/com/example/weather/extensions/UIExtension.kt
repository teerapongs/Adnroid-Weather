package com.example.weather.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target

inline fun <reified T: Activity> Activity.navigate(func: Intent.() -> Unit) {
    val intent = Intent(this, T::class.java)
    intent.func()
    startActivity(intent)
}

fun Activity.hideKeyboard(view: View) {
    view.apply {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}

fun AppCompatActivity.initToolbar(toolbar: Toolbar, title: String? = "", iconId: Int? = null) {
    toolbar.apply {
        this.title = title
        iconId?.run {
            this@apply.navigationIcon = ContextCompat.getDrawable(this@initToolbar, this)
        }
        setSupportActionBar(this)
    }
}

fun EditText.onActionListener(onActionListener: (String) -> Unit) {
    this.setOnEditorActionListener(object : OnEditorActionListener {
        override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
            if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                onActionListener.invoke(p0?.text.toString())
                return true
            }
            return false
        }

    })
}

fun Context.toast(resourceId: Int) = toast(getString(resourceId))

fun Context.toast(message: CharSequence?) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

infix fun ImageView.loadImageWhite(url: Any?): Target<Drawable> = let {
    val requestOptions = RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(android.R.color.white)
        .error(android.R.color.white)
    Glide.with(this.context).setDefaultRequestOptions(requestOptions).load(url).into(this)
}

