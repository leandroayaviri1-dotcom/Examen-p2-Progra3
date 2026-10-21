package com.example.progra32p.dto

import com.google.gson.annotations.SerializedName

// DTO de Cat Facts: viene paginado y cada elemento de "data" es un dato curioso.
data class DatosGatoDto(
    @SerializedName("current_page") val paginaActual: Int?,
    val total: Int?,
    val data: List<DatoGatoDto>?
)

data class DatoGatoDto(
    val fact: String?,
    val length: Int?
)
