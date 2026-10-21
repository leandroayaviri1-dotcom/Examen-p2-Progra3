package com.example.progra32p.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Un solo cliente HTTP compartido y un Retrofit por cada URL base.
object RetrofitClient {
    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun retrofit(baseUrl: String) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val openMeteo: OpenMeteoApi by lazy {
        retrofit("https://api.open-meteo.com/").create(OpenMeteoApi::class.java)
    }
    val dogCeo: DogApi by lazy {
        retrofit("https://dog.ceo/api/").create(DogApi::class.java)
    }
    val catFacts: CatFactsApi by lazy {
        retrofit("https://catfact.ninja/").create(CatFactsApi::class.java)
    }
}
