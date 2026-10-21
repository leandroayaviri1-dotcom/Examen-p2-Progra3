package com.example.progra32p.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.progra32p.api.RetrofitClient
import com.example.progra32p.database.ExploradorDatabase
import com.example.progra32p.mapper.ClimaPerrosMapper
import com.example.progra32p.mapper.aClima
import com.example.progra32p.mapper.aModelo
import com.example.progra32p.modelo.ClimaActual
import com.example.progra32p.modelo.NivelPaseo
import com.example.progra32p.modelo.PaseoRaza
import com.example.progra32p.repository.ApiException
import com.example.progra32p.repository.ClimaRepository
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

data class PaseosUiState(
    val clima: ClimaActual? = null,
    val razas: List<PaseoRaza> = emptyList(),   // ya filtradas
    val totalRazas: Int = 0,
    val conteoPorNivel: Map<NivelPaseo, Int> = emptyMap(),
    val busqueda: String = "",
    val nivel: NivelPaseo? = null,              // null = todos los niveles
    val actualizando: Boolean = false,
    val error: String? = null
)

// Lo que el ViewModel controla por su cuenta (lo demás sale de Room).
private data class ControlPaseos(
    val busqueda: String = "",
    val nivel: NivelPaseo? = null,
    val actualizando: Boolean = false,
    val error: String? = null
)

// ViewModel del Mapper 1: Repositorio de Clima + Repositorio de Perros.
class PaseosViewModel(application: Application) : AndroidViewModel(application) {
    private val climaRepository = ClimaRepository(RetrofitClient.openMeteo)
    private val perrosRepository = PerrosRepository(RetrofitClient.dogCeo)
    private val dao = ExploradorDatabase.obtener(application).paseoDao()

    private val control = MutableStateFlow(ControlPaseos())

    // La pantalla = tabla "paseos" de Room + búsqueda/filtro/errores.
    val estado: StateFlow<PaseosUiState> = combine(dao.observarTodos(), control) { filas, c ->
        val razas = filas.map { it.aModelo() }
        PaseosUiState(
            clima = filas.firstOrNull()?.aClima(),
            razas = razas.filter {
                (c.nivel == null || it.nivel == c.nivel) &&
                    it.raza.contains(c.busqueda.trim(), ignoreCase = true)
            },
            totalRazas = razas.size,
            conteoPorNivel = razas.groupingBy { it.nivel }.eachCount(),
            busqueda = c.busqueda,
            nivel = c.nivel,
            actualizando = c.actualizando,
            error = c.error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PaseosUiState(actualizando = true))

    init {
        actualizar()
    }

    fun cambiarBusqueda(texto: String) = control.update { it.copy(busqueda = texto) }

    fun filtrarNivel(nivel: NivelPaseo?) = control.update { it.copy(nivel = nivel) }

    /** API -> Repositorio -> Mapper -> Room. Si falla, lo que ya estaba guardado sigue en pantalla. */
    fun actualizar() {
        if (control.value.actualizando) return
        control.update { it.copy(actualizando = true, error = null) }
        viewModelScope.launch {
            try {
                coroutineScope {
                    // Las dos APIs se piden al mismo tiempo; el mapper necesita las dos.
                    val clima = async { climaRepository.climaActual() }
                    val razas = async { perrosRepository.razas() }
                    dao.reemplazarTodo(ClimaPerrosMapper.combinar(clima.await(), razas.await()))
                }
                control.update { it.copy(actualizando = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: ApiException) {
                control.update { it.copy(actualizando = false, error = e.message) }
            } catch (e: Exception) {
                control.update { it.copy(actualizando = false, error = "No se pudo actualizar el clima y las razas.") }
            }
        }
    }
}
