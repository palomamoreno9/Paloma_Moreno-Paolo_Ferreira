package com.example.mpazreserva.repository

import com.example.mpazreserva.data.model.Feriado
import com.example.mpazreserva.data.remote.RetrofitInstance

open class FeriadoRepository {
    open suspend fun getFeriados(): List<Feriado> {
        return RetrofitInstance.api.getFeriados()
    }
}