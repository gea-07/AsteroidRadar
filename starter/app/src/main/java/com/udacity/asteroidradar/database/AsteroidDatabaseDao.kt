package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDatabaseDao {

@Insert(onConflict = OnConflictStrategy.REPLACE)
abstract suspend fun insert(asteroid: Asteroid)

@Update
suspend fun update(asteroid: Asteroid)

@Query("SELECT * from asteroid_table WHERE id = :key")
suspend fun get(key: Long):Asteroid?

@Query("DELETE FROM asteroid_table")
suspend fun clear()

@Query("SELECT * FROM asteroid_table ORDER by close_approach_date DESC")
fun getAllAsteroids() : LiveData<List<Asteroid>>

@Query("SELECT * FROM asteroid_table WHERE close_approach_date BETWEEN date(:startDate) AND date(:endDate) ORDER BY date(close_approach_date) ASC")
fun getAsteroidsByDateRange(startDate: String, endDate:String) : LiveData<List<Asteroid>>

@Query("SELECT * FROM asteroid_table ORDER BY close_approach_date DESC LIMIT 1")
suspend fun getAsteroid(): Asteroid?
}