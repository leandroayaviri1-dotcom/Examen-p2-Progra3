package com.example.progra32p.dto

import com.google.gson.annotations.SerializedName

// DTO: copia exacta del JSON de Open-Meteo. Todo es nullable porque Gson no garantiza nada.
data class ClimaDto(
    val timezone: String?,
    @SerializedName("current") val actual: ClimaActualDto?
)

data class ClimaActualDto(
    val time: String?,
    @SerializedName("temperature_2m") val temperatura: Double?,
    @SerializedName("relative_humidity_2m") val humedad: Int?,
    @SerializedName("wind_speed_10m") val viento: Double?
)
