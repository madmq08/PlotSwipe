package com.mario.plotswipe.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mario.plotswipe.data.local.AppDatabase
import com.mario.plotswipe.data.remote.MovieDto
import com.mario.plotswipe.data.repository.MovieRepository
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    // Ahora el ViewModel sí le pasa la base de datos al Repositorio
    private val database = AppDatabase.getDatabase(application)
    private val repository = MovieRepository(database)

    var movies by mutableStateOf<List<MovieDto>>(emptyList())
        private set

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            movies = repository.fetchPopularMovies()
        }
    }

    // La nueva función mágica que decide qué hacer al deslizar
    fun handleSwipe(movie: MovieDto, isLiked: Boolean) {
        if (isLiked) {
            viewModelScope.launch {
                repository.insertMovieToFavorites(movie)
            }
        }
        if (movies.isNotEmpty()) {
            movies = movies.drop(1)
        }
    }
}