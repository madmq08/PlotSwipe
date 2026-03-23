package com.mario.plotswipe.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Hemos subido la versión a 2 porque añadimos la columna "overview"
@Database(entities = [MovieEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Conectamos con el DAO (las instrucciones de la base de datos)
    abstract fun movieDao(): MovieDao

    // Companion object es como una "fábrica" que nos da la base de datos cuando se la pedimos
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plotswipe_database"
                )
                    // ¡EL TRUCO NINJA AUTOMÁTICO! 🥷
                    // Si cambiamos los planos (añadir overview), borra la vieja y crea una nueva sola.
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}