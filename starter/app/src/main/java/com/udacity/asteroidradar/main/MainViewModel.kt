package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.HiddenConstants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidProperty
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

enum class AsteroidApiStatus {LOADING, ERROR, DONE}

class MainViewModel(val database: AsteroidDatabaseDao, application:Application)
    : AndroidViewModel(application) {
    private val TAG = "MainViewModel"

    private val _status = MutableLiveData<AsteroidApiStatus>()

    val status: LiveData<AsteroidApiStatus> = _status

    private var _asteroidList = MutableLiveData<MutableList<Asteroid>>()

    val asteroidList: LiveData<MutableList<Asteroid>> = _asteroidList

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid> = _navigateToSelectedAsteroid

    init {
        getAsteroidList()
    }

    private fun getAsteroidList() {
        viewModelScope.launch() {
            val asteroids  = database.getAllAsteroids()
            if (_asteroidList.value.isNullOrEmpty()) {
                // get current time as the start date and then 7 days from now for the end date
                val calendar = Calendar.getInstance()
                val currentTime = calendar.time
                val dateFormat =
                    SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
                val startDate = dateFormat.format(currentTime)
                calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
                val endTime = calendar.time
                val endDate = dateFormat.format(endTime)

                // perform network call to get data from Nasa
                _status.value = AsteroidApiStatus.LOADING
                try {
                    var listResult = AsteroidApi.retrofitService
                        .getAsteroids(startDate, endDate, HiddenConstants.APIKEY)
                    _status.value = AsteroidApiStatus.DONE
                    _asteroidList.value = parseAsteroidsJsonResult(JSONObject(listResult))
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            for (asteroid in (_asteroidList.value as ArrayList<Asteroid>)) {
                                database.insert(asteroid)
                            }
                        } catch (e1: Exception) {
                            Log.e(TAG, "Failure: " + e1.message)
                        }
                    }
                } catch (e2: Exception) {
                    _status.value = AsteroidApiStatus.ERROR
                    _asteroidList.value = ArrayList()
                    Log.e(TAG, "Failure: " + e2.message)
                }
            } else {
                _asteroidList.value = asteroids.value as MutableList<Asteroid>?
            }
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }
}