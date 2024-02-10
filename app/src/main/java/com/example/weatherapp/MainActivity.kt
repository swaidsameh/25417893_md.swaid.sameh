package com.example.weatherapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Dhaka")
        searchCity()
    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object:androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun fetchWeatherData(cityName : String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName,"22de1458ca98a63cd050b5f5d1502727","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
               if (response.isSuccessful && response.body()!=null){
                   val temperature = response.body()!!.main.temp
                   val humidity = response.body()!!.main.humidity
                   val windSpeed = response.body()!!.wind.speed
                   val sunRise = response.body()!!.sys.sunrise.toLong()
                   val sunSet = response.body()!!.sys.sunset.toLong()
                   val seaLevel = response.body()!!.main.pressure
                   val condition = response.body()!!.weather.firstOrNull()?.main?:"unknown"
                   val maxTemp = response.body()!!.main.temp_max
                   val minTemp = response.body()!!.main.temp_min
                   //Log.d("TAG", "onResponse: $temperature")
                   binding.temp.text = "$temperature °C"
                   binding.weather.text = condition
                   binding.condition.text = condition
                   binding.maxTemp.text = "Max Temp: $maxTemp °C"
                   binding.minTemp.text = "Min Temp: $minTemp °C"
                   binding.humidity.text = "$humidity %"
                   binding.windSpeed.text = "$windSpeed m/s"
                   binding.sunRise.text = "${time(sunRise)}"
                   binding.sunSet.text = "${time(sunSet)}"
                   binding.seaLevel.text = "$seaLevel hPa"
                   binding.cityName.text = "$cityName"
                   binding.day.text = dayName(System.currentTimeMillis())
                   binding.date.text = date()
                   changeImageAccordingToWeatherCondition(condition)
               }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeImageAccordingToWeatherCondition(conditions: String) {
        when(conditions){
            "Haze"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnamationView.setAnimation(R.raw.cloud)
            }
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.background_shape_2)
                binding.lottieAnamationView.setAnimation(R.raw.sun)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnamationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnamationView.setAnimation(R.raw.sun)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnamationView.setAnimation(R.raw.sun)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnamationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnamationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy",Locale.getDefault())
        return sdf.format(Date())
    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    fun dayName(timestamp: Long) : String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

}

