package com.mario.plotswipe.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
// Añadimos un "onMovieClick" para avisar a la brújula de que queremos viajar
fun FavoritesScreen(viewModel: MovieViewModel, onMovieClick: (Int) -> Unit) {
    val favoriteMovies by viewModel.peliculasFavoritas.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Mis Películas Favoritas",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favoriteMovies.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Aún no tienes favoritas. ¡Haz swipe a la derecha!")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                // MAGIA: El weight(1f) fuerza a la cuadrícula a usar el espacio sobrante y habilita el scroll perfecto
                modifier = Modifier.weight(1f)
            ) {
                items(favoriteMovies) { movie ->
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2f / 3f)
                            // MAGIA 2: Hacemos que la imagen se pueda tocar y pasamos su ID
                            .clickable { onMovieClick(movie.id) }
                    )
                }
            }
        }
    }
}