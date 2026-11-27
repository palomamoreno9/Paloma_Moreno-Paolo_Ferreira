package com.example.mpazreserva.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservas")
data class ReservationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val solicitanteEmail: String,
    val espacioNombre: String,
    val fechaReserva: String,
    val horario: String,
    val actividad: String,
    val documentoUrl: String = "",
    val estado: String = "Pendiente",
    val motivoRechazo: String = ""
)