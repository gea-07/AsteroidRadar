package com.udacity.asteroidradar.main

import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidProperty
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val APIKEY = "8z7wdyAJ06CuPQCDcgqkco5zX9SNJqQEbLetKsgj"
class MainViewModel : ViewModel() {

    private var _asteroidList = MutableLiveData<MutableList<Asteroid>>()

    val asteroidList: LiveData<MutableList<Asteroid>> = _asteroidList

    init {
        getAsteroidList()
    }

    private fun getAsteroidList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val calendar = Calendar.getInstance()
                val currentTime = calendar.time
                val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
                val startDate = dateFormat.format(currentTime)
                calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
                val endTime = calendar.time
                val endDate = dateFormat.format(endTime)

                var result = AsteroidApi.retrofitService.getAsteroids(
                    startDate,
                    endDate,
                    APIKEY)
                if (result !== null) {
                    _asteroidList.value = parseAsteroidsJsonResult(JSONObject(result))
                }
            } catch (e: Exception) {
                val response = "Failure: " + e.message
            }
        }

//        _asteroidList.value = mutableListOf(
//            Asteroid(
//                1,
//                "Aster 1",
//                "10-09-21",
//                3.5,
//                5.2,
//                500.0,
//                350.0,
//                true
//            ),
//            Asteroid(
//                2,
//                "Aster 2",
//                "10-06-21",
//                3.5,
//                5.2,
//                500.0,
//                350.0,
//                false
//            )
//        )
    }
}