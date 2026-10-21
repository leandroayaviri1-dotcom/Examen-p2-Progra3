package com.example.progra32p.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/** Título de cada pantalla; la etiqueta dice qué dos APIs se combinan. */
@Composable
fun Encabezado(
    etiqueta: String,
    titulo: String,
    descripcion: String,
    acento: Color,
    actualizando: Boolean,
    onActualizar: () -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(etiqueta, color = acento, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Text(titulo, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Text(descripcion, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            if (actualizando) CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
            else FilledTonalIconButton(onClick = onActualizar) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
            }
        }
    }
}

@Composable
fun Insignia(texto: String, color: Color) {
    Text(
        texto, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold,
        modifier = Modifier.clip(RoundedCornerShape(50)).background(color).padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

/** Si ya hay datos guardados el error se muestra como aviso: la lista sigue visible. */
@Composable
fun MensajeError(mensaje: String, hayDatosGuardados: Boolean, onReintentar: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(mensaje, color = MaterialTheme.colorScheme.onErrorContainer, textAlign = TextAlign.Center)
            if (hayDatosGuardados) Text(
                "Mostrando los últimos datos guardados en el teléfono.",
                style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onReintentar) { Text("Reintentar") }
        }
    }
}

@Composable
fun Cargando() {
    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun SinResultados(texto: String) {
    Text(texto, modifier = Modifier.fillMaxWidth().padding(24.dp), textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant)
}
