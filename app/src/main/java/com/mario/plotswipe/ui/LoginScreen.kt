package com.mario.plotswipe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoginScreen(authViewModel: AuthViewModel) {
    // Variables para guardar lo que el usuario escribe
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Leemos el error del ViewModel por si hay que mostrarlo
    val errorMessage by authViewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la app
        Text(
            text = "PlotSwipe 🍿",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Inicia sesión para guardar tus películas")
        Spacer(modifier = Modifier.height(32.dp))

        // Caja de texto para el EMAIL
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                authViewModel.clearError() // Limpiamos el error si empieza a escribir
            },
            label = { Text("Correo electrónico") },
            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = "Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Caja de texto para la CONTRASEÑA
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                authViewModel.clearError()
            },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(), // Oculta los caracteres con puntitos
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Si hay un error, lo mostramos en rojo
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage!!, color = Color.Red, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón de ENTRAR
        Button(
            onClick = { authViewModel.login(email.trim(), password.trim()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de REGISTRO
        OutlinedButton(
            onClick = { authViewModel.register(email.trim(), password.trim()) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Crear cuenta nueva", fontSize = 16.sp)
        }
    }
}