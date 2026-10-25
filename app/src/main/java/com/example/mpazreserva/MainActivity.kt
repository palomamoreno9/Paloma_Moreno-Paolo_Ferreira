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
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.mpazreserva.ui.LoginScreen
import com.example.mpazreserva.ui.RegisterScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                var pantallaActual by remember { mutableStateOf("BIENVENIDA") }
                var usuarioLogueado by remember { mutableStateOf<Pair<String, String>?>(null) }

                when (pantallaActual) {
                    "BIENVENIDA" -> {
                        PantallaBienvenidaInicial(
                            onIrALogin = { pantallaActual = "LOGIN" }
                        )
                    }
                    "LOGIN" -> {
                        LoginScreen(
                            onLoginSuccess = { email, rol ->
                                usuarioLogueado = Pair(email, rol)
                                pantallaActual = "DASHBOARD"
                                Toast.makeText(
                                    this,
                                    "Sesión iniciada como $rol ($email)",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onIrARegistro = { pantallaActual = "REGISTRO" }
                        )
                    }
                    "REGISTRO" -> {
                        RegisterScreen(
                            onRegistroExitoso = { email, rol ->
                                usuarioLogueado = Pair(email, rol)
                                pantallaActual = "DASHBOARD"
                                Toast.makeText(
                                    this,
                                    "Registro exitoso como $rol",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                                Toast.makeText(this, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()
                            }
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
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Escuela Escritora Marcela Paz",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = "Ícono de Reserva",
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¡Bienvenido a tu Reserva!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Sistema digital para la solicitud y gestión de espacios compartidos",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onIrALogin,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ir a Iniciar Sesión")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { mostrarEstado = !mostrarEstado },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (mostrarEstado) "Ocultar Estado" else "Ver Estado del Sistema")
                    }

                    AnimatedVisibility(
                        visible = mostrarEstado,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "✓ Sistema activo y base de datos Room sincronizada.",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VistaBienvenidaUsuario(email: String, rol: String, onCerrarSesion: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Panel de $rol") },
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Hola, $email!",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Rol activo: $rol", style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = onCerrarSesion,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Cerrar Sesión")
                    }
                }
            }
        }
    }
}