package com.mario.plotswipe.ui

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Done

@Composable
// Añadimos un "onMovieClick" para avisar a la brújula de que queremos viajar
fun FavoritesScreen(viewModel: MovieViewModel, onMovieClick: (Int) -> Unit) {
    val favoriteMovies by viewModel.peliculasFavoritas.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Favoritas",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // EL BOTÓN DEL PÁNICO
            IconButton(onClick = { viewModel.vaciarFavoritos() }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Borrar todo",
                    tint = Color.Red // Papelera roja para que quede claro
                )
            }
        }

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
                    // Metemos la imagen en un Box para poder superponer el botón
                    Box(modifier = Modifier.fillMaxWidth()) {

                        // Capa 1: El póster de la película
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                            contentDescription = movie.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f / 3f)
                                .clickable { onMovieClick(movie.id) }
                        )

                        // Capa 2: El botón de "Marcar como Vista" MEJORADO
                        IconButton(
                            onClick = { viewModel.marcarComoVista(movie.id) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                // Fondo negro casi opaco y un poco más grande
                                .background(Color.Black.copy(alpha = 0.8f), shape = androidx.compose.foundation.shape.CircleShape)
                                .size(42.dp)
                        ) {
                            Icon(
                                // Usamos un icono mucho más grueso y sólido
                                imageVector = androidx.compose.material.icons.Icons.Filled.CheckCircle,
                                contentDescription = "Marcar como vista",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}