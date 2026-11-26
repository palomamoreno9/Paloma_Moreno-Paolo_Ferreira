package com.example.mpazreserva

import com.example.mpazreserva.data.ReservationEntity
import com.example.mpazreserva.data.UserEntity
import com.example.mpazreserva.network.FeriadoDto
import com.example.mpazreserva.network.ReservaDto
import com.example.mpazreserva.network.UsuarioDto
import com.example.mpazreserva.network.toDto
import com.example.mpazreserva.network.toEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservaRepositoryTest {

    // Revisa si dos horas chocan
    @Test
    fun mismoEspacioYHorarioCruzado_detectaChoque() {
        val choque = haySolapamientoHorario("08:30 - 10:00", "09:30 - 11:00")
        assertTrue(choque)
    }

    // Revisa que dos horas seguidas no choquen
    @Test
    fun horarioConsecutivoEnMismoEspacio_noDetectaChoque() {
        val choque = haySolapamientoHorario("08:30 - 10:00", "10:00 - 11:30")
        assertFalse(choque)
    }

    // Revisa que no falle si la hora viene mal escrita
    @Test
    fun horarioConFormatoInvalido_retornaFalse() {
        val choque = haySolapamientoHorario("sin_hora", "10:00 - 11:30")
        assertFalse(choque)
    }

    // Pasa una reserva nueva para enviarla a Render
    @Test
    fun reservaLocalNueva_convierteIdANullParaBackend() {
        val reservaLocal = ReservationEntity(
            id = 0,
            solicitanteEmail = "docente@mpaz.cl",
            espacioNombre = "Biblioteca",
            fechaReserva = "27/11/2026",
            horario = "09:00 - 10:30",
            actividad = "Taller de lectura"
        )

        val dto = reservaLocal.toDto()

        assertNull(dto.id)
        assertEquals("Biblioteca", dto.espacioNombre)
        assertEquals("Pendiente", dto.estado)
    }

    // Mantiene el ID y motivo si la reserva fue rechazada
    @Test
    fun reservaLocalRechazada_conservaIdYMotivoAlEnviar() {
        val reservaLocal = ReservationEntity(
            id = 15,
            solicitanteEmail = "docente@mpaz.cl",
            espacioNombre = "CIEMP",
            fechaReserva = "28/11/2026",
            horario = "11:00 - 12:30",
            actividad = "Reunión",
            estado = "Rechazada",
            motivoRechazo = "Mantención eléctrica"
        )

        val dto = reservaLocal.toDto()

        assertEquals(15, dto.id)
        assertEquals("Rechazada", dto.estado)
        assertEquals("Mantención eléctrica", dto.motivoRechazo)
    }

    // Guarda en el teléfono una reserva que viene de Render
    @Test
    fun reservaDesdeRender_convierteCorrectamenteAEntidadRoom() {
        val dto = ReservaDto(
            id = 12,
            solicitanteEmail = "profe@mpaz.cl",
            espacioNombre = "Laboratorio de Ciencias",
            fechaReserva = "29/11/2026",
            horario = "14:00 - 15:30",
            actividad = "Clase de Química",
            estado = "Aprobada"
        )

        val entidad = dto.toEntity()

        assertEquals(12, entidad.id)
        assertEquals("Laboratorio de Ciencias", entidad.espacioNombre)
        assertEquals("Aprobada", entidad.estado)
    }

    // Pone ID 0 si Render manda el ID vacío
    @Test
    fun reservaDesdeRenderSinId_asignaCeroPorDefecto() {
        val dto = ReservaDto(
            id = null,
            solicitanteEmail = "profe@mpaz.cl",
            espacioNombre = "Sala Sensorial",
            fechaReserva = "30/11/2026",
            horario = "10:00 - 11:00",
            actividad = "Terapia"
        )

        val entidad = dto.toEntity()

        assertEquals(0, entidad.id)
        assertEquals("Pendiente", entidad.estado)
    }

    // Prepara un usuario nuevo para enviarlo a Render
    @Test
    fun usuarioLocalSolicitante_convierteCorrectamenteADto() {
        val usuario = UserEntity(
            nombre = "Paloma Moreno",
            email = "paloma@mpaz.cl",
            password = "clave123",
            rol = "Solicitante"
        )

        val dto = usuario.toDto()

        assertEquals("Paloma Moreno", dto.nombre)
        assertEquals("paloma@mpaz.cl", dto.email)
        assertEquals("Solicitante", dto.rol)
    }

    // Guarda en el teléfono el usuario que responde Render
    @Test
    fun usuarioInspectoriaDesdeRender_convierteCorrectamenteAEntidad() {
        val dto = UsuarioDto(
            id = 1L,
            nombre = "Inspectoría General",
            email = "inspectoria@mpaz.cl",
            password = "inspectoria",
            rol = "Inspectoria"
        )

        val entidad = dto.toEntity()

        assertEquals("Inspectoría General", entidad.nombre)
        assertEquals("inspectoria@mpaz.cl", entidad.email)
        assertEquals("Inspectoria", entidad.rol)
    }

    // Guarda bien la fecha y nombre de un feriado
    @Test
    fun feriadoChileApi_almacenaFechaYNombreCorrectamente() {
        val feriado = FeriadoDto(
            date = "2026-09-18",
            localName = "Independencia Nacional"
        )

        assertEquals("2026-09-18", feriado.date)
        assertEquals("Independencia Nacional", feriado.localName)
    }
}