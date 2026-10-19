package com.example.luminico

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminico.network.Esp32ApiClient
import kotlinx.coroutines.launch

private val CardSurface = Color(0xFF1A1528)
private val NeonPurpleAccent = Color(0xFFC084FC)
private val NeonPurpleButton = Color(0xFFA855F7)
private val BorderInactive = Color(0xFF2E2648)
private val TextSecondary = Color(0xFFA7A3C1)

@Composable
fun RhythmTab(isConnected: Boolean) {
    val coroutineScope = rememberCoroutineScope()
    var micSensitivity by remember { mutableFloatStateOf(35f) }

    val styles = listOf(
        "auto" to "Auto-Rotación",
        "0" to "VU Meter",
        "1" to "Espectro RGB",
        "2" to "Explosión",
        "3" to "Arcoíris Beat",
        "4" to "Destellos",
        "5" to "VU Centro",
        "6" to "Pulso Bass",
        "7" to "Carrusel",
        "8" to "Chispas RGB",
        "9" to "Fuego Rítmico",
        "10" to "Ondas",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = null,
                        tint = NeonPurpleAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Animaciones del Vúmetro",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                styles.chunked(2).forEach { rowStyles ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowStyles.forEach { (key, title) ->
                            OutlinedButton(
                                onClick = {
                                    coroutineScope.launch {
                                        Esp32ApiClient.sendActuatorCommand("estilo=$key")
                                    }
                                },
                                enabled = true,
                                modifier = Modifier.weight(1f).height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text(title, fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sensibilidad del Micrófono: ${micSensitivity.toInt()}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Slider(
                    value = micSensitivity,
                    onValueChange = { micSensitivity = it },
                    onValueChangeFinished = {
                        coroutineScope.launch {
                            Esp32ApiClient.sendActuatorCommand("sens=${micSensitivity.toInt()}")
                        }
                    },
                    valueRange = 5f..200f,
                    enabled = true,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonPurpleAccent,
                        activeTrackColor = NeonPurpleButton,
                        inactiveTrackColor = BorderInactive
                    )
                )
            }
        }
    }
}
