package com.example.luminico

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val CardSurface = Color(0xFF1A1528)
private val InputBackground = Color(0xFF120E20)
private val NeonPurpleAccent = Color(0xFFC084FC)
private val TextSecondary = Color(0xFFA7A3C1)

@Composable
fun SensorsTab(
    pirSensor: String,
    micSensor: String,
    ldrSensor: String,
    estiloActual: String,
) {
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
                        imageVector = Icons.Default.Sensors,
                        contentDescription = null,
                        tint = NeonPurpleAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Telemetría de Sensores",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SensorTile(title = "PRESENCIA PIR", value = pirSensor)
                    SensorTile(title = "NIVEL MICRÓFONO", value = micSensor)
                    SensorTile(title = "LUZ AMBIENTAL LDR", value = ldrSensor)
                    SensorTile(title = "ESTILO VISUAL ACTIVO", value = estiloActual)
                }
            }
        }
    }
}

@Composable
private fun SensorTile(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputBackground, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}
