package com.example.weather.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.base.BaseViewHolder
import com.example.weather.databinding.ContentListPopularCitiesBinding

class PopularAdapter(
    private val onSelectedItem: (String?) -> Unit
): RecyclerView.Adapter<BaseViewHolder>() {

    private var items: MutableList<String>? = arrayListOf()

    fun setItems(data: MutableList<String>?) {
        this.items = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return PopularViewHolder(
            ContentListPopularCitiesBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        ) { name ->
            onSelectedItem.invoke(name)
        }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
       when(holder) {
           is PopularViewHolder -> {
               holder.onBindData(items?.get(position))
           }
       }
    }
}