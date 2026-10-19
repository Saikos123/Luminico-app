package com.example.luminico

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminico.network.Esp32ApiClient
import com.example.luminico.ui.theme.LuminicoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.time.Duration.Companion.seconds

private val BackgroundDark = Color(0xFF0F0C1B)
private val CardSurface = Color(0xFF1A1528)
private val NeonPurpleAccent = Color(0xFFC084FC)
private val TextSecondary = Color(0xFFA7A3C1)
private val StatusOnline = Color(0xFF00E676)
private val StatusOffline = Color(0xFFFF5252)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    userEmail: String,
    onLogout: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    var selectedTab by remember { mutableIntStateOf(0) }

    var showMenu by remember { mutableStateOf(value = false) }
    var showWifiDialog by remember { mutableStateOf(value = false) }

    var isConnected by remember { mutableStateOf(value = false) }

    var pirSensor by remember { mutableStateOf("Desconectado") }
    var micSensor by remember { mutableStateOf("-") }
    var ldrSensor by remember { mutableStateOf("-") }
    var estiloActual by remember { mutableStateOf("-") }

    LaunchedEffect(Unit) {
        while (true) {
            val connected = Esp32ApiClient.checkConnection()
            isConnected = connected

            if (connected) {
                val jsonResult = Esp32ApiClient.getSensorsData()
                if (jsonResult != null) {
                    try {
                        val json = JSONObject(jsonResult)
                        pirSensor = if (json.optBoolean("pir", false)) "DETECTADO" else "Limpio"
                        micSensor = json.optString("mic", "-")
                        ldrSensor = json.optString("ldr", "-")
                        estiloActual = json.optString("estilo", "-")
                    } catch (_: Exception) { }
                }
            } else {
                pirSensor = "Sin Red"
                micSensor = "-"
                ldrSensor = "-"
                estiloActual = "-"
            }
            delay(2.seconds)
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Lumínico IoT",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    color = if (isConnected) StatusOnline else StatusOffline,
                                    shape = CircleShape
                                )
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = if (isConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                        contentDescription = null,
                        tint = if (isConnected) StatusOnline else StatusOffline,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Opciones",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(CardSurface)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Seguridad Wi-Fi ESP32", color = Color.White) },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = NeonPurpleAccent)
                                },
                                onClick = {
                                    showMenu = false
                                    showWifiDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Cerrar Sesión", color = StatusOffline) },
                                leadingIcon = {
                                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = StatusOffline)
                                },
                                onClick = {
                                    showMenu = false
                                    onLogout()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardSurface)
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp)),
                containerColor = CardSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Tune, contentDescription = "Control") },
                    label = { Text("Control", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonPurpleAccent,
                        selectedTextColor = NeonPurpleAccent,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = BackgroundDark
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.GraphicEq, contentDescription = "Ritmo") },
                    label = { Text("Ritmo", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonPurpleAccent,
                        selectedTextColor = NeonPurpleAccent,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = BackgroundDark
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Sensors, contentDescription = "Sensores") },
                    label = { Text("Sensores", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonPurpleAccent,
                        selectedTextColor = NeonPurpleAccent,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = BackgroundDark
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                    label = { Text("Perfil", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = NeonPurpleAccent,
                        selectedTextColor = NeonPurpleAccent,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary,
                        indicatorColor = BackgroundDark
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> ControlTab(isConnected = isConnected)
                1 -> RhythmTab(isConnected = isConnected)
                2 -> SensorsTab(
                    pirSensor = pirSensor,
                    micSensor = micSensor,
                    ldrSensor = ldrSensor,
                    estiloActual = estiloActual,
                )
                3 -> ProfileTab(
                    userEmail = userEmail,
                    onLogout = onLogout,
                )
            }
        }

        if (showWifiDialog) {
            WifiSecurityDialog(
                isConnected = isConnected,
                onDismissRequest = { showWifiDialog = false },
                onChangePasswordSubmit = { oldPass: String, newPass: String, onResult: (Boolean, String) -> Unit ->
                    coroutineScope.launch {
                        val (success, msg) = Esp32ApiClient.changeWifiPassword(oldPass, newPass)
                        onResult(success, msg)
                    }
                },
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    LuminicoTheme {
        DashboardScreen(
            userEmail = "demo@luminico.iot",
            onLogout = {},
        )
    }
}
