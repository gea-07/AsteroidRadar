package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.*
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.launch
import java.lang.Exception


class MainViewModel(private val database: AsteroidDatabase, application:Application)
    : AndroidViewModel(application) {
    private val TAG= "MainViewModel"

    // Setup navigation to selected asteroid clicked by the user from the recyclerview
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid> = _navigateToSelectedAsteroid

    // Create the repository
    private val asteroidRepository = AsteroidsRepository(database)

    // Always get the asteroid list from the database first
    val asteroidList: LiveData<List<Asteroid>> = asteroidRepository.asteroids

    // Setup the status of fetching data from network with a progress bar to notify user of status
    val status: LiveData<AsteroidApiStatus> = asteroidRepository.status

    // Setup picture of day livedata
    private val _pictureOfTheDay = MutableLiveData<PictureOfDay>()
    val pictureOfTheDay: LiveData<PictureOfDay> = _pictureOfTheDay

    init {
        // get picture of the day
        getPictureOfTheDay()
        // fetch asteroid list from the network
        getAsteroidList()
    }

    private fun getPictureOfTheDay() {
        viewModelScope.launch {
            try {

                _pictureOfTheDay.value = AsteroidApi.retrofitService.getImageofTheDay(
                    BuildConfig.nasa_api_key)
                Log.i(TAG, "FOUND PICTURE OF DAY")
            } catch (e1: Exception) {
                Log.e(TAG, "Failure to fetch image of the day: " + e1.message)
            }
        }
    }

    private fun getAsteroidList() {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
        }
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }
}