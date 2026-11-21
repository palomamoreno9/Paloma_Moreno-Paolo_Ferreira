package com.example.mpazreserva.data.remote

import com.example.mpazreserva.data.model.Feriado
import retrofit2.http.GET

interface ApiService {
    @GET("api/v3/PublicHolidays/2026/CL")
    suspend fun getFeriados(): List<Feriado>
}