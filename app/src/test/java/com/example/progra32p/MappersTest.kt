package com.example.progra32p

import com.example.progra32p.dto.ClimaActualDto
import com.example.progra32p.dto.ClimaDto
import com.example.progra32p.dto.DatoGatoDto
import com.example.progra32p.dto.DatosGatoDto
import com.example.progra32p.dto.RazasDto
import com.example.progra32p.mapper.ClimaPerrosMapper
import com.example.progra32p.mapper.PerrosGatosMapper
import com.example.progra32p.mapper.aClima
import com.example.progra32p.mapper.aModelo
import com.example.progra32p.modelo.Especie
import com.example.progra32p.modelo.NivelPaseo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MappersTest {

    private val razas = RazasDto(
        message = mapOf(
            "akita" to emptyList(), "bulldog" to listOf("boston", "english", "french"),
            "chihuahua" to emptyList(), "husky" to emptyList(), "pug" to emptyList()
        ),
        status = "success"
    )

    private fun clima(temperatura: Double, viento: Double = 5.0) =
        ClimaDto("America/La_Paz", ClimaActualDto("2026-10-21T08:00", temperatura, 65, viento))

    // ---------- Mapper 1: Clima + Perros ----------

    @Test
    fun climaPerrosCreaUnaFilaPorRazaConElClima() {
        val filas = ClimaPerrosMapper.combinar(clima(9.1), razas)

        assertEquals(5, filas.size)
        val bulldog = filas.first { it.raza == "bulldog" }
        assertEquals("boston,english,french", bulldog.subrazas)
        assertEquals(9.1, bulldog.temperatura, 0.0)
        assertEquals(65, bulldog.humedad)
        assertEquals("08:00", bulldog.horaClima)
        assertEquals(listOf("Boston", "English", "French"), bulldog.aModelo().subrazas)
        assertEquals("08:00", bulldog.aClima().hora)
    }

    @Test
    fun conFrioElHuskyEsIdealYElChihuahuaNecesitaAbrigo() {
        val filas = ClimaPerrosMapper.combinar(clima(9.0), razas).associateBy { it.raza }

        assertEquals(NivelPaseo.IDEAL.name, filas.getValue("husky").nivel)
        assertEquals(NivelPaseo.PRECAUCION.name, filas.getValue("chihuahua").nivel)
        assertTrue(filas.getValue("chihuahua").consejo.contains("abrigo"))
    }

    @Test
    fun conMuchoFrioElChihuahuaMejorEnCasa() {
        val (nivel, _) = ClimaPerrosMapper.evaluar("chihuahua", 2.0, 65, 5.0)
        assertEquals(NivelPaseo.EVITAR, nivel)
    }

    @Test
    fun conCalorLasRazasChatasMejorEnCasa() {
        assertEquals(NivelPaseo.EVITAR, ClimaPerrosMapper.evaluar("pug", 30.0, 65, 5.0).first)
        assertEquals(NivelPaseo.PRECAUCION, ClimaPerrosMapper.evaluar("husky", 26.0, 65, 5.0).first)
    }

    @Test
    fun elVientoFuerteBajaUnDiaIdealAPrecaucion() {
        val (nivel, consejo) = ClimaPerrosMapper.evaluar("akita", 15.0, 65, 40.0)
        assertEquals(NivelPaseo.PRECAUCION, nivel)
        assertTrue(consejo.contains("40 km/h"))
    }

    @Test
    fun laHumedadAltaAfectaALasRazasChatas() {
        assertEquals(NivelPaseo.IDEAL, ClimaPerrosMapper.evaluar("pug", 20.0, 50, 5.0).first)
        assertEquals(NivelPaseo.PRECAUCION, ClimaPerrosMapper.evaluar("pug", 20.0, 80, 5.0).first)
        assertTrue(ClimaPerrosMapper.evaluar("akita", 10.0, 90, 5.0).second.contains("90 %"))
    }

    // ---------- Mapper 2: Perros + Gatos ----------

    @Test
    fun perrosGatosMezclaLasDosApisEnUnaSolaLista() {
        val datos = DatosGatoDto(1, 2, listOf(
            DatoGatoDto("Cats sleep a lot.", 17),
            DatoGatoDto("Cats make 100 sounds. Dogs make only 10.", 40)
        ))

        val lista = PerrosGatosMapper.combinar(razas, datos)

        assertEquals(7, lista.size)
        assertEquals((0 until 7).toList(), lista.map { it.orden })
        // 5 perros y 2 gatos: bloques de 3 perros, luego un gato.
        assertEquals(listOf("PERRO", "PERRO", "PERRO", "GATO", "PERRO", "PERRO", "GATO"), lista.map { it.especie })
        assertEquals("Dato curioso #1", lista[3].titulo)
        assertEquals("Gato vs. perro", lista[6].titulo)

        val bulldog = lista.first { it.id == "perro-bulldog" }.aModelo()
        assertEquals(Especie.PERRO, bulldog.especie)
        assertEquals("Bulldog", bulldog.titulo)
        assertEquals("3 subrazas: Boston, English, French", bulldog.descripcion)
    }

    @Test
    fun perrosGatosIgnoraDatosVaciosYFuncionaSinGatos() {
        val sinGatos = PerrosGatosMapper.combinar(razas, DatosGatoDto(1, 0, listOf(DatoGatoDto("  ", 0), DatoGatoDto(null, null))))
        assertEquals(5, sinGatos.size)
        assertTrue(sinGatos.all { it.especie == "PERRO" })
        assertEquals("Sin subrazas", PerrosGatosMapper.describirSubrazas(emptyList()))
        assertEquals("1 subraza: Wild", PerrosGatosMapper.describirSubrazas(listOf("wild")))
    }
}
