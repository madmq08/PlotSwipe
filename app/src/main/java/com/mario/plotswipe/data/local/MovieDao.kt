package com.mario.plotswipe.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    // 1. Insertar información técnica (Si ya existe, se ignora)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovieData(movie: MovieEntity)

    // 2. Insertar relación del usuario (Si cambia, se actualiza)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserRelation(relation: UserMovieCrossRef)
    @Transaction
    suspend fun saveMovieForUser(movie: MovieEntity, userId: String) {
        insertMovieData(movie)
        insertUserRelation(UserMovieCrossRef(userId = userId, movieId = movie.id))
    }


    @Query("""
        SELECT * FROM movies 
        INNER JOIN user_movie_cross_ref ON movies.id = user_movie_cross_ref.movieId 
        WHERE user_movie_cross_ref.userId = :userId
    """)
    suspend fun getAllMovies(userId: String): List<MovieEntity>

    @Query("""
        SELECT * FROM movies 
        INNER JOIN user_movie_cross_ref ON movies.id = user_movie_cross_ref.movieId 
        WHERE user_movie_cross_ref.userId = :userId
    """)
    fun getAllSavedMovies(userId: String): Flow<List<MovieEntity>>

    // Solo las favoritas/pendientes (isWatched = 0)
    @Query("""
        SELECT * FROM movies 
        INNER JOIN user_movie_cross_ref ON movies.id = user_movie_cross_ref.movieId 
        WHERE user_movie_cross_ref.userId = :userId AND user_movie_cross_ref.isWatched = 0
    """)
    fun getFavoriteMovies(userId: String): Flow<List<MovieEntity>>

    // Solo las vistas (isWatched = 1)
    @Query("""
        SELECT * FROM movies 
        INNER JOIN user_movie_cross_ref ON movies.id = user_movie_cross_ref.movieId 
        WHERE user_movie_cross_ref.userId = :userId AND user_movie_cross_ref.isWatched = 1
    """)
    fun getWatchedMovies(userId: String): Flow<List<MovieEntity>>

    @Query("UPDATE user_movie_cross_ref SET isWatched = 1 WHERE movieId = :movieId AND userId = :userId")
    suspend fun markAsWatched(movieId: Int, userId: String)

    @Query("UPDATE user_movie_cross_ref SET isWatched = 2 WHERE movieId = :movieId AND userId = :userId")
    suspend fun markAsDiscarded(movieId: Int, userId: String)

    @Query("DELETE FROM user_movie_cross_ref WHERE userId = :userId")
    suspend fun deleteAllMovies(userId: String)

    @Query("DELETE FROM user_movie_cross_ref WHERE isWatched = 1 AND userId = :userId")
    suspend fun deleteWatchedMovies(userId: String)
}