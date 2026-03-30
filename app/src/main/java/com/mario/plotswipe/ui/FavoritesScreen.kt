package com.mario.plotswipe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.graphics.Color

@Composable
fun FavoritesScreen(viewModel: MovieViewModel, onMovieClick: (Int) -> Unit) {
    val favoriteMovies by viewModel.peliculasFavoritas.collectAsState(initial = emptyList())

    // 🧠 ESTADOS PARA EL DIÁLOGO
    var showDialog by remember { mutableStateOf(false) }
    var movieToMark by remember { mutableStateOf<Int?>(null) }

    // 📢 EL DIÁLOGO DE CONFIRMACIÓN
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Película vista?") },
            text = { Text("Esta película se moverá a tu lista de 'Vistas' y desaparecerá de Favoritos.") },
            confirmButton = {
                TextButton(onClick = {
                    movieToMark?.let { viewModel.marcarComoVista(it) }
                    showDialog = false
                }) {
                    Text("Confirmar", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Favoritas",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { viewModel.vaciarFavoritos() }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Borrar todo",
                    tint = Color.Red
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
                modifier = Modifier.weight(1f)
            ) {
                items(favoriteMovies) { movie ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                            contentDescription = movie.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f / 3f)
                                .clickable { onMovieClick(movie.id) }
                        )

                        // 👇 CAMBIO AQUÍ: Ahora el botón activa el diálogo 👇
                        IconButton(
                            onClick = {
                                movieToMark = movie.id // Guardamos qué peli es
                                showDialog = true      // Mostramos el diálogo
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.8f), shape = androidx.compose.foundation.shape.CircleShape)
                                .size(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
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