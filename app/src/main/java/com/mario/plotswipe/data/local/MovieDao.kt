package com.mario.plotswipe.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<MovieEntity>
    @Query("SELECT * FROM movies")
    fun getAllSavedMovies(): kotlinx.coroutines.flow.Flow<List<MovieEntity>>
}