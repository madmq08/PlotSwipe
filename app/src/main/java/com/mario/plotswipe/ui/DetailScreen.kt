package com.mario.plotswipe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import com.mario.plotswipe.data.remote.ProviderInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: MovieViewModel, movieId: Int, onBackClick: () -> Unit) {
    // Buscamos la película exacta en nuestra lista de favoritas usando el ID
    val favoriteMovies by viewModel.peliculasFavoritas.collectAsState(initial = emptyList())
    val movie = favoriteMovies.find { it.id == movieId }

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
                    .verticalScroll(rememberScrollState()) // Scroll para textos muy largos
            ) {
                // 1. El "Marco" que envuelve todo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    // 2. La imagen de fondo (le ponemos fillMaxSize para que llene el marco)
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 3. Los logos anclados a la esquina inferior derecha
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp) // Un pequeño margen para que respiren
                    ) {
                        // Sacamos los logos de nuestra caché inteligente
                        LogosFlotantes(logos = viewModel.providersCache[movie.id] ?: emptyList())
                    }
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = movie.title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    // Si tu base de datos tiene la sinopsis, ponla aquí (ej. movie.overview)
                    Text(text = "Aquí irá la sinopsis de la película...", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp)) // Un pequeño espacio para que respire el diseño

                    // Llamamos a nuestro componente de las plataformas
                    PlataformasStreaming(
                        viewModel = viewModel,
                        movieId = movieId // Fíjate que usamos 'movieId' que te entra por la línea 28
                    )
                }
            }
        }
    }
}
@Composable
fun PlataformasStreaming(viewModel: MovieViewModel, movieId: Int) {
    // 1. LaunchedEffect: Esto se ejecuta automáticamente nada más aparecer en pantalla
    LaunchedEffect(key1 = movieId) {
        viewModel.loadProvidersForMovie(movieId)
    }

    // 2. Leemos la lista de plataformas que el ViewModel ha traído de internet
    val plataformas = viewModel.movieProviders

    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = "Dónde ver:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (plataformas.isNotEmpty()) {
            // Si hay plataformas, las pintamos en una fila desplazable
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(plataformas) { provider ->
                    // TMDB nos da solo el trozo final de la ruta, hay que ponerle la URL base de imágenes delante
                    val imageUrl = "https://image.tmdb.org/t/p/w92${provider.logoPath}"

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Logo de ${provider.providerName}",
                        modifier = Modifier
                            .size(50.dp) // Tamaño del logo cuadradito
                            .clip(RoundedCornerShape(12.dp)) // Bordes redondeados para que quede elegante
                    )
                }
            }
        } else {
            // Si la lista está vacía, avisamos al usuario
            Text(
                text = "No disponible en streaming en España actualmente.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
fun LogosFlotantes(logos: List<ProviderInfo>) {
    if (logos.isNotEmpty()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp) // Un pequeño margen con el borde de la imagen
        ) {
            items(logos) { provider ->
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w92${provider.logoPath}",
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp) // Tamaño del logo
                        .clip(RoundedCornerShape(8.dp)) // Bordes redonditos
                )
            }
        }
    }
}