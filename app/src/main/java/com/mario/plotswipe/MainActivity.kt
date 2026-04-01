package com.mario.plotswipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState // 👈 Importante para leer estados de Firebase
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mario.plotswipe.ui.AuthViewModel // 👈 Importamos el cerebro del login
import com.mario.plotswipe.ui.DetailScreen
import com.mario.plotswipe.ui.FavoritesScreen
import com.mario.plotswipe.ui.LoginScreen // 👈 Importamos la pantalla de login
import com.mario.plotswipe.ui.MovieViewModel
import com.mario.plotswipe.ui.SwipeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // 🧠 1. Instanciamos el nuevo cerebro de usuarios
                val authViewModel: AuthViewModel = viewModel()

                // 🧠 2. Le preguntamos a Firebase: "¿Hay alguien conectado?"
                val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

                // 🚪 EL PORTERO DE LA DISCOTECA
                if (!isUserLoggedIn) {
                    // Si NO está logueado, mostramos SOLAMENTE la pantalla de Login
                    LoginScreen(authViewModel = authViewModel)
                } else {
                    // Si SÍ está logueado, cargamos tu app tal y como la tenías
                    // 1. Instanciamos la Brújula y el Cerebro (ViewModel) de las pelis
                    val navController = rememberNavController()
                    val viewModel: MovieViewModel = viewModel()

                    // 2. Scaffold nos crea una pantalla con espacio para un menú inferior
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            NavigationBar {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.destination?.route

                                // Botón 1: Inicio
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
                                // Botón 3: Vistas
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "Vistas") },
                                    label = { Text("Vistas") },
                                    selected = currentRoute == "watched",
                                    onClick = { navController.navigate("watched") { launchSingleTop = true } }
                                )
                            }
                        }
                    ) { paddingValues ->
                        // 3. El Gestor de Rutas
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
                                        navController.navigate("detail/$movieId")
                                    }
                                )
                            }
                            composable("watched") {
                                com.mario.plotswipe.ui.WatchedScreen(
                                    viewModel = viewModel,
                                    onMovieClick = { movieId ->
                                        navController.navigate("detail/$movieId")
                                    }
                                )
                            }
                            composable("detail/{movieId}") { backStackEntry ->
                                val movieIdString = backStackEntry.arguments?.getString("movieId")
                                val movieId = movieIdString?.toIntOrNull() ?: 0

                                DetailScreen(
                                    viewModel = viewModel,
                                    movieId = movieId,
                                    onBackClick = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}