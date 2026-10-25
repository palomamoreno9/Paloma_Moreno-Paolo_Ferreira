package com.example.mpazreserva.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppDao {
    // Autenticación de usuarios por rol (Solicitante / Inspectoria)
    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun registrarUsuario(usuario: UserEntity)

    // Gestión de Reservas de espacios
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun crearReserva(reserva: ReservationEntity)

    @Query("SELECT * FROM reservas ORDER BY id DESC")
    suspend fun obtenerTodasLasReservas(): List<ReservationEntity>

    @Query("SELECT * FROM reservas WHERE usuarioId = :usuarioId ORDER BY id DESC")
    suspend fun obtenerReservasPorUsuario(usuarioId: Int): List<ReservationEntity>

    @Update
    suspend fun actualizarReserva(reserva: ReservationEntity)
}