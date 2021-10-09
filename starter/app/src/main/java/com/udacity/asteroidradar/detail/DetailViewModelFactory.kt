package com.udacity.asteroidradar.detail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.Asteroid
import java.lang.IllegalArgumentException

class DetailViewModelFactory(private val selectedAsteroid: Asteroid, private val app: Application)
    : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(selectedAsteroid, app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}