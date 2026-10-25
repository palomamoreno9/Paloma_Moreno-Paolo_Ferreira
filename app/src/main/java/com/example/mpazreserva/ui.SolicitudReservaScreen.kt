package com.example.mpazreserva.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
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
    var horaInicio by remember { mutableStateOf("") }
    var horaTermino by remember { mutableStateOf("") }
    var actividad by remember { mutableStateOf("") }
    var asistentes by remember { mutableStateOf("") }
    var expandedEspacio by remember { mutableStateOf(false) }

    // Variables para el Documento
    var documentoUri by remember { mutableStateOf<Uri?>(null) }

    var intentoEnvio by remember { mutableStateOf(false) }
    var mensajeGeneralError by remember { mutableStateOf("") }

    val espacios = listOf("Laboratorio de Computación", "Laboratorio de Ciencias", "Biblioteca", "Sala Sensorial", "CIEMP")

    // Lanzador Nativo para seleccionar archivos (PDF, Word, Imágenes)
    val selectorArchivos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            documentoUri = uri
            mensajeGeneralError = ""
        }
    }

    val espacioValido = espacioSeleccionado.isNotBlank()
    val fechaValida = fechaReserva.isNotBlank()
    val horaInicioValida = horaInicio.isNotBlank()
    val horaTerminoValida = horaTermino.isNotBlank()
    val actividadValida = actividad.isNotBlank()
    val asistentesValido = asistentes.isNotBlank()
    // Hacemos que adjuntar documento sea obligatorio para cumplir la pauta
    val documentoValido = documentoUri != null

    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)

    val datePickerDialog = DatePickerDialog(context, { _, year, month, dayOfMonth ->
        fechaReserva = "$dayOfMonth/${month + 1}/$year"
        mensajeGeneralError = ""
    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    datePickerDialog.datePicker.minDate = calendar.timeInMillis

    val timePickerInicio = TimePickerDialog(context, { _, h, m -> horaInicio = String.format("%02d:%02d", h, m); mensajeGeneralError = "" }, 8, 0, true)
    val timePickerTermino = TimePickerDialog(context, { _, h, m -> horaTermino = String.format("%02d:%02d", h, m); mensajeGeneralError = "" }, 9, 0, true)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, "Volver") }
                },
                title = { Text("Nueva Reserva") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .animateContentSize(), // Animación suave al expandir/contraer elementos
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Formulario de Solicitud", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

            ExposedDropdownMenuBox(expanded = expandedEspacio, onExpandedChange = { expandedEspacio = !expandedEspacio }) {
                OutlinedTextField(
                    value = espacioSeleccionado, onValueChange = {}, readOnly = true, label = { Text("Espacio a reservar *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEspacio) },
                    isError = intentoEnvio && !espacioValido, modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expandedEspacio, onDismissRequest = { expandedEspacio = false }) {
                    espacios.forEach { espacio ->
                        DropdownMenuItem(text = { Text(espacio) }, onClick = { espacioSeleccionado = espacio; expandedEspacio = false })
                    }
                }
            }

            OutlinedTextField(
                value = fechaReserva, onValueChange = {}, readOnly = true, label = { Text("Fecha (Min. 1 día hábil) *") },
                trailingIcon = { IconButton(onClick = { datePickerDialog.show() }) { Icon(Icons.Default.DateRange, "Fecha") } },
                isError = intentoEnvio && !fechaValida, modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = horaInicio, onValueChange = {}, readOnly = true, label = { Text("Inicio *") },
                    trailingIcon = { IconButton(onClick = { timePickerInicio.show() }) { Icon(Icons.Default.Schedule, "Inicio") } },
                    isError = intentoEnvio && !horaInicioValida, modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = horaTermino, onValueChange = {}, readOnly = true, label = { Text("Término *") },
                    trailingIcon = { IconButton(onClick = { timePickerTermino.show() }) { Icon(Icons.Default.Schedule, "Término") } },
                    isError = intentoEnvio && !horaTerminoValida, modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(value = actividad, onValueChange = { actividad = it; mensajeGeneralError = "" }, label = { Text("Actividad a realizar *") }, isError = intentoEnvio && !actividadValida, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = asistentes, onValueChange = { asistentes = it; mensajeGeneralError = "" }, label = { Text("Cantidad de asistentes *") }, isError = intentoEnvio && !asistentesValido, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))

            // SECCIÓN: CARGAR DOCUMENTOS Y ELIMINAR
            Text("Evidencia / Planificación", style = MaterialTheme.typography.titleMedium)

            if (documentoUri == null) {
                OutlinedButton(
                    onClick = {
                        // Lanza el selector de archivos pidiendo PDFs, Docs o Imágenes
                        selectorArchivos.launch(arrayOf("application/pdf", "application/msword", "image/*"))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AttachFile, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cargar Documento (Obligatorio)")
                }
                if (intentoEnvio && !documentoValido) {
                    Text("Debe adjuntar un documento", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            } else {
                // Muestra el documento cargado con opción a eliminar (Animación incluida)
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Documento cargado", style = MaterialTheme.typography.bodyMedium)
                        }
                        IconButton(onClick = { documentoUri = null }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar documento", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            AnimatedVisibility(visible = mensajeGeneralError.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Text(text = mensajeGeneralError, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    intentoEnvio = true
                    if (espacioValido && fechaValida && horaInicioValida && horaTerminoValida && actividadValida && asistentesValido && documentoValido) {
                        Toast.makeText(context, "Solicitud enviada a Inspectoría", Toast.LENGTH_LONG).show()
                        onReservaCreada()
                    } else {
                        mensajeGeneralError = "Completa los campos y adjunta un documento."
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enviar Solicitud")
            }
        }
    }
}