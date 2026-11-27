package com.example.mpazreserva.network

import com.example.mpazreserva.data.ReservationEntity
import com.example.mpazreserva.data.UserEntity

//Modelo JSON que viaja hacia y desde Spring Boot (Reservas)
data class ReservaDto(
    val id: Int? = null,
    val solicitanteEmail: String,
    val espacioNombre: String,
    val fechaReserva: String,
    val horario: String,
    val actividad: String,
    val documentoUrl: String = "",
    val estado: String = "Pendiente",
    val motivoRechazo: String = ""
)

//Funciones para convertir entre Spring Boot (JSON) y Room (SQLite local)
fun ReservaDto.toEntity() = ReservationEntity(
    id = id ?: 0,
    solicitanteEmail = solicitanteEmail,
    espacioNombre = espacioNombre,
    fechaReserva = fechaReserva,
    horario = horario,
    actividad = actividad,
    documentoUrl = documentoUrl,
    estado = estado,
    motivoRechazo = motivoRechazo
)

fun ReservationEntity.toDto() = ReservaDto(
    id = if (id == 0) null else id,
    solicitanteEmail = solicitanteEmail,
    espacioNombre = espacioNombre,
    fechaReserva = fechaReserva,
    horario = horario,
    actividad = actividad,
    documentoUrl = documentoUrl,
    estado = estado,
    motivoRechazo = motivoRechazo
)

//Spring Boot (Usuarios)
data class UsuarioDto(
    val id: Long? = null,
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String = "Solicitante"
)

fun UsuarioDto.toEntity() = UserEntity(
    nombre = this.nombre,
    email = this.email,
    password = this.password,
    rol = this.rol
)

fun UserEntity.toDto() = UsuarioDto(
    nombre = this.nombre,
    email = this.email,
    password = this.password,
    rol = this.rol
)

//Modelo JSON para la API externa (Feriados oficiales de Chile)
data class FeriadoDto(
    val date: String,
    val localName: String
)