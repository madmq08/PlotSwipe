package com.mario.plotswipe.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun SwipeScreen(viewModel: MovieViewModel) {
    val peliculas = viewModel.movies

    // 🌟 Variables de estado para recordar dónde está el dedo
    val scope = rememberCoroutineScope()
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

            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                    .graphicsLayer { rotationZ = offsetX.value / 20f }
                    .pointerInput(peliculaActual) {
                        detectDragGestures(
                            onDragEnd = {
                                scope.launch {
                                    if (offsetX.value > 300f) {
                                        offsetX.animateTo(1000f, animationSpec = tween(300))
                                        viewModel.handleSwipe(peliculaActual, isLiked = true)
                                        offsetX.snapTo(0f)
                                        offsetY.snapTo(0f)
                                    } else if (offsetX.value < -300f) {
                                        offsetX.animateTo(-1000f, animationSpec = tween(300))
                                        viewModel.handleSwipe(peliculaActual, isLiked = false)
                                        offsetX.snapTo(0f)
                                        offsetY.snapTo(0f)
                                    } else {
                                        launch { offsetX.animateTo(0f, tween(300)) }
                                        launch { offsetY.animateTo(0f, tween(300)) }
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                scope.launch {
                                    offsetX.snapTo(offsetX.value + dragAmount.x)
                                    offsetY.snapTo(offsetY.value + dragAmount.y)
                                }
                            }
                        )
                    }
            ) {
                MovieCard(movie = peliculaActual)
            }
        }
    }
}