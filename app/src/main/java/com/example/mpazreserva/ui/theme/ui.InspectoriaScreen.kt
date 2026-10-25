package com.example.mpazreserva.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Agregamos "documentoUrl" para simular el archivo adjunto
data class ReservaPendiente(
    val id: Int,
    val solicitante: String,
    val espacio: String,
    val fecha: String,
    val horario: String,
    val actividad: String,
    val documentoUrl: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspectoriaScreen(
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    // Lista de prueba con enlaces a un PDF real de demostración
    var listaReservas by remember {
        mutableStateOf(
            listOf(
                ReservaPendiente(
                    1, "profe.juan@mpaz.cl", "Lab. de Computación", "15/11/2026", "10:00 - 11:30", "Clase de Excel",
                    "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
                ),
                ReservaPendiente(
                    2, "utp@mpaz.cl", "Biblioteca", "16/11/2026", "08:00 - 09:30", "Lectura Comprensiva",
                    "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf"
                )
            )
        )
    }

    var reservaARechazar by remember { mutableStateOf<ReservaPendiente?>(null) }
    var motivoRechazo by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = { Text("Panel de Inspectoría") },
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
                text = "Solicitudes Pendientes",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (listaReservas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay solicitudes pendientes. ¡Todo al día!")
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaReservas) { reserva ->
                        ReservaCard(
                            reserva = reserva,
                            onAprobar = {
                                listaReservas = listaReservas.filter { it.id != reserva.id }
                                Toast.makeText(context, "Reserva Aprobada", Toast.LENGTH_SHORT).show()
                            },
                            onRechazar = { reservaARechazar = reserva },
                            onVerDocumento = { url ->
                                // AQUÍ USAMOS EL RECURSO NATIVO PARA ABRIR EL ARCHIVO
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error al abrir el documento", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }

        // Animación de Diálogo (Alerta) para justificar el rechazo
        if (reservaARechazar != null) {
            AlertDialog(
                onDismissRequest = { reservaARechazar = null; motivoRechazo = "" },
                title = { Text("Rechazar Solicitud") },
                text = {
                    Column {
                        Text("Indique el motivo del rechazo para notificar al solicitante:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = motivoRechazo,
                            onValueChange = { motivoRechazo = it },
                            label = { Text("Motivo de rechazo *") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        onClick = {
                            if (motivoRechazo.isNotBlank()) {
                                listaReservas = listaReservas.filter { it.id != reservaARechazar!!.id }
                                Toast.makeText(context, "Reserva Rechazada", Toast.LENGTH_SHORT).show()
                                reservaARechazar = null
                                motivoRechazo = ""
                            } else {
                                Toast.makeText(context, "Debe ingresar un motivo", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { reservaARechazar = null; motivoRechazo = "" }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun ReservaCard(
    reserva: ReservaPendiente,
    onAprobar: () -> Unit,
    onRechazar: () -> Unit,
    onVerDocumento: (String) -> Unit // Recibe la URL
) {
    var expandido by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expandido = !expandido },
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = reserva.espacio, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(text = "${reserva.fecha} | ${reserva.horario}", style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (!expandido) {
                Text(
                    text = "Toca para ver detalles...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            if (expandido) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Solicitante: ${reserva.solicitante}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Actividad: ${reserva.actividad}", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(12.dp))

                // Botón Ver Documento
                OutlinedButton(
                    onClick = { onVerDocumento(reserva.documentoUrl) }, // Envía la URL al clickear
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Description, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Planificación Adjunta")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Botones de Acción
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRechazar,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rechazar")
                    }

                    Button(
                        onClick = onAprobar,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Aprobar")
                    }
                }
            }
        }
    }
}