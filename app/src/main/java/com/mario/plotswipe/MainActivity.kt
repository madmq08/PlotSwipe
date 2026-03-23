package com.mario.plotswipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mario.plotswipe.ui.DetailScreen
import com.mario.plotswipe.ui.FavoritesScreen
import com.mario.plotswipe.ui.MovieViewModel
import com.mario.plotswipe.ui.SwipeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // 1. Instanciamos la Brújula y el Cerebro (ViewModel)
                val navController = rememberNavController()
                val viewModel: MovieViewModel = viewModel()

                // 2. Scaffold nos crea una pantalla con espacio para un menú inferior
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            // Averiguamos en qué pantalla estamos para pintar el botón de un color u otro
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            // Botón 1: Inicio (Swipe)
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                                label = { Text("Inicio") },
                                selected = currentRoute == "swipe",
                                onClick = { navController.navigate("swipe") { launchSingleTop = true } }
                            )
                            // Botón 2: Favoritos
                            NavigationBarItem(
                                icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoritos") },
                                label = { Text("Favoritos") },
                                selected = currentRoute == "favorites",
                                onClick = { navController.navigate("favorites") { launchSingleTop = true } }
                            )
                        }
                    }
                ) { paddingValues ->
                    // 3. El Gestor de Rutas: Muestra una pantalla u otra según el botón pulsado
                    NavHost(
                        navController = navController,
                        startDestination = "swipe",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("swipe") {
                            SwipeScreen(viewModel = viewModel)
                        }
                        composable("favorites") {
                            FavoritesScreen(
                                viewModel = viewModel,
                                onMovieClick = { movieId ->
                                    // Cuando tocamos una peli, viajamos a la ruta de detalle con su ID
                                    navController.navigate("detail/$movieId")
                                }
                            )
                        }
                        // 🌟 NUEVA RUTA: La pantalla de detalle
                        composable("detail/{movieId}") { backStackEntry ->
                            // Rescatamos el ID de la URL a la que hemos viajado
                            val movieIdString = backStackEntry.arguments?.getString("movieId")
                            val movieId = movieIdString?.toIntOrNull() ?: 0

                            DetailScreen(
                                viewModel = viewModel,
                                movieId = movieId,
                                onBackClick = { navController.popBackStack() } // Para volver atrás
                            )
                        }
                    }
                }
            }
        }
    }
}