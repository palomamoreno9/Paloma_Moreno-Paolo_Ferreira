package com.example.mpazreserva.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mpazreserva.Reserva

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectoriaScreen(
    listaReservas: List<Reserva>,
    onAprobarReserva: (Int) -> Unit,
    onRechazarReserva: (Int, String) -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var pestañaActual by remember { mutableStateOf(0) }

    var reservaARechazar by remember { mutableStateOf<Reserva?>(null) }
    var motivoRechazo by remember { mutableStateOf("") }

    val reservasPendientes = listaReservas.filter { it.estado == "Pendiente" }
    val reservasHistorial = listaReservas.filter { it.estado != "Pendiente" }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = { IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, "Volver") } },
                title = { Text("Panel de Inspectoría") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = pestañaActual) {
                Tab(selected = pestañaActual == 0, onClick = { pestañaActual = 0 }, text = { Text("Pendientes (${reservasPendientes.size})") }, icon = { Icon(Icons.Default.Info, null) })
                Tab(selected = pestañaActual == 1, onClick = { pestañaActual = 1 }, text = { Text("Historial (${reservasHistorial.size})") }, icon = { Icon(Icons.Default.Check, null) })
            }

            Crossfade(targetState = pestañaActual, animationSpec = tween(500), label = "AnimacionTabs") { tabSeleccionada ->
                Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    if (tabSeleccionada == 0) {
                        if (reservasPendientes.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay solicitudes pendientes.") }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(reservasPendientes) { reserva ->
                                    ReservaCard(
                                        reserva = reserva, esHistorial = false,
                                        onAprobar = {
                                            onAprobarReserva(reserva.id)
                                            Toast.makeText(context, "Reserva Aprobada", Toast.LENGTH_SHORT).show()
                                        },
                                        onRechazar = { reservaARechazar = reserva },
                                        onVerDocumento = { Toast.makeText(context, "Abriendo archivo adjunto...", Toast.LENGTH_SHORT).show() }
                                    )
                                }
                            }
                        }
                    } else {
                        if (reservasHistorial.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("El historial está vacío.") }
                        } else {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(reservasHistorial) { reserva ->
                                    ReservaCard(reserva = reserva, esHistorial = true, onAprobar = {}, onRechazar = {}, onVerDocumento = {})
                                }
                            }
                        }
                    }
                }
            }
        }

        if (reservaARechazar != null) {
            AlertDialog(
                onDismissRequest = { reservaARechazar = null; motivoRechazo = "" },
                title = { Text("Rechazar Solicitud") },
                text = {
                    Column {
                        Text("Indique el motivo del rechazo:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = motivoRechazo, onValueChange = { motivoRechazo = it }, label = { Text("Motivo *") }, modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            if (motivoRechazo.isNotBlank()) {
                                onRechazarReserva(reservaARechazar!!.id, motivoRechazo)
                                Toast.makeText(context, "Reserva Rechazada", Toast.LENGTH_SHORT).show()
                                reservaARechazar = null
                                motivoRechazo = ""
                            } else { Toast.makeText(context, "Debe ingresar un motivo", Toast.LENGTH_SHORT).show() }
                        }
                    ) { Text("Confirmar") }
                },
                dismissButton = { TextButton(onClick = { reservaARechazar = null; motivoRechazo = "" }) { Text("Cancelar") } }
            )
        }
    }
}

@Composable
fun ReservaCard(reserva: Reserva, esHistorial: Boolean, onAprobar: () -> Unit, onRechazar: () -> Unit, onVerDocumento: (String) -> Unit) {
    var expandido by remember { mutableStateOf(false) }
    val colorEstado by animateColorAsState(targetValue = when (reserva.estado) { "Aprobada" -> Color(0xFF4CAF50); "Rechazada" -> Color(0xFFF44336); else -> MaterialTheme.colorScheme.primary }, label = "")

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
                if (esHistorial) {
                    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(colorEstado.copy(alpha = 0.2f)).padding(horizontal = 8.dp, vertical = 4.dp)) { Text(text = reserva.estado, color = colorEstado, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold) }
                }
            }
            if (expandido) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Solicitante: ${reserva.solicitante}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Actividad: ${reserva.actividad}", style = MaterialTheme.typography.bodyMedium)
                if (esHistorial && reserva.estado == "Rechazada") {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Motivo de rechazo:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    Text(text = reserva.motivoRechazo, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                }
                if (!esHistorial) {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { onVerDocumento(reserva.documentoUrl) }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Info, null); Spacer(modifier = Modifier.width(8.dp)); Text("Ver Planificación Adjunta") }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = onRechazar, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error), modifier = Modifier.weight(1f)) { Text("Rechazar") }
                        Button(onClick = onAprobar, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary), modifier = Modifier.weight(1f)) { Text("Aprobar") }
                    }
                }
            }
        }
    }
}