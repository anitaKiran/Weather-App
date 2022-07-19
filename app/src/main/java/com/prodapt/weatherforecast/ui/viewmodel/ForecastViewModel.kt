package com.prodapt.weatherforecast

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Anita Kiran on 7/18/2022.
 */
@HiltViewModel
class ForecastViewModel @Inject constructor(private val repo: WeatherRepository) : ViewModel()  {

    private var _forecast = MutableLiveData<Resource<ForecastModel>>()
    val forecast: LiveData<Resource<ForecastModel>> get() = _forecast


     fun getWeatherForecast(city:String?,lat:Double?,lon:Double?) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                repo.getForecast(city, lat, lon).let { response ->
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            _forecast.postValue(Resource.success(response.body()))
                        } else
                            _forecast.postValue(Resource.error(response.errorBody().toString(), null))
                    }
                }
            }
        }
        catch(e:Exception){
            _forecast.postValue(Resource.error("No Data found", null))
        }
    }
}