package com.example.luminico

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch

private val CardSurface = Color(0xFF1A1528)
private val NeonPurpleAccent = Color(0xFFC084FC)
private val NeonPurpleButton = Color(0xFFA855F7)
private val TextSecondary = Color(0xFFA7A3C1)
private val StatusOnline = Color(0xFF00E676)

@Composable
fun Esp32RadarDialog(
    isConnected: Boolean,
    onDismissRequest: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val wifiStatus by Esp32WifiManager.connectionStatus.collectAsState()

    // Animación infinita para la rotación del radar
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TÍTULO E ÍCONO
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = null,
                        tint = NeonPurpleAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Radar Wi-Fi Lumínico",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Buscando red del ESP32 en el entorno...",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // CANVAS CON ANIMACIÓN DE RADAR BARRIDO
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = center
                        val radius = size.minDimension / 2f

                        // Círculos concéntricos de guía
                        drawCircle(
                            color = NeonPurpleAccent.copy(alpha = 0.2f),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )
                        drawCircle(
                            color = NeonPurpleAccent.copy(alpha = 0.3f),
                            radius = radius * 0.66f,
                            center = center,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                        drawCircle(
                            color = NeonPurpleAccent.copy(alpha = 0.4f),
                            radius = radius * 0.33f,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // Cruz de radar
                        drawLine(
                            color = NeonPurpleAccent.copy(alpha = 0.3f),
                            start = Offset(center.x, 0f),
                            end = Offset(center.x, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                        drawLine(
                            color = NeonPurpleAccent.copy(alpha = 0.3f),
                            start = Offset(0f, center.y),
                            end = Offset(size.width, center.y),
                            strokeWidth = 1.dp.toPx()
                        )

                        // Haz de barrido rotatorio
                        drawArc(
                            color = NeonPurpleAccent.copy(alpha = 0.4f),
                            startAngle = sweepAngle - 60f,
                            sweepAngle = 60f,
                            useCenter = true,
                            size = Size(size.width, size.height)
                        )
                    }

                    // ICONO CENTRAL
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = if (isConnected) StatusOnline.copy(alpha = 0.3f) else NeonPurpleButton.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = if (isConnected) StatusOnline else NeonPurpleAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ESTADO ACTUAL DE CONEXIÓN
                Text(
                    text = if (isConnected) "¡Conectado al ESP32 con éxito!" else wifiStatus,
                    color = if (isConnected) StatusOnline else NeonPurpleAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // BOTONES DE ACCIÓN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismissRequest,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Cerrar", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                Esp32WifiManager.bindToEsp32Wifi(context, "ESP32_Luminico_IoT")
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonPurpleButton,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (isConnected) "Reconectar" else "Escanear",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
