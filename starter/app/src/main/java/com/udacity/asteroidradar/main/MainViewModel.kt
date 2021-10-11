package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.AsteroidApiStatus
import com.udacity.asteroidradar.AsteroidsRepository
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.launch


class MainViewModel(private val database: AsteroidDatabase, application:Application)
    : AndroidViewModel(application) {

    // Setup navigation to selected asteroid clicked by the user from the recyclerview
    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid> = _navigateToSelectedAsteroid

    // Create the repository
    private val asteroidRepository = AsteroidsRepository(database)

    // Always get the asteroid list from the database first
    val asteroidList: LiveData<List<Asteroid>> = asteroidRepository.asteroids

    // Setup the status of fetching data from network with a progress bar to notify user of status
    val status: LiveData<AsteroidApiStatus> = asteroidRepository.status

    init {
        // fetch asteroid list from the network
        getAsteroidList()
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