package com.prodapt.weatherforecast

import android.text.format.DateFormat
import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Anita Kiran on 7/20/2022.
 */

@BindingAdapter("datetime")
fun TextView.setWeekday(input: String) {
    if(input != null) {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date: Date? = formatter.parse(input)
        val dayToday = DateFormat.format("EEEE", date).toString()
        this.text = dayToday
    }
}

@BindingAdapter("temp")
fun TextView.setTemperature(temp: String) {
    if (temp != null) {
        this.setText(Math.round(temp.toDouble()).toString().plus(" \u2103"))
    }
}