package com.example.progra32p.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.progra32p.modelo.Especie
import com.example.progra32p.modelo.Mascota
import com.example.progra32p.viewmodel.MascotasViewModel

@Composable
fun MascotasPantalla(viewModel: MascotasViewModel, modifier: Modifier = Modifier) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    val total = estado.conteo.values.sum()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Encabezado(
                "DOG CEO + CAT FACTS", "Mascotas",
                "Razas de perros y datos curiosos de gatos en una sola lista.",
                NaranjaMascotas, estado.actualizando, viewModel::actualizar
            )
        }
        estado.error?.let { error ->
            item { MensajeError(error, total > 0, viewModel::actualizar) }
        }
        if (total == 0) {
            if (estado.actualizando) item { Cargando() }
            return@LazyColumn
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = estado.especie == null, onClick = { viewModel.filtrar(null) },
                    label = { Text("Todos ($total)") })
                FilterChip(selected = estado.especie == Especie.PERRO, onClick = { viewModel.filtrar(Especie.PERRO) },
                    label = { Text("Perros (${estado.conteo[Especie.PERRO] ?: 0})") })
                FilterChip(selected = estado.especie == Especie.GATO, onClick = { viewModel.filtrar(Especie.GATO) },
                    label = { Text("Gatos (${estado.conteo[Especie.GATO] ?: 0})") })
            }
        }
        items(estado.mascotas, key = { it.id }) { mascota -> TarjetaMascota(mascota) }
    }
}

@Composable
private fun TarjetaMascota(mascota: Mascota) {
    val esGato = mascota.especie == Especie.GATO
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = if (esGato) CardDefaults.cardColors(containerColor = NaranjaMascotas.copy(alpha = 0.14f))
        else CardDefaults.cardColors()
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(if (esGato) "🐱" else "🐶", fontSize = 30.sp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    mascota.titulo, fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (esGato) NaranjaMascotas else MaterialTheme.colorScheme.onSurface
                )
                Text(mascota.descripcion, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
