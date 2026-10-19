package com.example.luminico

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminico.network.Esp32ApiClient
import com.example.luminico.utils.triggerHaptic
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private val CardSurface = Color(0xFF1A1528)
private val InputBackground = Color(0xFF120E20)
private val NeonPurpleAccent = Color(0xFFC084FC)
private val NeonPurpleButton = Color(0xFFA855F7)
private val BorderInactive = Color(0xFF2E2648)
private val TextSecondary = Color(0xFFA7A3C1)
private val StatusOnline = Color(0xFF00E676)
private val StatusOffline = Color(0xFFFF5252)

@Composable
fun ControlTab(isConnected: Boolean) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    // Estado del color personalizado activo
    var pickedColor by remember { mutableStateOf(Color(0xFFC084FC)) }

    val quickColors = listOf(
        Triple("Morado", Color(0xFFC084FC), "r=192&g=132&b=252"),
        Triple("Azul Neón", Color(0xFF00E5FF), "r=0&g=229&b=255"),
        Triple("Verde", Color(0xFF00E676), "r=0&g=230&b=118"),
        Triple("Rojo", Color(0xFFFF1744), "r=255&g=23&b=68"),
        Triple("Amarillo", Color(0xFFFFEA00), "r=255&g=234&b=0"),
        Triple("Blanco", Color(0xFFFFFFFF), "r=255&g=255&b=255"),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // TARJETA 1: LOCALIZADOR / RADAR WI-FI ESP32
        Esp32RadarCard(isConnected = isConnected)

        // TARJETA 2: RUEDA CROMÁTICA INTERACTIVA (BOLA DE COLOR A MEDIDA)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.ColorLens,
                        contentDescription = null,
                        tint = NeonPurpleAccent,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Selector Libre de Color",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Arrastra el dedo sobre la bola para elegir cualquier tono RGB.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 14.dp)
                )

                // RUEDA CANVASCIRCULAR INTERACTIVA
                ColorWheelPicker(
                    enabled = true,
                    initialColor = pickedColor,
                    onColorChanged = { newColor ->
                        pickedColor = newColor
                    },
                    onColorPickedFinal = { finalColor ->
                        view.triggerHaptic()
                        pickedColor = finalColor
                        val r = (finalColor.red * 255).toInt()
                        val g = (finalColor.green * 255).toInt()
                        val b = (finalColor.blue * 255).toInt()

                        coroutineScope.launch {
                            Esp32ApiClient.sendActuatorCommand("r=$r&g=$g&b=$b")
                        }
                    },
                )

                Spacer(modifier = Modifier.height(16.dp))

                // MUESTRA DEL COLOR ACTUAL SELECCIONADO Y SUS VALORES RGB
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(InputBackground, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(pickedColor)
                                .border(1.dp, Color.White, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            val argb = pickedColor.toArgb()
                            val r = (argb shr 16) and 0xFF
                            val g = (argb shr 8) and 0xFF
                            val b = argb and 0xFF
                            val hex = String.format("#%02X%02X%02X", r, g, b)

                            Text(
                                text = hex,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "RGB: $r, $g, $b",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            view.triggerHaptic()
                            val argb = pickedColor.toArgb()
                            val r = (argb shr 16) and 0xFF
                            val g = (argb shr 8) and 0xFF
                            val b = argb and 0xFF
                            coroutineScope.launch {
                                Esp32ApiClient.sendActuatorCommand("r=$r&g=$g&b=$b")
                            }
                        },
                        enabled = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleButton)
                    ) {
                        Text("Aplicar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // TARJETA 3: MODOS DE OPERACIÓN
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = NeonPurpleAccent,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Modos de Operación",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                val modes = listOf(
                    "auto" to "Automático / Gaming",
                    "ritmo" to "Ritmo Directo",
                    "ambiente" to "Ambiente Pulsante",
                    "hora" to "Horario / LDR",
                    "alerta" to "Seguridad PIR",
                    "estudio" to "Modo Estudio"
                )

                modes.chunked(2).forEach { rowModes ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowModes.forEach { (key, title) ->
                            Button(
                                onClick = {
                                    view.triggerHaptic()
                                    coroutineScope.launch {
                                        Esp32ApiClient.sendActuatorCommand("modo=$key")
                                    }
                                },
                                enabled = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = InputBackground,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // TARJETA 4: PALETA RÁPIDA DE COLORES PRIMARIOS
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = NeonPurpleAccent,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Accesos Rápidos de Color",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    quickColors.forEach { (_, color, query) ->
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (pickedColor == color) 3.dp else 1.dp,
                                    color = if (pickedColor == color) Color.White else BorderInactive,
                                    shape = CircleShape
                                )
                                .clickable {
                                    view.triggerHaptic()
                                    pickedColor = color
                                    coroutineScope.launch {
                                        Esp32ApiClient.sendActuatorCommand(query)
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}

// COMPONENTE: TARJETA DE RADAR WI-FI ESP32
@Composable
fun Esp32RadarCard(isConnected: Boolean) {
    val context = LocalContext.current
    val view = LocalView.current
    var showRadarDialog by remember { mutableStateOf(false) }
    val wifiStatus by Esp32WifiManager.connectionStatus.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = if (isConnected) StatusOnline else StatusOffline,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isConnected) "ESP32 Enlazado" else "Radar ESP32",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = wifiStatus,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = {
                        view.triggerHaptic()
                        showRadarDialog = true
                        Esp32WifiManager.bindToEsp32Wifi(context, "ESP32_Luminico_IoT")
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurpleButton,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Abrir Radar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showRadarDialog) {
        Esp32RadarDialog(
            isConnected = isConnected,
            onDismissRequest = { showRadarDialog = false }
        )
    }
}

// COMPONENTE CANVAS: BOLA / RUEDA CROMÁTICA 360°
@Composable
fun ColorWheelPicker(
    enabled: Boolean,
    initialColor: Color,
    onColorChanged: (Color) -> Unit,
    onColorPickedFinal: (Color) -> Unit,
    sizeDp: Int = 200,
) {
    val wheelColors = remember {
        listOf(
            Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
        )
    }

    var pickerOffset by remember { mutableStateOf(Offset.Zero) }
    var isInitialized by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput

                    val radius = size.width / 2f
                    val center = Offset(radius, radius)

                    fun updateColorFromOffset(touchOffset: Offset, isFinal: Boolean) {
                        val dx = touchOffset.x - center.x
                        val dy = touchOffset.y - center.y
                        val distance = sqrt(dx * dx + dy * dy)
                        val clampedDistance = distance.coerceAtMost(radius)

                        val angleRad = atan2(dy.toDouble(), dx.toDouble())
                        var angleDeg = Math.toDegrees(angleRad).toFloat()
                        if (angleDeg < 0) angleDeg += 360f

                        val saturation = (clampedDistance / radius).coerceIn(0f, 1f)
                        val selectedColor = Color.hsv(angleDeg, saturation, 1.0f)

                        val clampedDx = cos(angleRad) * clampedDistance
                        val clampedDy = sin(angleRad) * clampedDistance
                        pickerOffset = Offset(center.x + clampedDx.toFloat(), center.y + clampedDy.toFloat())

                        onColorChanged(selectedColor)
                        if (isFinal) {
                            onColorPickedFinal(selectedColor)
                        }
                    }

                    detectTapGestures { tapOffset ->
                        updateColorFromOffset(tapOffset, isFinal = true)
                    }
                }
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput

                    val radius = size.width / 2f
                    val center = Offset(radius, radius)

                    detectDragGestures(
                        onDragEnd = {
                            val dx = pickerOffset.x - center.x
                            val dy = pickerOffset.y - center.y
                            val angleRad = atan2(dy.toDouble(), dx.toDouble())
                            var angleDeg = Math.toDegrees(angleRad).toFloat()
                            if (angleDeg < 0) angleDeg += 360f

                            val distance = sqrt(dx * dx + dy * dy)
                            val saturation = (distance / radius).coerceIn(0f, 1f)
                            val finalColor = Color.hsv(angleDeg, saturation, 1.0f)

                            onColorPickedFinal(finalColor)
                        }
                    ) { change, _ ->
                        change.consume()
                        val touchOffset = change.position
                        val dx = touchOffset.x - center.x
                        val dy = touchOffset.y - center.y
                        val distance = sqrt(dx * dx + dy * dy)
                        val clampedDistance = distance.coerceAtMost(radius)

                        val angleRad = atan2(dy.toDouble(), dx.toDouble())
                        var angleDeg = Math.toDegrees(angleRad).toFloat()
                        if (angleDeg < 0) angleDeg += 360f

                        val saturation = (clampedDistance / radius).coerceIn(0f, 1f)
                        val selectedColor = Color.hsv(angleDeg, saturation, 1.0f)

                        val clampedDx = cos(angleRad) * clampedDistance
                        val clampedDy = sin(angleRad) * clampedDistance
                        pickerOffset = Offset(center.x + clampedDx.toFloat(), center.y + clampedDy.toFloat())

                        onColorChanged(selectedColor)
                    }
                }
        ) {
            val canvasRadius = size.width / 2f
            val canvasCenter = Offset(canvasRadius, canvasRadius)

            if (!isInitialized) {
                pickerOffset = canvasCenter
                isInitialized = true
            }

            // 1. DIBUJAR BOLA CROMÁTICA CON DEGRADADO ANGULAR (SWEEP GRADIENT)
            drawCircle(
                brush = Brush.sweepGradient(wheelColors, center = canvasCenter),
                radius = canvasRadius
            )

            // 2. CAPA RADIAL DE SATURACIÓN (DESDE BLANCO EN EL CENTRO HASTA TRANSPARENTE EN EL BORDE)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White, Color.Transparent),
                    center = canvasCenter,
                    radius = canvasRadius
                ),
                radius = canvasRadius
            )

            // 3. ANILLO / SELECTOR DE POSICIÓN
            drawCircle(
                color = Color.White,
                radius = 12.dp.toPx(),
                center = pickerOffset
            )
            drawCircle(
                color = initialColor,
                radius = 9.dp.toPx(),
                center = pickerOffset
            )
        }
    }
}
