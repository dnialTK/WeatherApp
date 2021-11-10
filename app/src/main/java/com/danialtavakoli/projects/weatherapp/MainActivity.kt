package com.danialtavakoli.projects.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.danialtavakoli.projects.weatherapp.databinding.ActivityMainBinding
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    //Default city is Tehran
    private var urlCity =
        "https://api.openweathermap.org/data/2.5/weather?q=tehran&appid=5afe1a52ea31bff546b9053c560c374b&lang=fa&units=metric"

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getData(urlCity)
    }

    @SuppressLint("SetTextI18n")
    private fun showContent(
        cityName: String,
        weatherDescription: String,
        imageURL: String,
        sunset: Int,
        sunrise: Int,
        temp: Double,
        feelsLike: Double,
        tempMin: Double,
        tempMax: Double,
        pressure: Int,
        humidity: Int,
        allClouds: Int,
        windSpeed: Double,
    ) {
        with(binding) {
            imageViewTower.visibility = View.VISIBLE
            progressBar.visibility = View.INVISIBLE
            textViewCityName.text = cityName
            textViewWeatherDescription.text = weatherDescription
            textViewSunset.text = getTimeFromUnixTime(sunset)
            textViewSunrise.text = getTimeFromUnixTime(sunrise)
            textViewTemp.text = "دما: $temp"
            textViewFeelsLike.text = "دمای احساس شده: $feelsLike"
            textViewTempMin.text = "حداقل دما: $tempMin"
            textViewTempMax.text = "حداکثر دما: $tempMax"
            textViewPressure.text = "فشار هوا: $pressure"
            textViewHumidity.text = "رطوبت هوا: $humidity"
            textViewWindSpeed.text = "سرعت باد: $windSpeed"
            textViewAllClouds.text = "تعداد ابر: $allClouds"
            buttonBerlin.text = "برلین"
            buttonAmsterdam.text = "آمستردام"
            buttonParis.text = "پاریس"
            buttonLondon.text = "لندن"
        }
        Glide.with(this).load(imageURL).into(binding.imageViewWeather)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTimeFromUnixTime(unixTime: Int): String {
        val time = unixTime * 1000.toLong()
        val date = Date(time)
        val formatter = SimpleDateFormat("HH:mm a")
        return formatter.format(date)
    }

    private fun getDataAndShowThem(rawContent: String) {
        val jsonObject = JSONObject(rawContent)
        val cityName = jsonObject.getString("name")
        val weatherArray = jsonObject.getJSONArray("weather")
        val weatherArrayObject = weatherArray.getJSONObject(0)
        val weatherDescription = weatherArrayObject.getString("description")
        val iconID = weatherArrayObject.getString("icon")
        val imageURL = "https://openweathermap.org/img/wn/$iconID@2x.png"
        val weatherSystem = jsonObject.getJSONObject("sys")
        val sunset = weatherSystem.getInt("sunset")
        val sunrise = weatherSystem.getInt("sunrise")
        val main = jsonObject.getJSONObject("main")
        val temp = main.getDouble("temp")
        val feelsLike = main.getDouble("feels_like")
        val tempMin = main.getDouble("temp_min")
        val tempMax = main.getDouble("temp_max")
        val pressure = main.getInt("pressure")
        val humidity = main.getInt("humidity")
        val clouds = jsonObject.getJSONObject("clouds")
        val allClouds = clouds.getInt("all")
        val wind = jsonObject.getJSONObject("wind")
        val windSpeed = wind.getDouble("speed")

        runOnUiThread {
            showContent(
                cityName,
                weatherDescription,
                imageURL,
                sunset,
                sunrise,
                temp,
                feelsLike,
                tempMin,
                tempMax,
                pressure,
                humidity,
                allClouds,
                windSpeed,
            )
        }
    }

    private fun getData(address: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(address)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.d("tagX", "onFailure: failed")
            }

            override fun onResponse(call: Call, response: Response) {
                val rawContent = response.body!!.string()
                getDataAndShowThem(rawContent)
            }
        })
    }

    fun reloadData(view: View) {
        with(binding) {
            imageViewTower.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            textViewCityName.text = "--"
            textViewWeatherDescription.text = "--"
            textViewSunset.text = "--"
            textViewSunrise.text = "--"
            textViewTemp.text = "--"
            textViewFeelsLike.text = "--"
            textViewTempMin.text = "--"
            textViewTempMax.text = "--"
            textViewPressure.text = "--"
            textViewHumidity.text = "--"
            textViewWindSpeed.text = "--"
            textViewAllClouds.text = "--"
            buttonLondon.text = "--"
            buttonBerlin.text = "--"
            buttonParis.text = "--"
            buttonAmsterdam.text = "--"
        }
        Glide.with(this).load(R.drawable.ic_refresh).into(binding.imageViewWeather)
        getData(urlCity)
    }

    fun setCityWeather(view: View) {
        val button = view as Button
        lateinit var cityName: String
        when (button) {
            binding.buttonLondon -> cityName = "london"
            binding.buttonParis -> cityName = "paris"
            binding.buttonAmsterdam -> cityName = "amsterdam"
            binding.buttonBerlin -> cityName = "berlin"
        }
        urlCity =
            "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=5afe1a52ea31bff546b9053c560c374b&lang=fa&units=metric"
        reloadData(view)
    }
}