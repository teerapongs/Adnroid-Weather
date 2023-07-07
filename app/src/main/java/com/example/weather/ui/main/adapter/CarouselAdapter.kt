package com.example.weather.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.base.BaseViewHolder
import com.example.weather.databinding.ContentCarouselBinding
import com.example.weather.model.RecommendationDAO

class CarouselAdapter : RecyclerView.Adapter<BaseViewHolder>(){

    private var items: MutableList<RecommendationDAO>? = null

    fun setItems(items: MutableList<RecommendationDAO>?) {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return CarouselViewHolder(
            ContentCarouselBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = this.items?.size ?: 0

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
       when(holder) {
           is CarouselViewHolder -> {
               holder.onBindData(this.items?.get(position))
           }
       }
    }
}