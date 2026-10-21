package com.example.progra32p

import com.example.progra32p.api.CatFactsApi
import com.example.progra32p.api.DogApi
import com.example.progra32p.api.OpenMeteoApi
import com.example.progra32p.repository.ApiException
import com.example.progra32p.repository.ClimaRepository
import com.example.progra32p.repository.GatosRepository
import com.example.progra32p.repository.PerrosRepository
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Cada repositorio contra un servidor falso que responde con JSON real de cada API.
class RepositoriosTest {
    private lateinit var servidor: MockWebServer

    @Before fun iniciar() { servidor = MockWebServer().apply { start() } }
    @After fun detener() { servidor.shutdown() }

    private inline fun <reified T> api(): T = Retrofit.Builder()
        .baseUrl(servidor.url("/"))
        .addConverterFactory(GsonConverterFactory.create())
        .build().create(T::class.java)

    private fun responder(json: String, codigo: Int = 200) =
        servidor.enqueue(MockResponse().setResponseCode(codigo).setBody(json))

    @Test
    fun climaPideLaPazYLeeTemperaturaHumedadYViento() = runBlocking {
        responder("""{"timezone":"America/La_Paz","current":{"time":"2026-10-21T08:00","interval":900,
            "temperature_2m":9.1,"relative_humidity_2m":65,"wind_speed_10m":0.9}}""")

        val clima = ClimaRepository(api<OpenMeteoApi>()).climaActual()

        val ruta = servidor.takeRequest().requestUrl!!
        assertEquals("/v1/forecast", ruta.encodedPath)
        assertEquals("-16.5", ruta.queryParameter("latitude"))
        assertEquals("-68.15", ruta.queryParameter("longitude"))
        assertEquals("temperature_2m,relative_humidity_2m,wind_speed_10m", ruta.queryParameter("current"))
        assertEquals("America/La_Paz", ruta.queryParameter("timezone"))
        assertEquals(9.1, clima.actual!!.temperatura!!, 0.0)
        assertEquals(65, clima.actual!!.humedad)
        assertEquals(0.9, clima.actual!!.viento!!, 0.0)
    }

    @Test
    fun perrosLeeRazasYSubrazas() = runBlocking {
        responder("""{"message":{"affenpinscher":[],"bulldog":["boston","english","french"]},"status":"success"}""")

        val razas = PerrosRepository(api<DogApi>()).razas()

        assertEquals("/breeds/list/all", servidor.takeRequest().path)
        assertEquals(listOf("boston", "english", "french"), razas.message!!["bulldog"])
        assertEquals(2, razas.message!!.size)
    }

    @Test
    fun gatosPideDiezDatos() = runBlocking {
        responder("""{"current_page":1,"total":332,"data":[{"fact":"A group of cats is called a clowder.","length":38}]}""")

        val datos = GatosRepository(api<CatFactsApi>()).datosCuriosos()

        assertEquals("/facts?limit=10", servidor.takeRequest().path)
        assertEquals("A group of cats is called a clowder.", datos.data!!.single().fact)
    }

    @Test
    fun unErrorHttpSeConvierteEnMensajeParaElUsuario() = runBlocking {
        responder("{}", codigo = 500)
        try {
            PerrosRepository(api<DogApi>()).razas()
            fail("Debía lanzar ApiException")
        } catch (e: ApiException) {
            assertTrue(e.message!!.contains("Dog CEO"))
            assertTrue(e.message!!.contains("500"))
        }
    }

    @Test
    fun unaRespuestaSinDatosTambienEsError() = runBlocking {
        responder("""{"current_page":1,"total":0,"data":[]}""")
        try {
            GatosRepository(api<CatFactsApi>()).datosCuriosos()
            fail("Debía lanzar ApiException")
        } catch (e: ApiException) {
            assertEquals("Cat Facts no devolvió datos curiosos.", e.message)
        }
    }
}
