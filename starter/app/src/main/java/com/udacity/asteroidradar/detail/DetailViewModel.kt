package com.udacity.asteroidradar.detail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.Asteroid

class DetailViewModel(selectedAsteroid: Asteroid, app:Application) : ViewModel() {
    private val _asteroid = MutableLiveData<Asteroid>()
    val asteroid: LiveData<Asteroid> = _asteroid
    init {
        _asteroid.value = selectedAsteroid
    }
}