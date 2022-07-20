package com.prodapt.weatherforecast

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.junit.Assert.assertEquals

/**
 * Created by Anita Kiran on 7/20/2022.
 */
class WeatherRepositoryTest {

    lateinit var mainRepository: WeatherRepository
    val forecastModel = ForecastModel("Berlin","DE",
        data = ArrayList<ForecastData>(),lon="13.4105",lat="52.52437",timezone = "",state_code ="")

    @Mock
    lateinit var apiService: RetrofitService

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        mainRepository = WeatherRepository(apiService)
    }

    @Test
    fun `get forecast by city name`() {
        runBlocking {
            Mockito.`when`(apiService.getForecast("Berlin",null,null)).thenReturn(Response.success((forecastModel)))
            val response = mainRepository.getForecast("Berlin",null,null)
            assertEquals(forecastModel, response.body())
        }
    }

    @Test
    fun `get forecast by location coordinates`() {
        runBlocking {
            Mockito.`when`(apiService.getForecast(null, 52.52437, 13.41053))
                .thenReturn(Response.success((forecastModel)))
            val response = mainRepository.getForecast(null, 52.52437, 13.41053)
            assertEquals(forecastModel, response.body())
        }
    }
}