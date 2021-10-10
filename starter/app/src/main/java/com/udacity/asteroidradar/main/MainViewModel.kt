package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.HiddenConstants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

enum class AsteroidApiStatus {LOADING, ERROR, DONE}

class MainViewModel(val database: AsteroidDatabaseDao, application:Application)
    : AndroidViewModel(application) {
    private val TAG = "MainViewModel"

    // Setup navigation to selected asteroid clicked by the user from the recyclerview
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid> = _navigateToSelectedAsteroid

    // Setup the status of fetching data from network with a progress bar to notify user of status
    private val _status = MutableLiveData<AsteroidApiStatus>()
    val status: LiveData<AsteroidApiStatus> = _status

    // Always get the asteroid list from the database first
    // Fetch and display the asteroids from the database and only fetch the asteroids from today
    // onwards, ignoring asteroids before today. Also, display the asteroids sorted by date
    val asteroidList: LiveData<List<Asteroid>> = database.getAsteroidsByDateRange(
        getCurrentDate(),
        getCurrentDatePlusSevenDays()
    )

    init {
        // fetch asteroid list from the network
        getAsteroidList()
    }

    private fun getAsteroidList() {
        viewModelScope.launch() {
            _status.value = AsteroidApiStatus.LOADING
            try {
                // Perform network call to get data from Nasa
                var listResult = AsteroidApi.retrofitService
                    .getAsteroids(getCurrentDate(), getCurrentDatePlusSevenDays(), HiddenConstants.APIKEY)
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

    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val currentDate = dateFormat.format(currentTime)
        return currentDate
    }

    private fun getCurrentDatePlusSevenDays(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
        val endTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        val endDate = dateFormat.format(endTime)
        return endDate
    }
}