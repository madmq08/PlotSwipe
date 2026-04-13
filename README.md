# PlotSwipe
PlotSwipe 🎬 | App móvil (PFC DAM) para descubrir películas con interfaz de swipe, usando la API de TMDB. Resuelve la parálisis por elección permitiendo crear una watchlist personal de forma visual y fluida.

# Tencologías utilizadas

- Lenguaje: Kotlin.

- UI Toolkit: Jetpack Compose (Material Design 3).

- Arquitectura: MVVM (Model-View-ViewModel) recomendada por Google.

- Navegación: Jetpack Navigation Compose (Single Activity Architecture).

- Base de Datos Local: Room (SQLite) para la persistencia de películas guardadas y vistas.

- Backend y Autenticación: Firebase Authentication (Email y Contraseña).

- Consumo de API REST: Retrofit 2 + GSON (para conexión con The Movie Database - TMDB).

- Carga de Imágenes Asíncrona: Coil (para renderizar pósters y logos de plataformas).

- Programación Reactiva y Asíncrona: Kotlin Coroutines & StateFlow.


# Pruebas realizadas

- Pruebas de Autenticación (Firebase): Se ha comprobado el correcto registro de nuevos usuarios, el inicio de sesión exitoso y el bloqueo de acceso cuando las credenciales son incorrectas o los campos están vacíos.

- Pruebas de Persistencia Local (Room): Se validó la correcta inserción de películas, la actualización de estados (movimiento de Favoritas a Vistas) y el borrado de registros. Se implementó un estado de "Descartada" para evitar que el algoritmo muestre películas rechazadas al reiniciar la app.

- Pruebas de Consumo de API: Se verificó la descarga dinámica de páginas de películas desde TMDB, incluyendo el manejo de errores si una película no tiene póster, y la obtención en tiempo real de plataformas de streaming disponibles en España.

- Pruebas de Navegación y Usabilidad (UX): Se comprobó el flujo seguro de pantallas (Login -> Home), el funcionamiento del BottomNavigationBar y la implementación de AlertDialogs (cuadros de confirmación) para evitar acciones accidentales por parte del usuario.
  
# Árbol de directorios 😉

```text
com.mario.plotswipe
│
├── MainActivity.kt          # Punto de entrada y Gestor de Rutas (NavHost + BottomBar)
│
├── ui/                      # CAPA DE VISTA Y PRESENTACIÓN (Jetpack Compose)
│   ├── AuthViewModel.kt     # Lógica de Autenticación y gestión de sesiones (Firebase)
│   ├── MovieViewModel.kt    # Lógica de negocio de películas, Swipe y estados
│   ├── LoginScreen.kt       # Interfaz de acceso y registro de usuarios
│   ├── SwipeScreen.kt       # Pantalla principal con motor de cartas (Tinder style)
│   ├── FavoritesScreen.kt   # Listado de películas guardadas (Pendientes)
│   ├── WatchedScreen.kt     # Listado de películas marcadas como vistas
│   └── DetailScreen.kt      # Información extendida y plataformas de streaming
│
└── data/                    # CAPA DE DATOS (Arquitectura de Repositorio)
    ├── MovieRepository.kt   # Gestor de datos (SSOT - Single Source of Truth)
    │
    ├── local/               # PERSISTENCIA LOCAL (Room Database)
    │   ├── MovieDatabase.kt # Definición de la base de datos SQLite
    │   ├── MovieDao.kt      # Interfaz de consultas SQL (Data Access Object)
    │   └── MovieEntity.kt   # Modelo de datos para la tabla de la base de datos
    │
    └── remote/              # CONEXIÓN EXTERNA (Retrofit & API)
        ├── RetrofitClient.kt# Configuración y cliente de conexión a TMDB
        ├── MovieDto.kt      # Modelo de transferencia de datos (JSON mapping)
        └── ProviderInfo.kt  # Modelo para logos y nombres de plataformas
