package com.example.mpazreserva.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Modelo para la vista del Solicitante
data class MiReserva(
    val id: Int,
    val espacio: String,
    val fecha: String,
    val horario: String,
    val estado: String, // "Pendiente", "Aprobada", "Rechazada"
    val motivoRechazo: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(
    emailActual: String,
    onVolver: () -> Unit
) {
    // Datos de prueba para la demostración visual
    val misReservas = listOf(
        MiReserva(1, "Lab. de Computación", "15/11/2026", "10:00 - 11:30", "Aprobada"),
        MiReserva(2, "Biblioteca", "18/11/2026", "08:00 - 09:30", "Pendiente"),
        MiReserva(3, "Sala Sensorial", "20/11/2026", "11:30 - 13:00", "Rechazada", "El espacio está en mantenimiento preventivo esta semana.")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, "Volver") }
                },
                title = { Text("Mis Solicitudes") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Historial de reservas",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(misReservas) { reserva ->
                    MiReservaCard(reserva)
                }
            }
        }
    }
}

@Composable
fun MiReservaCard(reserva: MiReserva) {
    var expandido by remember { mutableStateOf(false) }

    // Asignamos un color según el estado de la reserva
    val colorEstado = when (reserva.estado) {
        "Aprobada" -> Color(0xFF4CAF50) // Verde
        "Rechazada" -> Color(0xFFF44336) // Rojo
        else -> Color(0xFFFFA000) // Naranja para Pendiente
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expandido = !expandido },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = colorEstado)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = reserva.espacio, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = "${reserva.fecha} | ${reserva.horario}", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Etiqueta visual de estado
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(colorEstado.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = reserva.estado,
                        color = colorEstado,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Si está rechazada y se toca la tarjeta, se expande para mostrar el motivo
            if (expandido && reserva.estado == "Rechazada") {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Motivo del rechazo:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                Text(text = reserva.motivoRechazo, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            } else if (expandido) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Toca el botón '+' en tu panel para crear una nueva reserva.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}