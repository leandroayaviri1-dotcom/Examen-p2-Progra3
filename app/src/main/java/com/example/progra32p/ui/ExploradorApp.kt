package com.example.progra32p.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.progra32p.viewmodel.MascotasViewModel
import com.example.progra32p.viewmodel.PaseosViewModel

// Una pestaña por mapper.
private enum class Seccion(val titulo: String, val icono: ImageVector) {
    PASEOS("Clima y perros", Icons.Default.LocationOn),
    MASCOTAS("Mascotas", Icons.Default.Favorite)
}

@Composable
fun ExploradorApp() {
    // Los ViewModels viven en la Activity: cada pestaña conserva sus datos al cambiar.
    val paseos: PaseosViewModel = viewModel()
    val mascotas: MascotasViewModel = viewModel()
    var actual by rememberSaveable { mutableStateOf(Seccion.PASEOS) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Seccion.entries.forEach { seccion ->
                    NavigationBarItem(
                        selected = actual == seccion,
                        onClick = { actual = seccion },
                        icon = { Icon(seccion.icono, contentDescription = null) },
                        label = { Text(seccion.titulo) }
                    )
                }
            }
        }
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when (actual) {
            Seccion.PASEOS -> PaseosPantalla(paseos, modifier)
            Seccion.MASCOTAS -> MascotasPantalla(mascotas, modifier)
        }
    }
}
