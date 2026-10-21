package com.example.progra32p.repository

import com.example.progra32p.api.OpenMeteoApi
import com.example.progra32p.dto.ClimaDto

// Repositorio 1: solo sabe pedir el clima a Open-Meteo.
class ClimaRepository(private val api: OpenMeteoApi) {

    suspend fun climaActual(): ClimaDto {
        val clima = llamar("Open-Meteo") { api.climaActual() }
        if (clima.actual?.temperatura == null) throw ApiException("Open-Meteo no devolvió el clima actual.")
        return clima
    }
}
