package com.example.progra32p.database

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla del Mapper 2 (Perros + Gatos): perros y gatos juntos en una sola lista.
@Entity(tableName = "mascotas")
data class MascotaEntity(
    @PrimaryKey val id: String,    // "perro-akita", "gato-3"
    val especie: String,           // PERRO / GATO
    val titulo: String,
    val descripcion: String,
    val orden: Int                 // posición en la lista mezclada
)
