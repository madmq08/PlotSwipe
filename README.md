# PlotSwipe
PlotSwipe 🎬 | App móvil (PFC DAM) para descubrir películas con interfaz de swipe, usando la API de TMDB. Resuelve la parálisis por elección permitiendo crear una watchlist personal de forma visual y fluida.

# Tencologías utilizadas

- Kotlin + Jetpack Compose
- Retrofit 2 (API REST)
- Room Database (Persistencia local)
- Firebase Auth (Autenticación de usuarios)
- MVVM Architecture

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
