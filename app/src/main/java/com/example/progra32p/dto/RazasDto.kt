package com.example.progra32p.dto

// DTO de Dog CEO: "message" es un mapa raza -> subrazas, p. ej. "bulldog": ["boston", "english", "french"].
data class RazasDto(
    val message: Map<String, List<String>>?,
    val status: String?
)
