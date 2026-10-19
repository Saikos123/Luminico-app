package com.example.luminico

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

// PALETA NEÓN MORADO
private val CardSurface = Color(0xFF1A1528)
private val InputBackground = Color(0xFF120E20)
private val NeonPurpleAccent = Color(0xFFC084FC)
private val NeonPurpleButton = Color(0xFFA855F7)
private val BorderInactive = Color(0xFF2E2648)
private val TextSecondary = Color(0xFFA7A3C1)

@Composable
fun WifiSecurityDialog(
    isConnected: Boolean,
    onDismissRequest: () -> Unit,
    onChangePasswordSubmit: (oldPass: String, newPass: String, onResult: (Boolean, String) -> Unit) -> Unit,
) {
    var oldWifiPassword by remember { mutableStateOf("") }
    var newWifiPassword by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(value = false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // ÍCONO Y TÍTULO
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFF251C3A), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = NeonPurpleAccent,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Seguridad Wi-Fi ESP32",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Cambia la clave de acceso del Access Point.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                )

                // CAMPO CLAVE ACTUAL
                OutlinedTextField(
                    value = oldWifiPassword,
                    onValueChange = { oldWifiPassword = it },
                    placeholder = { Text("Clave actual", color = Color(0xFF6B658A)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        focusedBorderColor = NeonPurpleAccent,
                        unfocusedBorderColor = BorderInactive,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // CAMPO NUEVA CLAVE
                OutlinedTextField(
                    value = newWifiPassword,
                    onValueChange = { newWifiPassword = it },
                    placeholder = { Text("Nueva clave (8-63 caracteres)", color = Color(0xFF6B658A)) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        focusedBorderColor = NeonPurpleAccent,
                        unfocusedBorderColor = BorderInactive,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                if (statusMessage != null) {
                    Text(
                        text = statusMessage!!,
                        color = NeonPurpleAccent,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

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
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                    ) {
                        Text("Cancelar", fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            if (newWifiPassword.length !in 8..63) {
                                statusMessage = "La clave debe tener entre 8 y 63 caracteres"
                                return@Button
                            }

                            isSubmitting = true
                            statusMessage = "Enviando al ESP32..."

                            onChangePasswordSubmit(oldWifiPassword, newWifiPassword) { success, msg ->
                                isSubmitting = false
                                statusMessage = if (success) {
                                    "¡Clave actualizada!"
                                } else {
                                    msg
                                }
                            }
                        },
                        enabled = isConnected && !isSubmitting,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonPurpleButton,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (isSubmitting) "..." else "Guardar",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
