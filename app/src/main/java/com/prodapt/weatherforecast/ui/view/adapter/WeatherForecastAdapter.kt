package com.prodapt.weatherforecast

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.prodapt.weatherforecast.databinding.ListRowItemBinding

/**
 * Created by Anita Kiran on 7/19/2022.
 */
class WeatherForecastAdapter() : RecyclerView.Adapter<WeatherForecastAdapter.DataViewHolder>() {

    var list = arrayListOf<ForecastData>()

    fun setForecastList(list: ArrayList<ForecastData>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): DataViewHolder {
        val binding =
            ListRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun getItemCount() = list.size

    class DataViewHolder(val viewHolder: ListRowItemBinding): RecyclerView.ViewHolder(viewHolder.root)

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.viewHolder.listItems = this.list[position]
    }
}