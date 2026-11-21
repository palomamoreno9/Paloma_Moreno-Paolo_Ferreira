package com.example.mpazreserva.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservas")
data class ReservationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val espacioNombre: String,
    val actividad: String,
    val cursoGrupo: String,
    val cantidadAsistentes: Int,
    val fechaReserva: String,
    val horaInicio: String,
    val horaFin: String,
    val estado: String = "Pendiente",
    val justificacion: String? = null
)