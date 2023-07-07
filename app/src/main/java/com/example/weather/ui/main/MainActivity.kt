package com.example.weather.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.PorterDuff
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.weather.R
import com.example.weather.base.BaseActivity
import com.example.weather.data.KEY_DATA
import com.example.weather.data.PERMISSION_REQUEST_ACCESS_FINE_LOCATION
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.databinding.ContentAddNewCityBinding
import com.example.weather.extensions.hideKeyboard
import com.example.weather.extensions.initToolbar
import com.example.weather.extensions.navigate
import com.example.weather.extensions.onActionListener
import com.example.weather.model.ForecastDAO
import com.example.weather.model.LocationDAO
import com.example.weather.model.MainFormStateDAO
import com.example.weather.model.RecommendationDAO
import com.example.weather.model.WeatherDAO
import com.example.weather.ui.forecast.ForecastActivity
import com.example.weather.ui.main.adapter.CarouselAdapter
import com.example.weather.ui.main.adapter.PopularAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import kotlin.math.abs

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var contentAddNewCityBinding: ContentAddNewCityBinding
    private val mainViewModel: MainViewModel by viewModel()
    private var locationManager: LocationManager? = null

    private var cityName: String = ""
    private var weatherDAO: WeatherDAO? = null
    private var mainFormState: MainFormStateDAO? = null
    private var forecastDAO: ForecastDAO? = null

    private val popularAdapter: PopularAdapter by lazy {
        PopularAdapter {
            toggleSlideView(false)
            cityName = if (it.toString() == getString(R.string.my_location)) "" else it.toString()
            getWeather()
        }
    }
    private val carouselAdapter: CarouselAdapter by lazy { CarouselAdapter() }
    private val flexboxLayoutManager: FlexboxLayoutManager by lazy { FlexboxLayoutManager(applicationContext) }

    override fun onResume() {
        super.onResume()
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        intent = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        contentAddNewCityBinding = binding.contentAddNewCity
        setContentView(binding.root)
        val toolbar = contentAddNewCityBinding.toolbar
        initToolbar(toolbar)
        toolbar.setNavigationOnClickListener {
            toggleSlideView(false)
        }
        setupAll()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_FINE_LOCATION) {
            when(grantResults[0]) {
                PackageManager.PERMISSION_GRANTED -> getLocation()
                PackageManager.PERMISSION_DENIED -> uiShowDialogWarning(getString(R.string.permission_denied), getString(R.string.description_permission_denied))
            }
        }
    }

    private fun setupAll() {
        setupMainView()
        setupViewPager()
        setupPopularCitiesListView()
        setupObservation()
    }

    private fun getLocation() {
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_ACCESS_FINE_LOCATION)
            return
        }
        try {
            val location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                LocationDAO(lat = location.latitude, long = location.longitude).also {
                    userData.setLastLocation(it)
                }
            }
            getWeatherNowWithLocation(
                lat = location?.latitude ?: userData.getLastLocation()?.lat ?: 0.0,
                long = location?.longitude ?: userData.getLastLocation()?.long ?: 0.0
            )
        } catch (ex: SecurityException) {
            uiShowError(ex.message)
        }
    }

    private fun setupMainView() {
        binding.buttonSearch.setOnClickListener {
            toggleSlideView(true)
        }
        ContextCompat.getColor(this, R.color.white).also {
            binding.switchImageView.setColorFilter(it, PorterDuff.Mode.SRC_ATOP)
            binding.imageForecast.setColorFilter(it, PorterDuff.Mode.SRC_ATOP)
        }
        binding.switchTempButton.setOnClickListener {
            mainViewModel.switchState()
        }
        binding.refreshButton.setOnClickListener { getWeather() }
        binding.forecastButton.setOnClickListener { navigate<ForecastActivity> { putExtra(KEY_DATA, mGson.toJson(forecastDAO)) }}
        contentAddNewCityBinding.searchEditText.onActionListener {
            this.cityName = it
            toggleSlideView(false)
            getWeatherNow()
        }
        contentAddNewCityBinding.cancelButton.setOnClickListener {
            contentAddNewCityBinding.searchEditText.text.clear()
        }
    }

    private fun setupViewPager() {
        binding.viewPager.apply {
            clipChildren = false // No clipping the left and right items
            clipToPadding = false // Show the viewpager in full width without clipping the padding
            offscreenPageLimit = 3 // Render the left and right items
            (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect
        }
        binding.viewPager.adapter = carouselAdapter
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer((40 * Resources.getSystem().displayMetrics.density).toInt()))
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = (0.80f + r * 0.20f)
        }
        binding.viewPager.setPageTransformer(compositePageTransformer)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupObservation() {
        mainViewModel.mainFormState.observe(this, Observer { response ->
            val result = response ?: return@Observer
            this.mainFormState = result
            getWeather()
        })
        mainViewModel.getWeatherNowSuccess.observe(this, Observer { response ->
            val result = response ?: return@Observer
            println("####result: $result")
            if (result.error != null || result.errorInt != null) {
                val error = result.error ?: result.errorInt?.let { getString(it) }
                uiShowDialogWarning(getString(R.string.warning), error.toString())
            }
            if (result.success != null) {
                this.weatherDAO = result.success
                this.cityName = this.weatherDAO?.name ?: ""
                getForecast()
            }
            if (this.weatherDAO != null) updateUI(this.weatherDAO)
        })
        mainViewModel.imageBackground.observe(this, Observer { response ->
            val result = response ?: return@Observer
            binding.weatherImageView.setImageResource(result)
        })
        mainViewModel.getForecastSuccess.observe(this, Observer { response ->
            val result = response ?: return@Observer
            if (result.error != null || result.errorInt != null) {
                val error = result.error ?: result.errorInt?.let { getString(it) }
                uiShowDialogWarning(getString(R.string.warning), error.toString())
            }
            if (result.success != null) this.forecastDAO = result.success
        })

        mainViewModel.recommendation.observe(this, Observer { response ->
            val result = response ?: return@Observer
            result.apply {
                _title = getString(title ?: 0)
                _description = getString(description ?: 0)
            }.also {
                mutableListOf<RecommendationDAO>().apply {
                    add(result)
                }.run {
                    carouselAdapter.setItems(this)
                    carouselAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    private fun getWeather() {
        if (this.cityName.isBlank()) getLocation()
        else getWeatherNow()
    }

    private fun getWeatherNow() {
        mainViewModel.getWeatherNow(name = this.cityName, units = getString(this.mainFormState?.units ?: 0))
    }

    private fun getWeatherNowWithLocation(lat: Double, long: Double) {
        mainViewModel.getWeatherNowWithLocation(lat = lat, long = long, units = getString(this.mainFormState?.units ?: 0))
    }

    private fun getForecast() {
        mainViewModel.getForecast(this.cityName, units = getString(this.mainFormState?.units ?: 0))
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(data: WeatherDAO?) {
        binding.locationTextView.text = data?.name
        binding.switchTextView.text = getString(this.mainFormState?.switch ?: 0)
        binding.tempTextView.text = "${data?.main?.temp?.toInt()} ${getString(this.mainFormState?.tempType ?: 0)}"
        binding.descriptionTextView.text = "${data?.weather?.first()?.main}: ${data?.weather?.first()?.description}"
        binding.humidityTextView.text = getString(R.string.humidity) + " ${data?.main?.humidity} %"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            binding.dayTextView.text = LocalDate.now()?.dayOfWeek?.name
        }
    }

    private fun toggleSlideView(show: Boolean) {
        val view = contentAddNewCityBinding.contentViewAddCity
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 300
        transition.addTarget(view)

        TransitionManager.beginDelayedTransition(binding.parentView, transition)
        view.visibility = if (show) View.VISIBLE else View.GONE
        hideKeyboard(contentAddNewCityBinding.contentViewAddCity)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupPopularCitiesListView() {
        val popularList = resources.getStringArray(R.array.popular_cities)
        flexboxLayoutManager.apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
        }

        contentAddNewCityBinding.popularRecyclerView.apply {
            layoutManager = flexboxLayoutManager
            adapter ?: run {
                adapter = popularAdapter
            }
        }
        popularAdapter.setItems(popularList.toMutableList())
        popularAdapter.notifyDataSetChanged()
    }

}