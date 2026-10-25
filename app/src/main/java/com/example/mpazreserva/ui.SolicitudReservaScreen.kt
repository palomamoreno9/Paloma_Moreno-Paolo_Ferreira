package com.example.mpazreserva.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudReservaScreen(
    emailSolicitante: String,
    onReservaCreada: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var espacioSeleccionado by remember { mutableStateOf("") }
    var fechaReserva by remember { mutableStateOf("") }

    // Separamos el horario en Inicio y Término
    var horaInicio by remember { mutableStateOf("") }
    var horaTermino by remember { mutableStateOf("") }

    var actividad by remember { mutableStateOf("") }
    var asistentes by remember { mutableStateOf("") }
    var expandedEspacio by remember { mutableStateOf(false) }

    var intentoEnvio by remember { mutableStateOf(false) }
    var mensajeGeneralError by remember { mutableStateOf("") }

    val espacios = listOf("Laboratorio de Computación", "Laboratorio de Ciencias", "Biblioteca", "Sala Sensorial", "CIEMP", "Salas de Reuniones", "Huerto Escolar")

    // Validaciones en tiempo real
    val espacioValido = espacioSeleccionado.isNotBlank()
    val fechaValida = fechaReserva.isNotBlank()
    val horaInicioValida = horaInicio.isNotBlank()
    val horaTerminoValida = horaTermino.isNotBlank()
    val actividadValida = actividad.isNotBlank()
    val asistentesValido = asistentes.isNotBlank()

    // Configuración del DatePicker Nativo
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            fechaReserva = "$dayOfMonth/${month + 1}/$year"
            mensajeGeneralError = ""
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
    datePickerDialog.datePicker.minDate = calendar.timeInMillis

    // Configuración del TimePicker Nativo para Inicio
    val timePickerInicio = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            horaInicio = String.format("%02d:%02d", hourOfDay, minute)
            mensajeGeneralError = ""
        },
        8, 0, true // 8:00 AM por defecto, formato 24hrs
    )

    // Configuración del TimePicker Nativo para Término
    val timePickerTermino = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            horaTermino = String.format("%02d:%02d", hourOfDay, minute)
            mensajeGeneralError = ""
        },
        9, 0, true // 9:00 AM por defecto, formato 24hrs
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Formulario de Solicitud",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Selector de Espacio
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
                    isError = intentoEnvio && !espacioValido,
                    supportingText = { if (intentoEnvio && !espacioValido) Text("Debe seleccionar un espacio", color = MaterialTheme.colorScheme.error) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
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
                                mensajeGeneralError = ""
                            }
                        )
                    }
                }
            }

            // Selector de Fecha
            OutlinedTextField(
                value = fechaReserva,
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha (Min. 1 día de anticipación) *") },
                trailingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                },
                isError = intentoEnvio && !fechaValida,
                supportingText = { if (intentoEnvio && !fechaValida) Text("La fecha es obligatoria", color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )

            // Fila con Hora Inicio y Hora Término
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Hora Inicio
                OutlinedTextField(
                    value = horaInicio,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora Inicio *") },
                    trailingIcon = {
                        IconButton(onClick = { timePickerInicio.show() }) {
                            Icon(Icons.Default.Schedule, contentDescription = "Hora Inicio")
                        }
                    },
                    isError = intentoEnvio && !horaInicioValida,
                    supportingText = { if (intentoEnvio && !horaInicioValida) Text("Requerido", color = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.weight(1f)
                )

                // Hora Término
                OutlinedTextField(
                    value = horaTermino,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Hora Término *") },
                    trailingIcon = {
                        IconButton(onClick = { timePickerTermino.show() }) {
                            Icon(Icons.Default.Schedule, contentDescription = "Hora Término")
                        }
                    },
                    isError = intentoEnvio && !horaTerminoValida,
                    supportingText = { if (intentoEnvio && !horaTerminoValida) Text("Requerido", color = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Actividad
            OutlinedTextField(
                value = actividad,
                onValueChange = { actividad = it; mensajeGeneralError = "" },
                label = { Text("Actividad a realizar *") },
                isError = intentoEnvio && !actividadValida,
                supportingText = { if (intentoEnvio && !actividadValida) Text("Describa la actividad", color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )

            // Cantidad de asistentes
            OutlinedTextField(
                value = asistentes,
                onValueChange = { asistentes = it; mensajeGeneralError = "" },
                label = { Text("Cantidad de asistentes (Aprox) *") },
                isError = intentoEnvio && !asistentesValido,
                supportingText = { if (intentoEnvio && !asistentesValido) Text("Indique el número de participantes", color = MaterialTheme.colorScheme.error) },
                modifier = Modifier.fillMaxWidth()
            )

            AnimatedVisibility(
                visible = mensajeGeneralError.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = mensajeGeneralError,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    intentoEnvio = true
                    if (espacioValido && fechaValida && horaInicioValida && horaTerminoValida && actividadValida && asistentesValido) {
                        Toast.makeText(context, "Solicitud enviada a Inspectoría", Toast.LENGTH_LONG).show()
                        onReservaCreada()
                    } else {
                        mensajeGeneralError = "No se puede reservar. Completa los campos en rojo."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Solicitud")
            }
        }
    }
}