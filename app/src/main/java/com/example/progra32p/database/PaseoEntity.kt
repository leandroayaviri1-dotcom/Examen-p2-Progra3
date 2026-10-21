package com.example.progra32p.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla del Mapper 1 (Clima + Perros): una fila por raza con el clima con el que se calculó el consejo.
@Entity(tableName = "paseos")
data class PaseoEntity(
    @PrimaryKey val raza: String,
    val subrazas: String,          // separadas por coma: "boston,english,french"
    val temperatura: Double,
    val humedad: Int,
    val viento: Double,
    val horaClima: String,
    val nivel: String,             // IDEAL / PRECAUCION / EVITAR
    val consejo: String
)
