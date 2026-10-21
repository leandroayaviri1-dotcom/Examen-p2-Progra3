package com.example.progra32p.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.progra32p.api.RetrofitClient
import com.example.progra32p.database.ExploradorDatabase
import com.example.progra32p.mapper.PerrosGatosMapper
import com.example.progra32p.mapper.aModelo
import com.example.progra32p.modelo.Especie
import com.example.progra32p.modelo.Mascota
import com.example.progra32p.repository.ApiException
import com.example.progra32p.repository.GatosRepository
import com.example.progra32p.repository.PerrosRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MascotasUiState(
    val mascotas: List<Mascota> = emptyList(),  // ya filtradas
    val conteo: Map<Especie, Int> = emptyMap(),
    val especie: Especie? = null,               // null = perros y gatos
    val actualizando: Boolean = false,
    val error: String? = null
)

private data class ControlMascotas(
    val especie: Especie? = null,
    val actualizando: Boolean = false,
    val error: String? = null
)

// ViewModel del Mapper 2: Repositorio de Perros + Repositorio de Gatos.
class MascotasViewModel(application: Application) : AndroidViewModel(application) {
    private val perrosRepository = PerrosRepository(RetrofitClient.dogCeo)
    private val gatosRepository = GatosRepository(RetrofitClient.catFacts)
    private val dao = ExploradorDatabase.obtener(application).mascotaDao()

    private val control = MutableStateFlow(ControlMascotas())

    val estado: StateFlow<MascotasUiState> = combine(dao.observarTodas(), control) { filas, c ->
        val todas = filas.map { it.aModelo() }
        MascotasUiState(
            mascotas = if (c.especie == null) todas else todas.filter { it.especie == c.especie },
            conteo = todas.groupingBy { it.especie }.eachCount(),
            especie = c.especie,
            actualizando = c.actualizando,
            error = c.error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MascotasUiState(actualizando = true))

    init {
        actualizar()
    }

    fun filtrar(especie: Especie?) = control.update { it.copy(especie = especie) }

    fun actualizar() {
        if (control.value.actualizando) return
        control.update { it.copy(actualizando = true, error = null) }
        viewModelScope.launch {
            try {
                coroutineScope {
                    val razas = async { perrosRepository.razas() }
                    val datos = async { gatosRepository.datosCuriosos() }
                    dao.reemplazarTodo(PerrosGatosMapper.combinar(razas.await(), datos.await()))
                }
                control.update { it.copy(actualizando = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                control.update { it.copy(actualizando = false, error = e.message) }
            } catch (e: Exception) {
                control.update { it.copy(actualizando = false, error = "No se pudieron actualizar las mascotas.") }
            }
        }
    }
}
