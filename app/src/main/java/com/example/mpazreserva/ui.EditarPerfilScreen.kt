package com.example.mpazreserva.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    emailActual: String,
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var nuevoNombre by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }

    val hayCambios = nuevoNombre.isNotBlank() || nuevaPassword.isNotBlank()

    // AQUÍ DEFINIMOS TUS COLORES PERSONALIZADOS
    val colorRosado = Color(0xFFF48FB1) // Un rosado pastel muy lindo
    val colorCeleste = Color(0xFF4FC3F7) // El celeste claro que usó tu profe

    // ANIMACIÓN 1: Cambio de Color
    val colorBoton by animateColorAsState(
        // Si quieres celeste, cambia "colorRosado" por "colorCeleste"
        targetValue = if (hayCambios) colorRosado else Color.LightGray,
        animationSpec = tween(durationMillis = 600),
        label = "AnimacionColorBoton"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onVolver) { Icon(Icons.Default.ArrowBack, "Volver") }
                },
                title = { Text("Editar Perfil") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Actualizar Datos",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text("Cuenta activa: $emailActual", style = MaterialTheme.typography.bodyMedium)

            OutlinedTextField(
                value = nuevoNombre,
                onValueChange = { nuevoNombre = it },
                label = { Text("Nuevo Nombre") },
                leadingIcon = { Icon(Icons.Default.Person, null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = nuevaPassword,
                onValueChange = { nuevaPassword = it },
                label = { Text("Nueva Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            // ANIMACIÓN 2: Mostrar/Ocultar
            AnimatedVisibility(
                visible = hayCambios,
                enter = fadeIn(animationSpec = tween(600)),
                exit = fadeOut(animationSpec = tween(600))
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "📝 Tienes cambios pendientes por guardar.",
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (hayCambios) {
                        Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                        onGuardar()
                    } else {
                        Toast.makeText(context, "Ingresa algún dato para actualizar", Toast.LENGTH_SHORT).show()
                    }
                },
                // Aplicamos el color animado
                colors = ButtonDefaults.buttonColors(containerColor = colorBoton),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cambios", color = Color.White) // Texto en blanco para que resalte
            }
        }
    }
}