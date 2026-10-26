package com.example.mpazreserva

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mpazreserva.data.AppDatabase
import com.example.mpazreserva.data.ReservationEntity
import com.example.mpazreserva.data.UserEntity
import com.example.mpazreserva.ui.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                val db = remember { AppDatabase.getDatabase(context) }
                val dao = db.appDao()

                var pantallaActual by remember { mutableStateOf("BIENVENIDA") }
                var usuarioLogueado by remember { mutableStateOf<UserEntity?>(null) }
                var listaReservas by remember { mutableStateOf<List<ReservationEntity>>(emptyList()) }

                fun actualizarListaReservas() {
                    scope.launch(Dispatchers.IO) {
                        val usuario = usuarioLogueado
                        if (usuario != null) {
                            val resultado = if (usuario.rol == "Inspectoria") {
                                dao.obtenerTodasLasReservas()
                            } else {
                                dao.obtenerReservasPorEmail(usuario.email)
                            }
                            withContext(Dispatchers.Main) {
                                listaReservas = resultado
                            }
                        }
                    }
                }

                LaunchedEffect(pantallaActual) {
                    if (pantallaActual == "PANEL_INSPECTORIA" || pantallaActual == "MIS_RESERVAS" || pantallaActual == "DASHBOARD") {
                        actualizarListaReservas()
                    }
                }

                when (pantallaActual) {
                    "BIENVENIDA" -> PantallaBienvenidaInicial(
                        onIrALogin = { pantallaActual = "LOGIN" }
                    )

                    "LOGIN" -> LoginScreen(
                        onLoginSuccess = { email, password ->
                            val cleanEmail = email.trim().lowercase()
                            val cleanPassword = password.trim()

                            scope.launch(Dispatchers.IO) {
                                val usuario = dao.login(cleanEmail, cleanPassword)
                                withContext(Dispatchers.Main) {
                                    if (usuario != null) {
                                        usuarioLogueado = usuario
                                        actualizarListaReservas()
                                        pantallaActual = "DASHBOARD"
                                        Toast.makeText(context, "Sesión iniciada como ${usuario.rol}", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        onIrARegistro = { pantallaActual = "REGISTRO" }
                    )

                    "REGISTRO" -> RegisterScreen(
                        onRegistroExitoso = { nombre, email, password ->
                            val cleanEmail = email.trim().lowercase()
                            val cleanPassword = password.trim()
                            val cleanNombre = nombre.trim()

                            // Restricción: No se permite crear más cuentas de Inspectoría
                            if (cleanEmail.contains("inspectoria")) {
                                Toast.makeText(
                                    context,
                                    "No se permite crear cuentas de Inspectoría. Utiliza la cuenta oficial.",
                                    Toast.LENGTH_LONG
                                ).show()
                            } else {
                                scope.launch(Dispatchers.IO) {
                                    val usuarioExistente = dao.obtenerUsuarioPorEmail(cleanEmail)
                                    if (usuarioExistente != null) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(context, "Este correo ya está registrado", Toast.LENGTH_LONG).show()
                                        }
                                    } else {
                                        val nuevoUsuario = UserEntity(
                                            nombre = cleanNombre,
                                            email = cleanEmail,
                                            password = cleanPassword,
                                            rol = "Solicitante"
                                        )
                                        dao.registrarUsuario(nuevoUsuario)

                                        withContext(Dispatchers.Main) {
                                            usuarioLogueado = nuevoUsuario
                                            actualizarListaReservas()
                                            pantallaActual = "DASHBOARD"
                                            Toast.makeText(context, "Cuenta creada con éxito", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        },
                        onVolverALogin = { pantallaActual = "LOGIN" }
                    )

                    "DASHBOARD" -> VistaBienvenidaUsuario(
                        email = usuarioLogueado?.email ?: "",
                        rol = usuarioLogueado?.rol ?: "Solicitante",
                        onCerrarSesion = {
                            usuarioLogueado = null
                            pantallaActual = "BIENVENIDA"
                        },
                        onNuevaReserva = { pantallaActual = "NUEVA_RESERVA" },
                        onVerSolicitudes = { pantallaActual = "PANEL_INSPECTORIA" },
                        onEditarPerfil = { pantallaActual = "EDITAR_PERFIL" },
                        onVerMisReservas = { pantallaActual = "MIS_RESERVAS" }
                    )

                    "NUEVA_RESERVA" -> SolicitudReservaScreen(
                        emailSolicitante = usuarioLogueado?.email ?: "",
                        onAgregarReserva = { nuevaReserva ->
                            scope.launch(Dispatchers.IO) {
                                val todasReservas = dao.obtenerTodasLasReservas()

                                // Validación de solapamiento de horarios en el mismo espacio y fecha
                                val conflicto = todasReservas.any { r ->
                                    r.espacioNombre.equals(nuevaReserva.espacioNombre, ignoreCase = true) &&
                                            r.fechaReserva == nuevaReserva.fechaReserva &&
                                            r.estado != "Rechazada" &&
                                            haySolapamientoHorario(r.horario, nuevaReserva.horario)
                                }

                                withContext(Dispatchers.Main) {
                                    if (conflicto) {
                                        Toast.makeText(
                                            context,
                                            "⚠️ El espacio '${nuevaReserva.espacioNombre}' ya tiene una reserva activa en ese horario.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    } else {
                                        scope.launch(Dispatchers.IO) {
                                            dao.crearReserva(nuevaReserva)
                                            withContext(Dispatchers.Main) {
                                                actualizarListaReservas()
                                                pantallaActual = "DASHBOARD"
                                                Toast.makeText(context, "Solicitud enviada a Inspectoría", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            }
                        },
                        onReservaCreada = { },
                        onVolver = { pantallaActual = "DASHBOARD" }
                    )

                    "PANEL_INSPECTORIA" -> InspectoriaScreen(
                        listaReservas = listaReservas,
                        onAprobarReserva = { idReserva ->
                            scope.launch(Dispatchers.IO) {
                                val reserva = listaReservas.find { it.id == idReserva }
                                if (reserva != null) {
                                    dao.actualizarReserva(reserva.copy(estado = "Aprobada"))
                                    withContext(Dispatchers.Main) {
                                        actualizarListaReservas()
                                    }
                                }
                            }
                        },
                        onRechazarReserva = { idReserva, motivo ->
                            scope.launch(Dispatchers.IO) {
                                val reserva = listaReservas.find { it.id == idReserva }
                                if (reserva != null) {
                                    dao.actualizarReserva(reserva.copy(estado = "Rechazada", motivoRechazo = motivo))
                                    withContext(Dispatchers.Main) {
                                        actualizarListaReservas()
                                    }
                                }
                            }
                        },
                        onVolver = { pantallaActual = "DASHBOARD" }
                    )

                    "EDITAR_PERFIL" -> EditarPerfilScreen(
                        emailActual = usuarioLogueado?.email ?: "",
                        onGuardar = { pantallaActual = "DASHBOARD" },
                        onVolver = { pantallaActual = "DASHBOARD" }
                    )

                    "MIS_RESERVAS" -> MisReservasScreen(
                        emailActual = usuarioLogueado?.email ?: "",
                        listaReservas = listaReservas,
                        onVolver = { pantallaActual = "DASHBOARD" }
                    )
                }
            }
        }
    }
}

// Función auxiliar para determinar choque de rangos de horas ("08:30 - 10:00")
fun haySolapamientoHorario(horario1: String, horario2: String): Boolean {
    fun aMinutos(hStr: String): Int {
        val partes = hStr.trim().split(":")
        if (partes.size < 2) return 0
        return (partes[0].toIntOrNull() ?: 0) * 60 + (partes[1].toIntOrNull() ?: 0)
    }

    val partes1 = horario1.split("-")
    val partes2 = horario2.split("-")

    if (partes1.size < 2 || partes2.size < 2) return false

    val inicio1 = aMinutos(partes1[0])
    val fin1 = aMinutos(partes1[1])
    val inicio2 = aMinutos(partes2[0])
    val fin2 = aMinutos(partes2[1])

    return max(inicio1, inicio2) < min(fin1, fin2)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaBienvenidaInicial(onIrALogin: () -> Unit) {
    var mostrarEstado by remember { mutableStateOf(false) }
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Escuela Escritora Marcela Paz") }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(20.dp), contentAlignment = Alignment.Center) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Default.Event, contentDescription = null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("¡Bienvenido a tu Reserva!", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onIrALogin, modifier = Modifier.fillMaxWidth()) { Text("Ir a Iniciar Sesión") }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = { mostrarEstado = !mostrarEstado }, modifier = Modifier.fillMaxWidth()) { Text(if (mostrarEstado) "Ocultar Estado" else "Ver Estado del Sistema") }
                    AnimatedVisibility(visible = mostrarEstado, enter = fadeIn(), exit = fadeOut()) {
                        Text("✓ Sistema activo en modo demostración.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaBienvenidaUsuario(
    email: String,
    rol: String,
    onCerrarSesion: () -> Unit,
    onNuevaReserva: () -> Unit,
    onVerSolicitudes: () -> Unit,
    onEditarPerfil: () -> Unit,
    onVerMisReservas: () -> Unit
) {
    val context = LocalContext.current

    val nombreUsuario = if (email.contains("@")) {
        email.substringBefore("@").replaceFirstChar { it.uppercase() }
    } else if (email.isNotBlank()) {
        email.replaceFirstChar { it.uppercase() }
    } else {
        "Usuario"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel de $rol") },
                actions = {
                    if (rol == "Solicitante") {
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+56912345678"))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Phone, contentDescription = "Llamada de Emergencia")
                        }
                    }
                    IconButton(onClick = onCerrarSesion) { Icon(Icons.Default.ExitToApp, "Cerrar sesión") }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            if (rol == "Solicitante") {
                FloatingActionButton(onClick = onNuevaReserva, containerColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Default.Add, "Nueva Reserva") }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp), contentAlignment = Alignment.Center) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    val textoSaludo = if (rol == "Inspectoria") "¡Bienvenida, Inspectora!" else "¡Hola, $nombreUsuario!"
                    Text(text = textoSaludo, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Correo registrado: $email", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(24.dp))

                    if (rol == "Solicitante") {
                        Text("Revisa el estado de tus reservas aquí:", textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 12.dp))
                        Button(onClick = onVerMisReservas, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Menu, null); Spacer(Modifier.width(8.dp)); Text("Ver Mis Solicitudes") }
                    } else {
                        Text("Administra las solicitudes de reservas aquí:", textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 12.dp))
                        Button(onClick = onVerSolicitudes, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Check, null); Spacer(Modifier.width(8.dp)); Text("Ver Solicitudes Pendientes") }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = onEditarPerfil, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Person, null); Spacer(Modifier.width(8.dp)); Text("Editar Perfil") }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) { Text("Cerrar Sesión") }
                }
            }
        }
    }
}