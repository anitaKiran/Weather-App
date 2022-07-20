package com.prodapt.weatherforecast

data class ForecastModel(
    val city_name: String?,
    val country_code: String?,
    val `data`: ArrayList<ForecastData>,
    val lat: String,
    val lon: String,
    val state_code: String,
    val timezone: String
)