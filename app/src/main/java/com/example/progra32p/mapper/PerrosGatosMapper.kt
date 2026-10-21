package com.example.progra32p.mapper

import com.example.progra32p.database.MascotaEntity
import com.example.progra32p.dto.DatosGatoDto
import com.example.progra32p.dto.RazasDto
import com.example.progra32p.modelo.Especie
import com.example.progra32p.modelo.Mascota

/**
 * MAPPER 2 = API Dog CEO (Repositorio 2) + API Cat Facts (Repositorio 3) -> tabla "mascotas".
 * Une razas de perros y datos de gatos en UNA sola lista, con los datos de gatos repartidos entre los perros.
 */
object PerrosGatosMapper {

    /** Combina los DOS repositorios en una lista mezclada y numerada. */
    fun combinar(razas: RazasDto, datos: DatosGatoDto): List<MascotaEntity> {
        val perros = razas.message.orEmpty().map { (raza, subrazas) ->
            MascotaEntity(
                id = "perro-$raza",
                especie = Especie.PERRO.name,
                titulo = raza.capitalizar(),
                descripcion = describirSubrazas(subrazas),
                orden = 0
            )
        }
        val gatos = datos.data.orEmpty()
            .mapNotNull { it.fact?.trim()?.takeIf(String::isNotEmpty) }
            .mapIndexed { i, dato ->
                MascotaEntity(
                    id = "gato-${i + 1}",
                    especie = Especie.GATO.name,
                    // Si el dato compara con los perros, se destaca: es justo lo que une a las dos APIs.
                    titulo = if (dato.contains("dog", ignoreCase = true)) "Gato vs. perro" else "Dato curioso #${i + 1}",
                    descripcion = dato,
                    orden = 0
                )
            }

        // Reparte los gatos a lo largo de la lista: un bloque de perros, un gato, otro bloque, otro gato...
        val bloques = if (gatos.isEmpty()) listOf(perros) else perros.chunked(maxOf(1, (perros.size + gatos.size - 1) / gatos.size))
        val mezcla = mutableListOf<MascotaEntity>()
        for (i in 0 until maxOf(bloques.size, gatos.size)) {
            bloques.getOrNull(i)?.let(mezcla::addAll)
            gatos.getOrNull(i)?.let(mezcla::add)
        }
        return mezcla.mapIndexed { posicion, mascota -> mascota.copy(orden = posicion) }
    }

    fun describirSubrazas(subrazas: List<String>): String = when (subrazas.size) {
        0 -> "Sin subrazas"
        1 -> "1 subraza: ${subrazas[0].capitalizar()}"
        else -> "${subrazas.size} subrazas: ${subrazas.joinToString(", ") { it.capitalizar() }}"
    }
}

fun MascotaEntity.aModelo() = Mascota(
    id = id,
    especie = Especie.entries.firstOrNull { it.name == especie } ?: Especie.PERRO,
    titulo = titulo,
    descripcion = descripcion
)
