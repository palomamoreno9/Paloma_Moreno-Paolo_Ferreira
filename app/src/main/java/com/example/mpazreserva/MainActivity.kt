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
import com.example.mpazreserva.ui.EditarPerfilScreen
import com.example.mpazreserva.ui.InspectoriaScreen
import com.example.mpazreserva.ui.LoginScreen
import com.example.mpazreserva.ui.MisReservasScreen
import com.example.mpazreserva.ui.RegisterScreen
import com.example.mpazreserva.ui.SolicitudReservaScreen

// MODELO DE DATOS INCLUIDO EN EL MISMO ARCHIVO
data class Reserva(
    val id: Int,
    val solicitante: String,
    val espacio: String,
    val fecha: String,
    val horario: String,
    val actividad: String,
    val documentoUrl: String = "",
    var estado: String = "Pendiente",
    var motivoRechazo: String = ""
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                var pantallaActual by remember { mutableStateOf("BIENVENIDA") }
                var usuarioLogueado by remember { mutableStateOf<Pair<String, String>?>(null) }

                // LISTA DE RESERVAS COMPARTIDA EN MEMORIA
                val listaReservas = remember {
                    mutableStateListOf(
                        Reserva(1, "profe.juan@mpaz.cl", "Lab. de Computación", "15/11/2026", "10:00 - 11:30", "Clase de Excel", "doc1.pdf"),
                        Reserva(2, "utp@mpaz.cl", "Biblioteca", "16/11/2026", "08:00 - 09:30", "Lectura Comprensiva", "doc2.pdf")
                    )
                }

                when (pantallaActual) {
                    "BIENVENIDA" -> PantallaBienvenidaInicial(onIrALogin = { pantallaActual = "LOGIN" })
                    "LOGIN" -> LoginScreen(
                        onLoginSuccess = { email, rol ->
                            usuarioLogueado = Pair(email, rol)
                            pantallaActual = "DASHBOARD"
                            Toast.makeText(this, "Sesión iniciada como $rol", Toast.LENGTH_SHORT).show()
                        },
                        onIrARegistro = { pantallaActual = "REGISTRO" }
                    )
                    "REGISTRO" -> RegisterScreen(
                        onRegistroExitoso = { email, rol ->
                            usuarioLogueado = Pair(email, rol)
                            pantallaActual = "DASHBOARD"
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        },
                        onVolverALogin = { pantallaActual = "LOGIN" }
                    )
                    "DASHBOARD" -> VistaBienvenidaUsuario(
                        email = usuarioLogueado?.first ?: "",
                        rol = usuarioLogueado?.second ?: "Solicitante",
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
                        emailSolicitante = usuarioLogueado?.first ?: "",
                        onAgregarReserva = { nueva -> listaReservas.add(nueva) },
                        onReservaCreada = { pantallaActual = "DASHBOARD" },
                        onVolver = { pantallaActual = "DASHBOARD" }
                    )
                    "PANEL_INSPECTORIA" -> InspectoriaScreen(
                        listaReservas = listaReservas,
                        onAprobarReserva = { id ->
                            val index = listaReservas.indexOfFirst { it.id == id }
                            if (index != -1) listaReservas[index] = listaReservas[index].copy(estado = "Aprobada")
                        },
                        onRechazarReserva = { id, motivo ->
                            val index = listaReservas.indexOfFirst { it.id == id }
                            if (index != -1) listaReservas[index] = listaReservas[index].copy(estado = "Rechazada", motivoRechazo = motivo)
                        },
                        onVolver = { pantallaActual = "DASHBOARD" }
                    )
                    "EDITAR_PERFIL" -> EditarPerfilScreen(
                        emailActual = usuarioLogueado?.first ?: "",
                        onGuardar = { pantallaActual = "DASHBOARD" },
                        onVolver = { pantallaActual = "DASHBOARD" }
                    )
                    "MIS_RESERVAS" -> MisReservasScreen(
                        emailActual = usuarioLogueado?.first ?: "",
                        listaReservas = listaReservas,
                        onVolver = { pantallaActual = "DASHBOARD" }
                    )
                }
            }
        }
    }
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