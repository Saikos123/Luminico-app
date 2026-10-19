package com.example.luminico

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.luminico.ui.theme.LuminicoTheme

private val BackgroundDark = Color(0xFF0F0C1B)
private val CardSurface = Color(0xFF1A1528)
private val InputBackground = Color(0xFF120E20)
private val NeonPurpleAccent = Color(0xFFC084FC)
private val NeonPurpleButton = Color(0xFFA855F7)
private val BorderInactive = Color(0xFF2E2648)
private val TextSecondary = Color(0xFFA7A3C1)

@Composable
fun LoginScreen(
    onAuthSuccess: (email: String, password: String, rememberMe: Boolean) -> Unit,
    onNavigateToRegister: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val sessionManager = remember { SessionManager(context) }

    var emailInput by remember { mutableStateOf(sessionManager.getSavedEmail()) }
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(value = false) }
    var rememberMe by remember { mutableStateOf(value = true) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun submitLogin() {
        var isValid = true

        if (emailInput.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            emailError = "Ingresa un correo válido"
            isValid = false
        }

        if (passwordInput.length < 6) {
            passwordError = "Mínimo 6 caracteres"
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(context, "Por favor revisa los campos indicados", Toast.LENGTH_SHORT).show()
        } else {
            onAuthSuccess(emailInput.trim(), passwordInput, rememberMe)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .background(Color(0xFF251C3A), shape = RoundedCornerShape(24.dp))
                .padding(18.dp),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Logo Lumínico",
                tint = NeonPurpleAccent,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Lumínico IoT",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = "Control de Iluminación y Vúmetro",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {

                Text(
                    text = "Iniciar Sesión",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 18.dp)
                )

                Text(
                    text = "CORREO ELECTRÓNICO",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = emailInput,
                    onValueChange = {
                        emailInput = it
                        emailError = null
                    },
                    placeholder = { Text("usuario@luminico.iot", color = Color(0xFF6B658A)) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = TextSecondary) },
                    isError = emailError != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        focusedBorderColor = NeonPurpleAccent,
                        unfocusedBorderColor = BorderInactive,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = TextSecondary,
                        unfocusedPlaceholderColor = Color(0xFF6B658A),
                        errorBorderColor = Color(0xFFFF5252)
                    )
                )
                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = Color(0xFFFF5252),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "CONTRASEÑA",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = {
                        passwordInput = it
                        passwordError = null
                    },
                    placeholder = { Text("••••••••", color = Color(0xFF6B658A)) },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = TextSecondary) },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Mostrar/Ocultar contraseña",
                                tint = TextSecondary
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = passwordError != null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            submitLogin()
                        }
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBackground,
                        unfocusedContainerColor = InputBackground,
                        focusedBorderColor = NeonPurpleAccent,
                        unfocusedBorderColor = BorderInactive,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = TextSecondary,
                        unfocusedPlaceholderColor = Color(0xFF6B658A),
                        errorBorderColor = Color(0xFFFF5252)
                    )
                )
                if (passwordError != null) {
                    Text(
                        text = passwordError!!,
                        color = Color(0xFFFF5252),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = NeonPurpleAccent,
                            uncheckedColor = BorderInactive,
                            checkmarkColor = Color.Black
                        )
                    )
                    Text(
                        text = "Recordar mi sesión en este dispositivo",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        submitLogin()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonPurpleButton,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Ingresar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "¿No tienes una cuenta?",
                color = TextSecondary,
                fontSize = 12.sp,
            )
            TextButton(onClick = onNavigateToRegister) {
                Text(
                    text = "Crear Cuenta",
                    color = NeonPurpleAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LuminicoTheme {
        LoginScreen(
            onAuthSuccess = { _, _, _ -> },
            onNavigateToRegister = {},
        )
    }
}
