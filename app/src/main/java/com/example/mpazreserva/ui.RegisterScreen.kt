package com.example.mpazreserva.ui

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegistroExitoso: (String, String) -> Unit, // (email, rol)
    onVolverALogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Estados para rastrear si el usuario ya intentó enviar el formulario
    var intentoEnvio by remember { mutableStateOf(false) }
    var mensajeGeneralError by remember { mutableStateOf("") }

    // Reglas de validación en tiempo real
    val nombreValido = nombre.trim().length >= 3
    val emailValido = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val passwordValida = password.length >= 6
    val confirmacionValida = confirmPassword == password && confirmPassword.isNotEmpty()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Escuela Escritora Marcela Paz") },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Crear Cuenta de Solicitante",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo Nombre Completo
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = {
                            nombre = it
                            mensajeGeneralError = ""
                        },
                        label = { Text("Nombre Completo *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        isError = intentoEnvio && !nombreValido,
                        supportingText = {
                            if (intentoEnvio && !nombreValido) {
                                Text("El nombre debe tener al menos 3 caracteres", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo Correo Institucional
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            mensajeGeneralError = ""
                        },
                        label = { Text("Correo Institucional *") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        isError = intentoEnvio && !emailValido,
                        supportingText = {
                            if (intentoEnvio && !emailValido) {
                                Text("Ingrese un correo válido (ejemplo@dominio.com)", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            mensajeGeneralError = ""
                        },
                        label = { Text("Contraseña *") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = intentoEnvio && !passwordValida,
                        supportingText = {
                            if (intentoEnvio && !passwordValida) {
                                Text("La contraseña debe tener mínimo 6 caracteres", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo Confirmar Contraseña
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            mensajeGeneralError = ""
                        },
                        label = { Text("Confirmar Contraseña *") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = intentoEnvio && !confirmacionValida,
                        supportingText = {
                            if (intentoEnvio && !confirmacionValida) {
                                Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Alerta General de Error
                    AnimatedVisibility(
                        visible = mensajeGeneralError.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
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

                    Spacer(modifier = Modifier.height(20.dp))

                    // Botón de Envío (Rol forzado a "Solicitante")
                    Button(
                        onClick = {
                            intentoEnvio = true
                            if (nombreValido && emailValido && passwordValida && confirmacionValida) {
                                // Aquí forzamos que todos los registros sean "Solicitante"
                                onRegistroExitoso(email, "Solicitante")
                            } else {
                                mensajeGeneralError = "Por favor corrige los campos remarcados en rojo."
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrarse")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onVolverALogin) {
                        Text("¿Ya tienes cuenta? Inicia sesión")
                    }
                }
            }
        }
    }
}