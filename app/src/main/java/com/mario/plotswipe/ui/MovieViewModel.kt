package com.mario.plotswipe.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mario.plotswipe.data.local.AppDatabase
import com.mario.plotswipe.data.local.MovieEntity
import com.mario.plotswipe.data.remote.MovieDto
import com.mario.plotswipe.data.remote.ProviderInfo
import com.mario.plotswipe.data.repository.MovieRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    // Ahora el ViewModel sí le pasa la base de datos al Repositorio
    private val database = AppDatabase.getDatabase(application)
    private val repository = MovieRepository(database)
    private var currentPage = 1

    var movies by mutableStateOf<List<MovieDto>>(emptyList())
        private set

    var movieProviders by mutableStateOf<List<ProviderInfo>>(emptyList())
        private set

    val providersCache = mutableStateMapOf<Int, List<ProviderInfo>>()

    val peliculasFavoritas: StateFlow<List<MovieEntity>> = repository.getFavoriteMovies()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val peliculasVistas: StateFlow<List<MovieEntity>> = repository.getWatchedMovies()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                // 1. Pedimos las pelis
                val nuevasPeliculas = repository.fetchPopularMovies(page = currentPage)
                val pelisGuardadas = repository.getAllSavedMovies().first()

                // 2. Filtramos
                val peliculasFiltradas = nuevasPeliculas.filter { nueva ->
                    val noEstaEnMano = movies.none { existente -> existente.id == nueva.id }
                    val noEstaEnDB = pelisGuardadas.none { guardada -> guardada.id == nueva.id }

                    // 🌟 EL FILTRO MÁGICO: Si la sinopsis está vacía, a la basura
                    val tieneSinopsis = nueva.overview != null && nueva.overview.isNotBlank()

                    // 🖼️ EL FILTRO VISUAL: Si no tiene póster, no la queremos
                    val tienePoster = nueva.posterPath != null && nueva.posterPath.isNotBlank()

                    // La peli solo entra si cumple las CUATRO cosas
                    noEstaEnMano && noEstaEnDB && tieneSinopsis && tienePoster
                }

                // 3. Añadimos las cartas válidas a la mano
                movies = movies + peliculasFiltradas

                // 🚀 4. NUEVO: EL MOTOR TURBO
                // Si después de filtrar nos quedan 3 cartas o menos, pedimos la siguiente página
                // (El 'isNotEmpty' evita que entremos en bucle si TMDB no tiene más películas en el mundo)
                if (movies.size <= 3 && nuevasPeliculas.isNotEmpty()) {
                    currentPage++
                    loadMovies() // 🔁 La función se llama a sí misma para seguir buscando
                }

            } catch (e: Exception) {
                // Manejo de errores
            }
        }
    }
    // La nueva función mágica que decide qué hacer al deslizar
    fun handleSwipe(movie: MovieDto, isLiked: Boolean) {
        if (isLiked) {
            // Caso Derecha (Like): Guardamos como favorita (isWatched = 0)
            viewModelScope.launch {
                repository.insertMovieToFavorites(movie)
            }
        } else {
            // 👇 NUEVO: Caso Izquierda (Dislike) 👇
            // Guardamos como descartada (isWatched = 2) para que no vuelva a salir
            descartarPelicula(movie)
        }

        // 1. Quitamos la carta de la pantalla
        if (movies.isNotEmpty()) {
            movies = movies.drop(1)
        }

        // 2. EL MOTOR INFINITO: Si nos quedan 3 cartas o menos, pedimos más a internet
        if (movies.size <= 3) {
            currentPage++
            loadMovies()
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
    fun getProvidersForCard(movieId: Int) {
        if (!providersCache.containsKey(movieId)) {
            viewModelScope.launch {
                val logos = repository.getMovieProviders(movieId)
                providersCache[movieId] = logos // Lo guardamos en el diccionario
            }
        }
    }
    fun marcarComoVista(movieId: Int) {
        viewModelScope.launch {
            repository.markAsWatched(movieId)
        }
    }

    fun vaciarVistas() {
        viewModelScope.launch {
            repository.deleteWatchedMovies()
        }
    }

    fun descartarPelicula(movieDto: MovieDto) {
        viewModelScope.launch {
            // La guardamos en la DB con el estado 2 (Descartada)
            val entity = MovieEntity(
                id = movieDto.id,
                title = movieDto.title,
                posterPath = movieDto.posterPath ?: "",
                overview = movieDto.overview,
                isWatched = 2 // 2 significa "No me la vuelvas a enseñar"
            )
            repository.insertMovieToFavorites(movieDto) // Primero la insertamos
            repository.markAsDiscarded(movieDto.id)     // Luego la marcamos como descartada
        }
    }
}