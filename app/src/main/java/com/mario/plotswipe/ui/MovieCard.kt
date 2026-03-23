package com.mario.plotswipe.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mario.plotswipe.data.remote.MovieDto

@Composable
fun MovieCard(movie: MovieDto, modifier: Modifier = Modifier) {
    // TMDB nos da solo el final de la ruta del póster, nosotros le ponemos el "https" delante
    val imageUrl = "https://image.tmdb.org/t/p/w500${movie.posterPath}"

    // Card es la "tarjeta" física con bordes redondeados y un poco de sombra
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.66f), // Proporción clásica de póster de cine (2:3)
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        // Box nos permite poner cosas una encima de otra (como capas de Photoshop)
        Box(modifier = Modifier.fillMaxSize()) {

            // Capa 1: El Póster descargado de internet
            AsyncImage(
                model = imageUrl,
                contentDescription = "Póster de ${movie.title}",
                contentScale = ContentScale.Crop, // Recorta la imagen para que llene la tarjeta
                modifier = Modifier.fillMaxSize()
            )

            // Capa 2: Un fondo semitransparente abajo para que el texto se lea bien
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f)) // 70% de opacidad
                    .padding(16.dp)
            ) {
                Column {
                    // Título de la película
                    Text(
                        text = movie.title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Sinopsis (limitada a 3 líneas para que no ocupe toda la pantalla)
                    Text(
                        text = movie.overview,
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis // Pone "..." si el texto es muy largo
                    )
                }
            }
        }
    }
}