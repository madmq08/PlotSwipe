package com.mario.plotswipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mario.plotswipe.ui.MovieCard
import com.mario.plotswipe.ui.MovieViewModel

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Llamamos a nuestra pantalla principal
                    PantallaPrincipal()
                }
            }
        }
    }
}

@Composable
fun PantallaPrincipal(viewModel: MovieViewModel = viewModel()) {
    val peliculas = viewModel.movies

    // 🌟 Variables de estado para recordar dónde está el dedo
    val scope = rememberCoroutineScope() // Esto nos deja lanzar animaciones
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (peliculas.isEmpty()) {
            CircularProgressIndicator()
        } else {
            val peliculaActual = peliculas.first()

            // Envolvemos la tarjeta en una caja "movible"
            Box(
                modifier = Modifier
                    // 1. Movemos la tarjeta según las variables X e Y
                    .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }

                    // 2. Le damos ese efecto "Tinder" de rotación al alejarla del centro
                    .graphicsLayer {
                        rotationZ = offsetX.value / 20f
                    }

                    // 3. Detectamos el dedo del usuario
                    .pointerInput(peliculaActual) {
                        detectDragGestures(
                            onDragEnd = {
                                // Cuando soltamos el dedo, lanzamos la animación
                                scope.launch {
                                    if (offsetX.value > 300f) {
                                        // 1. Vuela hacia la derecha suavemente (300 milisegundos)
                                        offsetX.animateTo(1000f, animationSpec = tween(300))
                                        // 2. Guarda la peli en Room
                                        viewModel.handleSwipe(peliculaActual, isLiked = true)
                                        // 3. Vuelve al centro (0) de forma invisible para que la nueva peli nazca ahí
                                        offsetX.snapTo(0f)
                                        offsetY.snapTo(0f)

                                    } else if (offsetX.value < -300f) {
                                        // 1. Vuela hacia la izquierda suavemente
                                        offsetX.animateTo(-1000f, animationSpec = tween(300))
                                        // 2. Descarta la peli
                                        viewModel.handleSwipe(peliculaActual, isLiked = false)
                                        // 3. Vuelve al centro invisiblemente
                                        offsetX.snapTo(0f)
                                        offsetY.snapTo(0f)

                                    } else {
                                        // EFECTO MUELLE: Si no la tiraste con fuerza, vuelve al centro suavemente
                                        launch { offsetX.animateTo(0f, tween(300)) }
                                        launch { offsetY.animateTo(0f, tween(300)) }
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                // Mientras arrastras el dedo, la tarjeta te sigue al instante (snapTo)
                                scope.launch {
                                    offsetX.snapTo(offsetX.value + dragAmount.x)
                                    offsetY.snapTo(offsetY.value + dragAmount.y)
                                }
                            }
                        )
                    }
            ) {
                // Aquí dentro dibujamos nuestra tarjeta de siempre
                MovieCard(movie = peliculaActual)
            }
        }
    }
}