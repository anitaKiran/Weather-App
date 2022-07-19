package com.prodapt.weatherforecast

import retrofit2.Response
import javax.inject.Inject

/**
 * Created by Anita Kiran on 7/18/2022.
 */

class WeatherRepository @Inject constructor(private val apiService: RetrofitService) {

    suspend fun getForecast(city:String?, lat:Double?, lon:Double?): Response<ForecastModel> =
        apiService.getForecast(city,lat,lon)
}