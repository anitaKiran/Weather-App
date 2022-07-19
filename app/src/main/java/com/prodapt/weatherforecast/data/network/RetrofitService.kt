package com.prodapt.weatherforecast

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Anita Kiran on 7/18/2022.
 */
interface RetrofitService {

    @GET("forecast/daily")
    suspend fun getForecast(
        @Query("city") city: String?,
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("key") apiKey: String = AppConstants.API_KEY
    ) : Response<ForecastModel>
}