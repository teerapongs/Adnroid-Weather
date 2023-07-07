package com.example.weather.ui.main.adapter

import com.example.weather.base.BaseViewHolder
import com.example.weather.databinding.ContentListPopularCitiesBinding

class PopularViewHolder(
    private val binding: ContentListPopularCitiesBinding,
    private val onSelectedItem: (String?) -> Unit
): BaseViewHolder(binding.root) {

    fun onBindData(name: String?) {
        binding.nameTextView.text = name
        itemView.setOnClickListener { onSelectedItem.invoke(name) }
    }
}