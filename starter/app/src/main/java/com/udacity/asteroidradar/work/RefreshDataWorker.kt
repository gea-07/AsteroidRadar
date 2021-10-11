package com.udacity.asteroidradar.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.AsteroidsRepository
import com.udacity.asteroidradar.database.AsteroidDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params:WorkerParameters):
CoroutineWorker(appContext, params){
    companion object {
        const val WORK_NAME ="RefreshDataWorker"
    }
    @InternalCoroutinesApi
    override suspend fun doWork(): Result {
        val database: AsteroidDatabase? = AsteroidDatabase.getInstance(applicationContext)
        val repository = AsteroidsRepository(database!!)

        return try{
            repository.refreshAsteroids()
            Result.success()
        } catch(exception: HttpException) {
            Result.retry()
        }
    }
}