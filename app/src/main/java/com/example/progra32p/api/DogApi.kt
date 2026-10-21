package com.example.progra32p.api

import com.example.progra32p.dto.RazasDto
import retrofit2.http.GET

// API 2: listado de razas de perros con sus subrazas.
interface DogApi {
    @GET("breeds/list/all")
    suspend fun razas(): RazasDto
}
