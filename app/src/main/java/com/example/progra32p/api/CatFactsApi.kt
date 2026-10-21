package com.example.progra32p.api

import com.example.progra32p.dto.DatosGatoDto
import retrofit2.http.GET
import retrofit2.http.Query

// API 3: datos curiosos sobre gatos.
interface CatFactsApi {
    @GET("facts")
    suspend fun datos(@Query("limit") limite: Int = 10): DatosGatoDto
}
