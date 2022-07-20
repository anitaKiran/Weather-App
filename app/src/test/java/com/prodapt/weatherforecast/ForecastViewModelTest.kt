package com.prodapt.weatherforecast

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import retrofit2.Response

/**
 * Created by Anita Kiran on 7/20/2022.
 */
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ForecastViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    lateinit var forecastViewModel: ForecastViewModel
    lateinit var repository: WeatherRepository

    val forecastModel = ForecastModel("Berlin","DE",
        data = ArrayList<ForecastData>(),lon="13.4105",lat="52.52437",timezone = "",state_code ="")


    @Mock
    lateinit var apiService: RetrofitService

    @get:Rule
    val instantTaskExecutionRule: InstantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        repository = WeatherRepository(apiService)
        forecastViewModel = ForecastViewModel(repository)
    }

    @Test
    fun getWeatherForecastSuccess() {
        runBlocking {
            Mockito.`when`(repository.getForecast("Berlin", null, null))
                .thenReturn(Response.success(forecastModel))
            forecastViewModel.getWeatherForecast("Berlin", null, null)
            val result = forecastViewModel.forecast.getOrAwaitValue()
            assert(result.data != null)
        }
    }
}