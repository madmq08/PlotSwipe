package com.mario.plotswipe.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mario.plotswipe.data.local.AppDatabase
import com.mario.plotswipe.data.remote.MovieDto
import com.mario.plotswipe.data.remote.ProviderInfo
import com.mario.plotswipe.data.repository.MovieRepository
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    // Ahora el ViewModel sí le pasa la base de datos al Repositorio
    private val database = AppDatabase.getDatabase(application)
    private val repository = MovieRepository(database)
    private var currentPage = 1

    var movies by mutableStateOf<List<MovieDto>>(emptyList())
        private set

    var movieProviders by mutableStateOf<List<ProviderInfo>>(emptyList())
        private set

    val peliculasFavoritas = repository.getAllSavedMovies()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                val nuevasPeliculas = repository.fetchPopularMovies(page = currentPage)

                // 🛡️ FILTRO: Solo dejamos pasar a las películas que NO estén ya en nuestra mano
                val peliculasSinRepetir = nuevasPeliculas.filter { nueva ->
                    movies.none { existente -> existente.id == nueva.id }
                }

                // Añadimos solo las que han pasado el filtro
                movies = movies + peliculasSinRepetir

            } catch (e: Exception) {
                // ...
            }
        }
    }

    // La nueva función mágica que decide qué hacer al deslizar
    fun handleSwipe(movie: MovieDto, isLiked: Boolean) {
        if (isLiked) {
            viewModelScope.launch {
                repository.insertMovieToFavorites(movieDto = movie)
            }
        }

        // 1. Quitamos la carta que acabamos de deslizar (la primera)
        if (movies.isNotEmpty()) {
            movies = movies.drop(1)
        }

        // 2. EL MOTOR INFINITO: Si nos quedan 3 cartas o menos en la mano...
        if (movies.size <= 3) {
            currentPage++ // Pasamos a la siguiente página (2, luego 3, luego 4...)
            loadMovies()  // Llamamos a internet en segundo plano
        }
    }
    // Llamaremos a esta función cuando el usuario pulse en una película para ver sus detalles
    fun loadProvidersForMovie(movieId: Int) {
        viewModelScope.launch {
            // Vaciamos la lista anterior para que no salgan logos de la peli anterior mientras carga
            movieProviders = emptyList()
            // Pedimos los logos nuevos al repositorio
            movieProviders = repository.getMovieProviders(movieId)
        }
    }
    fun vaciarFavoritos() {
        // Usamos viewModelScope.launch porque borrar en base de datos es una tarea pesada
        // y hay que hacerla en un "hilo secundario" (en la sombra)
        viewModelScope.launch {
            repository.deleteAllMovies()
        }
    }
}