# PlotSwipe
PlotSwipe 🎬 | App móvil (PFC DAM) para descubrir películas con interfaz de swipe, usando la API de TMDB. Resuelve la parálisis por elección permitiendo crear una watchlist personal de forma visual y fluida.

# Árbol de directorios 😉

com.mario.plotswipe
│
├── MainActivity.kt          # Punto de entrada y Gestor de Rutas (NavHost + BottomBar)
│
├── ui/                      # CAPA DE VISTA Y PRESENTACIÓN
│   ├── AuthViewModel.kt     # Cerebro para el Login/Registro (Firebase)
│   ├── MovieViewModel.kt    # Cerebro para las películas, Swipe y Base de Datos
│   ├── LoginScreen.kt       # Pantalla de acceso y registro de usuarios
│   ├── SwipeScreen.kt       # Pantalla principal (Motor tipo Tinder)
│   ├── FavoritesScreen.kt   # Pantalla de películas guardadas (Pendientes)
│   ├── WatchedScreen.kt     # Pantalla de películas ya vistas
│   └── DetailScreen.kt      # Pantalla con sinopsis y plataformas de streaming
│
└── data/                    # CAPA DE DATOS
    ├── MovieRepository.kt   # Intermediario entre la UI, Room y Retrofit
    │
    ├── local/               # BASE DE DATOS INTERNA (Room)
    │   ├── MovieDatabase.kt # Instancia de la base de datos SQLite
    │   ├── MovieDao.kt      # Consultas SQL (Insert, Delete, Update...)
    │   └── MovieEntity.kt   # Estructura de la tabla (Favoritas, Vistas, Descartadas)
    │
    └── remote/              # CONSUMO DE INTERNET (TMDB API)
        ├── RetrofitClient.kt# Configuración de conexión con The Movie Database
        ├── MovieDto.kt      # Modelo de datos que recibimos de la API
        └── ProviderInfo.kt  # Modelo para los logos de plataformas de streaming
