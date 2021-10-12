package com.udacity.asteroidradar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.getCurrentDate
import com.udacity.asteroidradar.api.getCurrentDatePlusSevenDays
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.Exception

enum class AsteroidApiStatus {LOADING, ERROR, DONE}

/**
* Repository for fetching asteroids from the network and storing them on disk
 */

class AsteroidsRepository(private val database: AsteroidDatabase) {
    private val TAG = "AsteroidsRepository"

    // Fetch and display the asteroids from the database and only fetch the asteroids from today
    // onwards, ignoring asteroids before today. Also, display the asteroids sorted by date
    val asteroids: LiveData<List<Asteroid>> = database.asteroidDatabaseDao.getAsteroidsByDateRange(
        getCurrentDate(), getCurrentDatePlusSevenDays())

    val status = MutableLiveData<AsteroidApiStatus>()


    suspend fun refreshAsteroids() {
        status.postValue(AsteroidApiStatus.LOADING)

        withContext(Dispatchers.IO) {
            try {
                val asteroidList: String = AsteroidApi.retrofitService
                    .getAsteroids(getCurrentDate(),
                        getCurrentDatePlusSevenDays(),
                        BuildConfig.nasa_api_key)
                status.postValue(AsteroidApiStatus.DONE)

                // Parsed JSON result into a list of asteroids
                val parsedAsteroidList = parseAsteroidsJsonResult(JSONObject(asteroidList))
                try {
                    // Delete previous days asteroid data to avoid adding to much in the database
                    database.asteroidDatabaseDao.deletePreviousDays(getCurrentDate())
                    // Now insert new data to the database
                    database.asteroidDatabaseDao.insertAll(*parsedAsteroidList.asDatabaseModel())
                }catch (e1: Exception) {
                    Log.e(TAG, "Failure to insert to the database: " + e1.message)
                }
            }catch (e2: Exception) {
                status.postValue(AsteroidApiStatus.ERROR)
                Log.e(TAG, "Failure to fetch data from NASA: " + e2.message)
            }
        }
    }
}