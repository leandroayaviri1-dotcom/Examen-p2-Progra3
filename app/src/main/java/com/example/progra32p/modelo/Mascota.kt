package com.example.progra32p.modelo

enum class Especie { PERRO, GATO }

data class Mascota(
    val id: String,
    val especie: Especie,
    val titulo: String,
    val descripcion: String
)
