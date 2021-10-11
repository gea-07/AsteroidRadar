package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDatabaseDao {
    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate BETWEEN date(:startDate) AND date(:endDate) ORDER BY date(closeApproachDate) ASC")
    fun getAsteroidsByDateRange(startDate: String, endDate:String) : LiveData<List<Asteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroids: DatabaseAsteroid)
}