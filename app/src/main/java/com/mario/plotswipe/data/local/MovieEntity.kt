package com.mario.plotswipe.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// TABLA 1: Solo información técnica de la película (Limpia)
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey
    val id: Int, // ID de TMDB
    val title: String,
    val posterPath: String?,
    val overview: String
)

// TABLA 2: La relación entre usuario y película
@Entity(
    tableName = "user_movie_cross_ref",
    primaryKeys = ["userId", "movieId"], // Clave compuesta: Soluciona el error
    indices = [Index(value = ["movieId"])] // Optimiza la búsqueda por película
)
data class UserMovieCrossRef(
    val userId: String,
    val movieId: Int,
    val isLiked: Boolean = false,
    val isWatched: Int = 0
)