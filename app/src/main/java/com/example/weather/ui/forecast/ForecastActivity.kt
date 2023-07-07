package com.example.weather.ui.forecast

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.base.BaseActivity
import com.example.weather.data.KEY_DATA
import com.example.weather.databinding.ActivityForecastBinding
import com.example.weather.extensions.initToolbar
import com.example.weather.model.ForecastDAO
import com.example.weather.ui.forecast.adapter.ForecastAdapter

class ForecastActivity : BaseActivity() {

    private lateinit var binding: ActivityForecastBinding
    private val forecastAdapter: ForecastAdapter by lazy { ForecastAdapter() }
    private var forecastDAO: ForecastDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForecastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            finish()
        }
        intent.getStringExtra(KEY_DATA).apply {
            forecastDAO = mGson.fromJson(this, ForecastDAO::class.java)
            forecastDAO?.apply {
                _tempType = getString(tempType ?: 0)
            }
        }
        setupRecycleView()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupRecycleView() {
        binding.forecastRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = forecastAdapter
        }
        if (forecastDAO != null) {
            forecastAdapter.setItems(this.forecastDAO)
            forecastAdapter.notifyDataSetChanged()
        }
    }
}