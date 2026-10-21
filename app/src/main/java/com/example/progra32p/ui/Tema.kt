package com.example.progra32p.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores de cada sección y de los niveles de paseo.
val AzulCielo = Color(0xFF1E88E5)
val NaranjaMascotas = Color(0xFFEF6C00)
val VerdeIdeal = Color(0xFF2E7D32)
val AmarilloPrecaucion = Color(0xFFF9A825)
val RojoEvitar = Color(0xFFC62828)

private val claro = lightColorScheme(
    primary = Color(0xFF2E7D32), onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9), onPrimaryContainer = Color(0xFF0B3D10),
    secondaryContainer = Color(0xFFDCEFD9),
    background = Color(0xFFF4F7F2), surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE4EBE1)
)
private val oscuro = darkColorScheme(
    primary = Color(0xFF8BD58F), primaryContainer = Color(0xFF1F5223),
    background = Color(0xFF111411), surface = Color(0xFF1A1D1A),
    surfaceVariant = Color(0xFF2A302A)
)

@Composable
fun ExploradorTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) oscuro else claro, content = content)
}
