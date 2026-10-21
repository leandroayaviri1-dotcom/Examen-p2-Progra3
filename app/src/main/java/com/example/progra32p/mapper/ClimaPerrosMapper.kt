package com.example.progra32p.mapper

import com.example.progra32p.database.PaseoEntity
import com.example.progra32p.dto.ClimaDto
import com.example.progra32p.dto.RazasDto
import com.example.progra32p.modelo.ClimaActual
import com.example.progra32p.modelo.NivelPaseo
import com.example.progra32p.modelo.PaseoRaza
import kotlin.math.roundToInt

/**
 * MAPPER 1 = API Open-Meteo (Repositorio 1) + API Dog CEO (Repositorio 2) -> tabla "paseos".
 * Por cada raza decide, según el clima actual de La Paz, si es buen momento para sacarla a pasear.
 */
object ClimaPerrosMapper {

    // Razas de pelaje grueso o de climas fríos.
    private val resistentesAlFrio = setOf(
        "husky", "malamute", "samoyed", "stbernard", "newfoundland", "pyrenees", "akita", "shiba",
        "elkhound", "chow", "leonberg", "mountain", "eskimo", "keeshond", "komondor", "kuvasz",
        "buhund", "finnish", "ovcharka", "tervuren", "groenendael", "bouvier", "briard",
        "entlebucher", "appenzeller", "spitz", "wolfhound", "deerhound"
    )

    // Razas pequeñas o de pelo muy corto que pasan frío.
    private val sensiblesAlFrio = setOf(
        "chihuahua", "mexicanhairless", "whippet", "greyhound", "basenji", "pinscher", "dachshund",
        "papillon", "maltese", "havanese", "saluki", "dalmatian", "pitbull", "vizsla", "weimaraner",
        "pointer", "cotondetulear", "frise", "rajapalayam", "chippiparai", "kombai", "mudhol", "pariah"
    )

    // Razas de hocico chato (braquicéfalas) que sufren con el calor.
    private val chatos = setOf(
        "pug", "puggle", "bulldog", "boxer", "pekinese", "shihtzu", "lhasa", "sharpei", "mastiff",
        "affenpinscher", "brabancon"
    )

    /** Combina los DOS repositorios: una fila por raza, cada una con el clima con el que se evaluó. */
    fun combinar(clima: ClimaDto, razas: RazasDto): List<PaseoEntity> {
        val actual = clima.actual
        val temperatura = actual?.temperatura ?: 0.0
        val humedad = actual?.humedad ?: 0
        val viento = actual?.viento ?: 0.0
        val hora = actual?.time?.substringAfter('T').orEmpty()

        return razas.message.orEmpty().map { (raza, subrazas) ->
            val (nivel, consejo) = evaluar(raza, temperatura, humedad, viento)
            PaseoEntity(
                raza = raza,
                subrazas = subrazas.joinToString(","),
                temperatura = temperatura,
                humedad = humedad,
                viento = viento,
                horaClima = hora,
                nivel = nivel.name,
                consejo = consejo
            )
        }
    }

    /** La regla que une las dos APIs: el dato de la raza (Dog CEO) contra el clima (Open-Meteo). */
    fun evaluar(raza: String, temperatura: Double, humedad: Int, viento: Double): Pair<NivelPaseo, String> {
        val grados = "${temperatura.roundToInt()} °C"
        var (nivel, consejo) = when {
            raza in sensiblesAlFrio && temperatura < 5 ->
                NivelPaseo.EVITAR to "Con $grados su pelo corto no alcanza: mejor jugar en casa."
            raza in sensiblesAlFrio && temperatura < 12 ->
                NivelPaseo.PRECAUCION to "Con $grados ponle un abrigo y que el paseo sea corto."
            raza in chatos && temperatura >= 28 ->
                NivelPaseo.EVITAR to "Con $grados le cuesta respirar: sal temprano o al atardecer."
            raza in chatos && temperatura >= 22 ->
                NivelPaseo.PRECAUCION to "Con $grados haz un paseo tranquilo y lleva agua."
            // Con humedad alta el calor les cuesta más aunque no haga tanto.
            raza in chatos && temperatura >= 18 && humedad >= 70 ->
                NivelPaseo.PRECAUCION to "Con $grados y $humedad % de humedad le cuesta respirar: paseo corto."
            raza in resistentesAlFrio && temperatura >= 25 ->
                NivelPaseo.PRECAUCION to "Con $grados su pelaje le da calor: busca sombra y agua."
            raza in resistentesAlFrio && temperatura < 12 ->
                NivelPaseo.IDEAL to "¡$grados es su clima favorito! Paseo largo."
            temperatura < 0 ->
                NivelPaseo.PRECAUCION to "Con $grados cuida sus patas del frío y del hielo."
            temperatura >= 30 ->
                NivelPaseo.PRECAUCION to "Con $grados evita el asfalto caliente."
            else ->
                NivelPaseo.IDEAL to "Con $grados es buen momento para salir."
        }
        if (viento >= 30 && nivel == NivelPaseo.IDEAL) {
            nivel = NivelPaseo.PRECAUCION
            consejo += " Ojo: hay viento fuerte (${viento.roundToInt()} km/h)."
        }
        if (humedad >= 85) consejo += " Humedad muy alta ($humedad %): puede llover, sécalo al volver."
        return nivel to consejo
    }
}

// De la tabla a los modelos de la pantalla.
fun PaseoEntity.aModelo() = PaseoRaza(
    raza = raza.capitalizar(),
    subrazas = subrazas.split(',').filter { it.isNotBlank() }.map { it.capitalizar() },
    nivel = NivelPaseo.entries.firstOrNull { it.name == nivel } ?: NivelPaseo.IDEAL,
    consejo = consejo
)

// Todas las filas comparten el mismo clima: se toma de cualquiera.
fun PaseoEntity.aClima() = ClimaActual(temperatura = temperatura, humedad = humedad, viento = viento, hora = horaClima)

internal fun String.capitalizar() = replaceFirstChar { it.uppercase() }
