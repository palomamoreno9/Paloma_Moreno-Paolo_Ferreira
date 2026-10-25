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
import com.example.mpazreserva.Reserva

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(
    emailActual: String,
    listaReservas: List<Reserva>,
    onVolver: () -> Unit
) {
    val misReservas = listaReservas.filter { it.solicitante == emailActual || emailActual.isEmpty() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, "Volver") } },
                title = { Text("Mis Solicitudes") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Text(text = "Historial de reservas de $emailActual", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(bottom = 16.dp))
            if (misReservas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes solicitudes registradas.")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(misReservas) { reserva -> MiReservaCard(reserva) }
                }
            }
        }
    }
}

@Composable
fun MiReservaCard(reserva: Reserva) {
    var expandido by remember { mutableStateOf(false) }
    val colorEstado = when (reserva.estado) { "Aprobada" -> Color(0xFF4CAF50); "Rechazada" -> Color(0xFFF44336); else -> Color(0xFFFFA000) }

    ElevatedCard(modifier = Modifier.fillMaxWidth().animateContentSize().clickable { expandido = !expandido }, elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Event, null, tint = colorEstado)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = reserva.espacio, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = "${reserva.fecha} | ${reserva.horario}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(colorEstado.copy(alpha = 0.2f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(text = reserva.estado, color = colorEstado, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
            if (expandido && reserva.estado == "Rechazada") {
                Spacer(modifier = Modifier.height(12.dp)); HorizontalDivider(); Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Motivo del rechazo:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                Text(text = reserva.motivoRechazo, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}