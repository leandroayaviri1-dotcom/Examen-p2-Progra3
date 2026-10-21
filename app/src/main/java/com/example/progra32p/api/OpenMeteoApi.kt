package com.example.progra32p.api

import com.example.progra32p.dto.ClimaDto
import retrofit2.http.GET
import retrofit2.http.Query

// API 1: clima actual de La Paz (temperatura, humedad y viento).
interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun climaActual(
        @Query("latitude") latitud: Double = -16.5,
        @Query("longitude") longitud: Double = -68.15,
        @Query("current") datos: String = "temperature_2m,relative_humidity_2m,wind_speed_10m",
        @Query("timezone") zonaHoraria: String = "America/La_Paz"
    ): ClimaDto
}
