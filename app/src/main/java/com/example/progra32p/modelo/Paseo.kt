package com.example.progra32p.modelo

// Modelos que usa la pantalla: limpios, sin anotaciones de Room ni de Gson.
data class ClimaActual(
    val temperatura: Double,
    val humedad: Int,
    val viento: Double,
    val hora: String
)

enum class NivelPaseo(val texto: String) {
    IDEAL("Ideal para pasear"),
    PRECAUCION("Con precaución"),
    EVITAR("Mejor en casa")
}

data class PaseoRaza(
    val raza: String,
    val subrazas: List<String>,
    val nivel: NivelPaseo,
    val consejo: String
)
