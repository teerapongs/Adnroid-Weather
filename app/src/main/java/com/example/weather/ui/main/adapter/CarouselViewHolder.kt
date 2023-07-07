package com.example.weather.ui.main.adapter

import com.example.weather.base.BaseViewHolder
import com.example.weather.databinding.ContentCarouselBinding
import com.example.weather.model.RecommendationDAO

class CarouselViewHolder(
    private val binding: ContentCarouselBinding
): BaseViewHolder(binding.root) {

    fun onBindData(recommend: RecommendationDAO?) {
        binding.titleTextView.text = recommend?._title
        binding.descriptionTextView.text = recommend?._description
    }
}