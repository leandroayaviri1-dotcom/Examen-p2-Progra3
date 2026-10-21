package com.example.progra32p.repository

import com.example.progra32p.api.CatFactsApi
import com.example.progra32p.dto.DatosGatoDto

// Repositorio 3: solo sabe pedir los datos curiosos a Cat Facts.
class GatosRepository(private val api: CatFactsApi) {

    suspend fun datosCuriosos(cantidad: Int = 10): DatosGatoDto {
        val datos = llamar("Cat Facts") { api.datos(cantidad) }
        if (datos.data.isNullOrEmpty()) throw ApiException("Cat Facts no devolvió datos curiosos.")
        return datos
    }
}
