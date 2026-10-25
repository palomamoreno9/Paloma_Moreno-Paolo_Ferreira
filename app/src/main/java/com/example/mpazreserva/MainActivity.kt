package com.example.mpazreserva

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
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mpazreserva.ui.InspectoriaScreen
import com.example.mpazreserva.ui.LoginScreen
import com.example.mpazreserva.ui.RegisterScreen
import com.example.mpazreserva.ui.SolicitudReservaScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                // ESTADOS DE PANTALLA AHORA INCLUYE INSPECTORIA
                var pantallaActual by remember { mutableStateOf("BIENVENIDA") }
                var usuarioLogueado by remember { mutableStateOf<Pair<String, String>?>(null) }

                when (pantallaActual) {
                    "BIENVENIDA" -> {
                        PantallaBienvenidaInicial(onIrALogin = { pantallaActual = "LOGIN" })
                    }
                    "LOGIN" -> {
                        LoginScreen(
                            onLoginSuccess = { email, rol ->
                                usuarioLogueado = Pair(email, rol)
                                pantallaActual = "DASHBOARD"
                                Toast.makeText(this, "Sesión iniciada como $rol", Toast.LENGTH_SHORT).show()
                            },
                            onIrARegistro = { pantallaActual = "REGISTRO" }
                        )
                    }
                    "REGISTRO" -> {
                        RegisterScreen(
                            onRegistroExitoso = { email, rol ->
                                usuarioLogueado = Pair(email, rol)
                                pantallaActual = "DASHBOARD"
                                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            },
                            onVolverALogin = { pantallaActual = "LOGIN" }
                        )
                    }
                    "DASHBOARD" -> {
                        VistaBienvenidaUsuario(
                            email = usuarioLogueado?.first ?: "",
                            rol = usuarioLogueado?.second ?: "Solicitante",
                            onCerrarSesion = {
                                usuarioLogueado = null
                                pantallaActual = "BIENVENIDA"
                            },
                            onNuevaReserva = { pantallaActual = "NUEVA_RESERVA" },
                            onVerSolicitudes = { pantallaActual = "PANEL_INSPECTORIA" } // Nuevo enlace
                        )
                    }
                    "NUEVA_RESERVA" -> {
                        SolicitudReservaScreen(
                            emailSolicitante = usuarioLogueado?.first ?: "",
                            onReservaCreada = { pantallaActual = "DASHBOARD" },
                            onVolver = { pantallaActual = "DASHBOARD" }
                        )
                    }
                    "PANEL_INSPECTORIA" -> {
                        InspectoriaScreen(
                            onVolver = { pantallaActual = "DASHBOARD" }
                        )
                    }
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
                        Text("✓ Sistema activo y base de datos Room sincronizada.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 16.dp))
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
    onVerSolicitudes: () -> Unit // Nuevo parámetro
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel de $rol") },
                actions = { IconButton(onClick = onCerrarSesion) { Icon(Icons.Default.ExitToApp, "Cerrar sesión") } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            if (rol == "Solicitante") {
                FloatingActionButton(onClick = onNuevaReserva, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) {
                    Icon(Icons.Default.Add, "Nueva Reserva")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp), contentAlignment = Alignment.Center) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("¡Hola, $email!", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Rol activo: $rol", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))

                    if (rol == "Solicitante") {
                        Text("Usa el botón '+' para solicitar un espacio.", textAlign = TextAlign.Center)
                    } else {
                        // SI ES INSPECTORÍA, MUESTRA ESTE BOTÓN
                        Text("Administra las solicitudes de reservas aquí:", textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 12.dp))
                        Button(onClick = onVerSolicitudes, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.FactCheck, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Ver Solicitudes Pendientes")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.fillMaxWidth()) { Text("Cerrar Sesión") }
                }
            }
        }
    }
}