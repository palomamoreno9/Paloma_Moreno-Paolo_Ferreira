package com.example.mpazreserva.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mpazreserva.data.ReservationEntity
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudReservaScreen(
    emailSolicitante: String,
    onAgregarReserva: (ReservationEntity) -> Unit,
    onReservaCreada: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current

    var espacioSeleccionado by remember { mutableStateOf("") }
    var expandedEspacio by remember { mutableStateOf(false) }

    var fechaReserva by remember { mutableStateOf("") }
    var horaInicio by remember { mutableStateOf("") }
    var horaTermino by remember { mutableStateOf("") }
    var actividad by remember { mutableStateOf("") }
    var documentoUri by remember { mutableStateOf<Uri?>(null) }

    var errorMensaje by remember { mutableStateOf("") }

    val espacios = listOf(
        "Laboratorio de Computación",
        "Laboratorio de Ciencias",
        "Biblioteca",
        "Sala Sensorial",
        "CIEMP"
    )

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let { documentoUri = it }
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val selCal = Calendar.getInstance()
            selCal.set(year, month, dayOfMonth)
            val dayOfWeek = selCal.get(Calendar.DAY_OF_WEEK)

            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                fechaReserva = ""
                errorMensaje = "❌ Las reservas son de Lunes a Viernes únicamente."
                Toast.makeText(context, "No se permiten reservas en fin de semana", Toast.LENGTH_SHORT).show()
            } else {
                fechaReserva = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                errorMensaje = ""
            }
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = System.currentTimeMillis()

    fun aMinutos(horaStr: String): Int {
        if (!horaStr.contains(":")) return 0
        val partes = horaStr.split(":")
        return (partes[0].toIntOrNull() ?: 0) * 60 + (partes[1].toIntOrNull() ?: 0)
    }

    val timePickerInicio = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val totalMin = hourOfDay * 60 + minute
            val minPermitido = 8 * 60 + 30
            val maxPermitido = 18 * 60

            if (totalMin < minPermitido || totalMin > maxPermitido) {
                horaInicio = ""
                errorMensaje = "❌ El horario permitido es de 08:30 a 18:00 hrs."
                Toast.makeText(context, "Horario permitido: 08:30 a 18:00 hrs", Toast.LENGTH_SHORT).show()
            } else {
                horaInicio = String.format("%02d:%02d", hourOfDay, minute)
                errorMensaje = ""
            }
        },
        8, 30, true
    )

    val timePickerTermino = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val totalMin = hourOfDay * 60 + minute
            val minPermitido = 8 * 60 + 30
            val maxPermitido = 18 * 60

            if (totalMin < minPermitido || totalMin > maxPermitido) {
                horaTermino = ""
                errorMensaje = "❌ El horario permitido es de 08:30 a 18:00 hrs."
                Toast.makeText(context, "Horario permitido: 08:30 a 18:00 hrs", Toast.LENGTH_SHORT).show()
            } else if (horaInicio.isNotBlank() && totalMin <= aMinutos(horaInicio)) {
                horaTermino = ""
                errorMensaje = "❌ La hora de término debe ser posterior a la de inicio."
            } else {
                horaTermino = String.format("%02d:%02d", hourOfDay, minute)
                errorMensaje = ""
            }
        },
        9, 30, true
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = { Text("Nueva Reserva") },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Formulario de Solicitud",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            ExposedDropdownMenuBox(
                expanded = expandedEspacio,
                onExpandedChange = { expandedEspacio = !expandedEspacio }
            ) {
                OutlinedTextField(
                    value = espacioSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Espacio a reservar *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEspacio) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedEspacio,
                    onDismissRequest = { expandedEspacio = false }
                ) {
                    espacios.forEach { espacio ->
                        DropdownMenuItem(
                            text = { Text(espacio) },
                            onClick = {
                                espacioSeleccionado = espacio
                                expandedEspacio = false
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fechaReserva,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha (Lunes a Viernes) *") },
                    trailingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.DateRange, contentDescription = "Abrir calendario")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { datePickerDialog.show() }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = horaInicio,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Inicio (08:30+) *") },
                        trailingIcon = {
                            IconButton(onClick = { timePickerInicio.show() }) {
                                Icon(Icons.Default.Schedule, contentDescription = "Hora Inicio")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { timePickerInicio.show() }
                    )
                }

                Box(modifier = Modifier.weight(1f)) {
                    OutlinedTextField(
                        value = horaTermino,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Término (-18:00) *") },
                        trailingIcon = {
                            IconButton(onClick = { timePickerTermino.show() }) {
                                Icon(Icons.Default.Schedule, contentDescription = "Hora Término")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { timePickerTermino.show() }
                    )
                }
            }

            OutlinedTextField(
                value = actividad,
                onValueChange = { actividad = it; errorMensaje = "" },
                label = { Text("Actividad a realizar *") },
                modifier = Modifier.fillMaxWidth()
            )

            if (documentoUri == null) {
                OutlinedButton(
                    onClick = { launcher.launch(arrayOf("application/pdf", "image/*")) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cargar Planificación (PDF/Img)")
                }
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text("Documento cargado")
                        }
                        IconButton(onClick = { documentoUri = null }) {
                            Icon(Icons.Default.Clear, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = errorMensaje.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMensaje,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Button(
                onClick = {
                    if (espacioSeleccionado.isBlank() || fechaReserva.isBlank() || horaInicio.isBlank() || horaTermino.isBlank() || actividad.isBlank()) {
                        errorMensaje = "⚠️ Por favor completa todos los campos obligatorios."
                    } else {
                        val nuevaReserva = ReservationEntity(
                            solicitanteEmail = emailSolicitante,
                            espacioNombre = espacioSeleccionado,
                            fechaReserva = fechaReserva,
                            horario = "$horaInicio - $horaTermino",
                            actividad = actividad,
                            documentoUrl = documentoUri?.toString() ?: ""
                        )
                        onAgregarReserva(nuevaReserva)
                        Toast.makeText(context, "Reserva enviada a Inspectoría", Toast.LENGTH_SHORT).show()
                        onReservaCreada()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Solicitud")
            }
        }
    }
}