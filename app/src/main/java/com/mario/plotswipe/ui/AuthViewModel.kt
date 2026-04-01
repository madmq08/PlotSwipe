package com.mario.plotswipe.ui

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {
    // 1. Instanciamos la herramienta de Firebase
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // 2. Estado para saber si ya hay un usuario dentro de la app
    private val _isUserLoggedIn = MutableStateFlow(auth.currentUser != null)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    // 3. Estado para mostrar mensajes de error en rojo si se equivocan
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 🔐 Función para INICIAR SESIÓN
    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Por favor, rellena todos los campos."
            return
        }
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                _isUserLoggedIn.value = true
                _errorMessage.value = null
            }
            .addOnFailureListener {
                _errorMessage.value = "Error al entrar: Correo o contraseña incorrectos."
            }
    }

    // 📝 Función para REGISTRARSE
    fun register(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Por favor, rellena todos los campos."
            return
        }
        if (pass.length < 6) {
            _errorMessage.value = "La contraseña debe tener al menos 6 caracteres."
            return
        }
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                _isUserLoggedIn.value = true
                _errorMessage.value = null
            }
            .addOnFailureListener {
                _errorMessage.value = "Error al registrar: ${it.message}"
            }
    }

    // 🚪 Función para CERRAR SESIÓN (La usaremos más adelante)
    fun logout() {
        auth.signOut()
        _isUserLoggedIn.value = false
    }

    // Limpiar errores (útil cuando cambiamos de escribir a borrar)
    fun clearError() {
        _errorMessage.value = null
    }
}