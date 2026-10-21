package com.example.progra32p.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Una base de datos con las dos tablas, una por cada mapper.
@Database(entities = [PaseoEntity::class, MascotaEntity::class], version = 1, exportSchema = false)
abstract class ExploradorDatabase : RoomDatabase() {
    abstract fun paseoDao(): PaseoDao
    abstract fun mascotaDao(): MascotaDao

    companion object {
        @Volatile private var instancia: ExploradorDatabase? = null

        // Singleton: abrir la base de datos es costoso, se hace una sola vez en toda la app.
        fun obtener(context: Context): ExploradorDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext, ExploradorDatabase::class.java, "explorador.db"
                ).build().also { instancia = it }
            }
    }
}
