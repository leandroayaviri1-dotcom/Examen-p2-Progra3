package com.example.progra32p.repository

import com.example.progra32p.api.DogApi
import com.example.progra32p.dto.RazasDto

// Repositorio 2: solo sabe pedir las razas a Dog CEO. Lo usan los DOS mappers.
class PerrosRepository(private val api: DogApi) {

    suspend fun razas(): RazasDto {
        val razas = llamar("Dog CEO") { api.razas() }
        if (razas.status != "success" || razas.message.isNullOrEmpty()) {
            throw ApiException("Dog CEO no devolvió el listado de razas.")
        }
        return razas
    }
}
