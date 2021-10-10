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

    // Setup the status of fetching data from network with a progress bar to notify user of status
    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus> = _status

    //private var _asteroidList = MutableLiveData<MutableList<Asteroid>>()
    // Always get the asteroid list from the database first
    val asteroidList: LiveData<List<Asteroid>> = database.getAllAsteroids()

    // Setup navigation to selected asteroid clicked by the user from the recyclerview
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid> = _navigateToSelectedAsteroid

    init {
        // fetch asteroid list from the network
        getAsteroidList()
    }

    private fun getAsteroidList() {
        viewModelScope.launch() {
            // get current time as the start date and then 7 days from now for the end date
            val calendar = Calendar.getInstance()
            val currentTime = calendar.time
            val dateFormat =
                SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
            val startDate = dateFormat.format(currentTime)
            calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
            val endTime = calendar.time
            val endDate = dateFormat.format(endTime)

            _status.value = AsteroidApiStatus.LOADING
            try {
                // Perform network call to get data from Nasa
                var listResult = AsteroidApi.retrofitService
                    .getAsteroids(startDate, endDate, HiddenConstants.APIKEY)
                _status.value = AsteroidApiStatus.DONE

                // Parsed JSON result into a list of asteroids
                val parsedAsteroidList = parseAsteroidsJsonResult(JSONObject(listResult))

                // Insert any new list fetched from network into the database
                try {
                    for (asteroid in parsedAsteroidList) {
                        database.insert(asteroid)
                    }
                } catch (e1: Exception) {
                    Log.e(TAG, "Failure to insert to the database: " + e1.message)
                }

            } catch (e2: Exception) {
                _status.value = AsteroidApiStatus.ERROR
                Log.e(TAG, "Failure to fetch data from NASA: " + e2.message)
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