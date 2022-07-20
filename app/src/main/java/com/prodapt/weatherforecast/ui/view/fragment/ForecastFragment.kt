package com.prodapt.weatherforecast

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
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
    private var forecastListAdapter = WeatherForecastAdapter()
    private var itemsList: ArrayList<ForecastData> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ForecastFragmentLayoutBinding.inflate(inflater, container, false)

        setupUI()

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
                                // observe api response
                                observeForecast()
                            }
                        }
                    },
                    Looper.myLooper()
                )
            } else {
                Toast.makeText(requireContext(), AppConstants.TURN_ON_LOC, Toast.LENGTH_LONG).show()
            }
        } else {
            requestPermissions()
        }
    }

    // observer api response
    private fun observeForecast(){
        viewModel.forecast.observe(viewLifecycleOwner, Observer {
            when (it?.status) {
                Status.SUCCESS -> {
                    it.data?.let { forecastData ->
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

    // set weather data
    private fun setWeatherData(weather: ForecastModel){
        // set city name
        weather.city_name?.let{ city ->
            binding.tvCityName.text = city
        }
        // set temp of current date
        weather.data.get(0).temp?.let { temp->
            binding.tvTemp.text= Math.round(temp).toString().plus( "\u2103")
        }
        // set weather description
        weather.data.get(0).weather.description?.let { desc->
            binding.tvWeatherDesc.text = desc
        }
        // populate recyclerview with 5 days forecast
        itemsList.clear() // clear the list in case previous data is kept
        for(i in 1 until 6) {
            itemsList.add(weather.data[i])
        }
        forecastListAdapter.setForecastList(itemsList)

    }

    private fun setupUI(){
        // set recyclerview adapter
        binding.recyclerView.apply {
            setHasFixedSize(true)
            adapter = forecastListAdapter
        }

        // click event for search icon
        binding.imgSearch.setOnClickListener {
            searchWithCity()
        }

        binding.searchEdittext.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchWithCity()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun searchWithCity(){
        HideSoftKeyboard.hide(requireContext(),binding.imgSearch)
        if(binding.searchEdittext.text.toString().isNotEmpty())
            viewModel.getWeatherForecast(binding.searchEdittext.text.toString(), null,null)
    }

    override fun onResume() {
        super.onResume()
        getCurrentLoc()
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
            50
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

