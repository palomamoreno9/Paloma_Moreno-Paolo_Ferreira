package com.example.mpazreserva.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface MpazApiService {
    @GET("api/reservas")
    suspend fun obtenerTodasLasReservas(): List<ReservaDto>

    @GET("api/reservas/usuario/{email}")
    suspend fun obtenerReservasPorUsuario(@Path("email") email: String): List<ReservaDto>

    @POST("api/reservas")
    suspend fun crearReserva(@Body reserva: ReservaDto): Response<ReservaDto>

    @PUT("api/reservas/{id}")
    suspend fun actualizarReserva(
        @Path("id") id: Int,
        @Body reserva: ReservaDto
    ): Response<ReservaDto>

    @DELETE("api/reservas/{id}")
    suspend fun eliminarReserva(@Path("id") id: Int): Response<Unit>

    @POST("api/usuarios/registro")
    suspend fun registrarUsuario(@Body usuario: UsuarioDto): Response<UsuarioDto>

    @POST("api/usuarios/login")
    suspend fun loginUsuario(@Body credenciales: UsuarioDto): Response<UsuarioDto>

    @PUT("api/usuarios/{email}")
    suspend fun actualizarUsuario(
        @Path("email") email: String,
        @Body usuario: UsuarioDto
    ): Response<UsuarioDto>
}

interface FeriadosApiService {
    @GET("api/v3/PublicHolidays/2026/CL")
    suspend fun obtenerFeriadosChile(): List<FeriadoDto>
}

object RetrofitClient {
    private const val BASE_URL_SPRING = "https://mpaz-backend-springboot.onrender.com/"
    private const val BASE_URL_FERIADOS = "https://date.nager.at/"

    val mpazApi: MpazApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_SPRING)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MpazApiService::class.java)
    }

    val feriadosApi: FeriadosApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_FERIADOS)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FeriadosApiService::class.java)
    }
}