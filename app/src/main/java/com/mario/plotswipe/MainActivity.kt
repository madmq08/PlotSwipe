package com.mario.plotswipe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // 👈 NUEVO
import androidx.compose.runtime.remember // 👈 NUEVO
import androidx.compose.runtime.setValue // 👈 NUEVO
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.mario.plotswipe.ui.AuthViewModel
import com.mario.plotswipe.ui.DetailScreen
import com.mario.plotswipe.ui.FavoritesScreen
import com.mario.plotswipe.ui.LoginScreen
import com.mario.plotswipe.ui.MovieViewModel
import com.mario.plotswipe.ui.SwipeScreen

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val authViewModel: AuthViewModel = viewModel()
                val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()

                if (!isUserLoggedIn) {
                    LoginScreen(authViewModel = authViewModel)
                } else {
                    val navController = rememberNavController()
                    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                    val viewModel: MovieViewModel = viewModel(key = currentUserUid)

                    // 👇 VARIABLE PARA CONTROLAR SI SE MUESTRA EL MENSAJE DE AVISO 👇
                    var showLogoutDialog by remember { mutableStateOf(false) }

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentRoute = navBackStackEntry?.destination?.route

                            if (currentRoute != null && !currentRoute.startsWith("detail")) {
                                TopAppBar(
                                    title = { Text("PlotSwipe 🍿") },
                                    actions = {
                                        // 👇 AHORA EL BOTÓN SOLO ABRE EL DIÁLOGO 👇
                                        IconButton(onClick = { showLogoutDialog = true }) {
                                            Icon(
                                                imageVector = Icons.Filled.ExitToApp,
                                                contentDescription = "Cerrar Sesión",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            NavigationBar {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentRoute = navBackStackEntry?.destination?.route

                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
                                    label = { Text("Inicio") },
                                    selected = currentRoute == "swipe",
                                    onClick = { navController.navigate("swipe") { launchSingleTop = true } }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoritos") },
                                    label = { Text("Favoritos") },
                                    selected = currentRoute == "favorites",
                                    onClick = { navController.navigate("favorites") { launchSingleTop = true } }
                                )
                                NavigationBarItem(
                                    icon = { Icon(Icons.Filled.CheckCircle, contentDescription = "Vistas") },
                                    label = { Text("Vistas") },
                                    selected = currentRoute == "watched",
                                    onClick = { navController.navigate("watched") { launchSingleTop = true } }
                                )
                            }
                        }
                    ) { paddingValues ->
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

                        // 👇 EL CUADRO DE DIÁLOGO DE CONFIRMACIÓN 👇
                        if (showLogoutDialog) {
                            AlertDialog(
                                onDismissRequest = { showLogoutDialog = false },
                                title = { Text("Cerrar Sesión") },
                                text = { Text("¿Estás seguro de que quieres salir de tu cuenta?") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showLogoutDialog = false // Escondemos el mensaje
                                            authViewModel.logout()   // 🚪 ¡Cerramos sesión!
                                        }
                                    ) {
                                        Text("Salir", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showLogoutDialog = false } // Solo escondemos
                                    ) {
                                        Text("Cancelar")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}