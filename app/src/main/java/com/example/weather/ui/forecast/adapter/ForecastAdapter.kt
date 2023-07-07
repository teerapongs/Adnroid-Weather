package com.example.weather.ui.forecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.base.BaseViewHolder
import com.example.weather.databinding.ContentListWholeDayForecastBinding
import com.example.weather.model.ForecastDAO

class ForecastAdapter: RecyclerView.Adapter<BaseViewHolder>() {

    private var items: ForecastDAO? = null

    fun setItems(items: ForecastDAO?) {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
       return ForecastViewHolder(
           ContentListWholeDayForecastBinding
               .inflate(LayoutInflater.from(parent.context), parent, false)
       )
    }

    override fun getItemCount(): Int = this.items?.list?.size ?: 0

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
       when(holder) {
           is ForecastViewHolder -> {
               holder.onBindData(this.items?.list?.get(position), this.items?._tempType)
           }
       }
    }
}