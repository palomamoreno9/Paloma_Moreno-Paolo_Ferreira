package com.example.mpazreserva.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AppDao {

    // --- AUTENTICACIÓN Y USUARIOS ---

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password LIMIT 1")
    fun login(email: String, password: String): UserEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    fun obtenerUsuarioPorEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun registrarUsuario(usuario: UserEntity)


    // --- GESTIÓN DE RESERVAS DE ESPACIOS ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun crearReserva(reserva: ReservationEntity)

    @Query("SELECT * FROM reservas ORDER BY id DESC")
    fun obtenerTodasLasReservas(): List<ReservationEntity>

    @Query("SELECT * FROM reservas WHERE solicitanteEmail = :email ORDER BY id DESC")
    fun obtenerReservasPorEmail(email: String): List<ReservationEntity>

    @Update
    fun actualizarReserva(reserva: ReservationEntity)
}