package com.example.mpazreserva.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    emailActual: String,
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var recibirAlertas by remember { mutableStateOf(true) }

    val perfilCompleto = nombre.isNotBlank() && telefono.isNotBlank()

    // 1. ANIMACIÓN DE COLOR DE BADGE Y BOTÓN (animateColorAsState)
    val colorBadge by animateColorAsState(
        targetValue = if (perfilCompleto) Color(0xFF2E7D32) else Color(0xFFED6C02),
        animationSpec = tween(durationMillis = 500),
        label = "ColorInsignia"
    )

    val colorBotonGuardar by animateColorAsState(
        targetValue = if (perfilCompleto) MaterialTheme.colorScheme.primary else Color(0xFFB0BEC5),
        animationSpec = tween(durationMillis = 500),
        label = "ColorBoton"
    )

    val colorFondoAlerta by animateColorAsState(
        targetValue = if (recibirAlertas) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(durationMillis = 500),
        label = "ColorAlerta"
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = { Text("Editar Perfil Institucional") },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tarjeta de perfil con estado animado
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (nombre.isNotBlank()) nombre else "Usuario Institucional",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = emailActual,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Badge interactivo que cambia de color y texto dinámicamente
                    Surface(
                        color = colorBadge.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (perfilCompleto) Icons.Default.CheckCircle else Icons.Default.Shield,
                                contentDescription = null,
                                tint = colorBadge,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (perfilCompleto) "Perfil Completo y Verificado" else "Datos Incompletos",
                                color = colorBadge,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Campos del formulario
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo *") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono de Contacto *") },
                modifier = Modifier.fillMaxWidth()
            )

            // Tarjeta con switch y animación de visibilidad + color
            Card(
                colors = CardDefaults.cardColors(containerColor = colorFondoAlerta),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Avisos de Reservas",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Switch(
                            checked = recibirAlertas,
                            onCheckedChange = { recibirAlertas = it }
                        )
                    }

                    // Texto con animación de desvanecimiento
                    AnimatedVisibility(
                        visible = recibirAlertas,
                        enter = fadeIn(animationSpec = tween(400)),
                        exit = fadeOut(animationSpec = tween(400)) + shrinkVertically()
                    ) {
                        Text(
                            text = "🔔 Recibirás avisos en tiempo real sobre la aprobación o rechazo de solicitudes por Inspectoría.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            // Banner verde con desvanecimiento animado
            AnimatedVisibility(
                visible = perfilCompleto,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ Requisitos institucionales cumplidos.",
                        color = Color(0xFF1B5E20),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón con color animado
            Button(
                onClick = {
                    if (perfilCompleto) {
                        Toast.makeText(context, "Perfil guardado correctamente", Toast.LENGTH_SHORT).show()
                        onGuardar()
                    } else {
                        Toast.makeText(context, "Ingresa tu nombre y teléfono", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorBotonGuardar),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (perfilCompleto) "Guardar Cambios" else "Completa los campos para Guardar")
            }
        }
    }
}