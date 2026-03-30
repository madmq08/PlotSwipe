package com.mario.plotswipe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.mario.plotswipe.data.remote.ProviderInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: MovieViewModel, movieId: Int, onBackClick: () -> Unit) {
    // Buscamos la película exacta en nuestra lista de favoritas usando el ID
    val favoriteMovies by viewModel.peliculasFavoritas.collectAsState(initial = emptyList())
    val movie = favoriteMovies.find { it.id == movieId }

    // 🧠 ESTADOS PARA EL DIÁLOGO DE CONFIRMACIÓN
    var showDialog by remember { mutableStateOf(false) }

    // 📢 EL DIÁLOGO (Aparece si showDialog es true)
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Ya la has visto?") },
            text = { Text("Esta película se moverá a tu lista de 'Vistas' y desaparecerá de tus favoritas.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.marcarComoVista(movieId)
                    showDialog = false
                    onBackClick() // 👈 Volvemos atrás automáticamente para que vea que ya no está en favoritos
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (movie != null) {
            LaunchedEffect(movie.id) {
                viewModel.getProvidersForCard(movie.id)
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        LogosFlotantes(logos = viewModel.providersCache[movie.id] ?: emptyList())
                    }
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = movie.title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Mostramos la sinopsis real si la tienes en MovieEntity
                    Text(text = movie.overview ?: "Sinopsis no disponible", fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    PlataformasStreaming(
                        viewModel = viewModel,
                        movieId = movieId,
                        onOpenConfirm = { showDialog = true } // 👈 Pasamos el aviso de abrir diálogo
                    )
                }
            }
        }
    }
}

@Composable
fun PlataformasStreaming(viewModel: MovieViewModel, movieId: Int, onOpenConfirm: () -> Unit) {
    LaunchedEffect(key1 = movieId) {
        viewModel.loadProvidersForMovie(movieId)
    }

    val plataformas = viewModel.movieProviders

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = "Dónde ver:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (plataformas.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(plataformas) { provider ->
                    val imageUrl = "https://image.tmdb.org/t/p/w92${provider.logoPath}"
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Logo de ${provider.providerName}",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }
        } else {
            Text(
                text = "No disponible en streaming en España actualmente.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón modificado para que llame al diálogo en lugar de marcar directamente
        Button(
            onClick = onOpenConfirm, // 👈 Ahora avisa arriba para que salga el AlertDialog
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            )
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Vista",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Marcar como Vista", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun LogosFlotantes(logos: List<ProviderInfo>) {
    if (logos.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            items(logos) { provider ->
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w92${provider.logoPath}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}