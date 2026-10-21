package com.example.progra32p.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.progra32p.modelo.ClimaActual
import com.example.progra32p.modelo.NivelPaseo
import com.example.progra32p.modelo.PaseoRaza
import com.example.progra32p.viewmodel.PaseosViewModel
import java.util.Locale

private val NivelPaseo.color: Color
    get() = when (this) {
        NivelPaseo.IDEAL -> VerdeIdeal
        NivelPaseo.PRECAUCION -> AmarilloPrecaucion
        NivelPaseo.EVITAR -> RojoEvitar
    }

@Composable
fun PaseosPantalla(viewModel: PaseosViewModel, modifier: Modifier = Modifier) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Encabezado(
                "OPEN-METEO + DOG CEO", "Paseos en La Paz",
                "El clima de ahora decide qué razas pueden salir a pasear.",
                AzulCielo, estado.actualizando, viewModel::actualizar
            )
        }
        estado.error?.let { error ->
            item { MensajeError(error, estado.totalRazas > 0, viewModel::actualizar) }
        }
        estado.clima?.let { clima -> item { TarjetaClima(clima) } }

        if (estado.totalRazas == 0) {
            if (estado.actualizando) item { Cargando() }
            return@LazyColumn
        }

        item {
            OutlinedTextField(
                value = estado.busqueda, onValueChange = viewModel::cambiarBusqueda,
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                label = { Text("Buscar raza") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = if (estado.busqueda.isNotEmpty()) {
                    { IconButton(onClick = { viewModel.cambiarBusqueda("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar búsqueda") } }
                } else null,
                shape = RoundedCornerShape(16.dp)
            )
        }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = estado.nivel == null, onClick = { viewModel.filtrarNivel(null) },
                    label = { Text("Todas (${estado.totalRazas})") }
                )
                NivelPaseo.entries.forEach { nivel ->
                    FilterChip(
                        selected = estado.nivel == nivel, onClick = { viewModel.filtrarNivel(nivel) },
                        label = { Text("${nivel.texto} (${estado.conteoPorNivel[nivel] ?: 0})") }
                    )
                }
            }
        }
        if (estado.razas.isEmpty()) item { SinResultados("Ninguna raza coincide con la búsqueda.") }
        items(estado.razas, key = { it.raza }) { raza -> TarjetaRaza(raza) }
    }
}

@Composable
private fun TarjetaClima(clima: ClimaActual) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AzulCielo, contentColor = Color.White),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("La Paz, ahora", style = MaterialTheme.typography.labelLarge)
                Text(String.format(Locale.getDefault(), "%.1f °C", clima.temperatura),
                    style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
                if (clima.hora.isNotEmpty()) Text("Medido a las ${clima.hora}", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Humedad", style = MaterialTheme.typography.labelSmall)
                Text("${clima.humedad} %", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Viento", style = MaterialTheme.typography.labelSmall)
                Text(String.format(Locale.getDefault(), "%.1f km/h", clima.viento), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TarjetaRaza(raza: PaseoRaza) {
    Card(shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(raza.raza, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f))
                Insignia(raza.nivel.texto, raza.nivel.color)
            }
            if (raza.subrazas.isNotEmpty()) Text(
                "Subrazas: ${raza.subrazas.joinToString(", ")}",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(raza.consejo, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
