package com.prodapt.weatherforecast

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.prodapt.weatherforecast.databinding.ForecastFragmentLayoutBinding
import dagger.hilt.android.AndroidEntryPoint


/**
 * Created by Anita Kiran on 7/18/2022.
 */
@AndroidEntryPoint
class ForecastFragment : Fragment() {

    private var _binding: ForecastFragmentLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel : ForecastViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ForecastFragmentLayoutBinding.inflate(inflater, container, false)

        getCurrentLoc()

        return binding.root
    }


    // get weather forecast of user's current location
    @SuppressLint("MissingPermission")
    private fun getCurrentLoc(){
        // Location Provider Client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                val locationRequest = LocationRequest.create().setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)
                            var lat : Double? = null
                            var lon : Double? = null
                            for (location in locationResult.locations) {
                                // get device current location coordinates
                                lat = location.latitude
                                lon = location.longitude
                            }
                            if(lat != null && lon != null) {
                                //call api for fetching the forecast of current location
                                viewModel.getWeatherForecast(city= null, lat = lat,lon = lon)
                                // observe forecast response
                                observeForecast()
                            }
                        }
                    },
                    Looper.myLooper()
                )
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
            }
        } else {
            requestPermissions()
        }
    }

    private fun observeForecast(){
        viewModel.forecast.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    it.data?.let { forecastData ->
                        //Log.e("current forcast", ""+forecastData.data.get(0).temp)
                        setWeatherData(forecastData)
                    }
                }
                Status.LOADING -> {}
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // set ui
    private fun setWeatherData(weather: ForecastModel){
        // set city name
        weather.city_name?.let{ city ->
            binding.tvCityName.text = city
        }
        // set temp of current date
        weather.data.get(0).temp?.let { temp->
            binding.tvTemp.text= temp.toString()
        }
        // set weather description
        weather.data.get(0).weather.description?.let { desc->
            binding.tvWeatherDesc.text = desc
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            42
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
}