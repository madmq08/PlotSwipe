package com.mario.plotswipe.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [MovieEntity::class, UserMovieCrossRef::class], // Se añade la nueva tabla
    version = 3, // Limpieza
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

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
                    .fallbackToDestructiveMigration() //Borrar la tabla vieja y creará las dos nuevas
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}