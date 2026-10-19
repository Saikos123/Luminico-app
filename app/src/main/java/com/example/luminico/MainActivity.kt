package com.example.luminico

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.lifecycleScope
import com.example.luminico.data.AppDatabase
import com.example.luminico.data.UserEntity
import com.example.luminico.ui.theme.LuminicoTheme
import com.example.luminico.utils.toSha256
import kotlinx.coroutines.launch

enum class AuthState {
    LOGIN,
    REGISTER,
    DASHBOARD
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(applicationContext)
        val db = AppDatabase.getDatabase(applicationContext)
        val userDao = db.userDao()

        setContent {
            LuminicoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0F0C1B),
                ) {
                    var currentScreen by remember {
                        mutableStateOf(
                            if (sessionManager.isLoggedIn()) AuthState.DASHBOARD else AuthState.LOGIN,
                        )
                    }
                    var userEmail by remember {
                        mutableStateOf(sessionManager.getSavedEmail())
                    }

                    when (currentScreen) {
                        AuthState.LOGIN -> {
                            LoginScreen(
                                onAuthSuccess = { email, password, rememberMe ->
                                    lifecycleScope.launch {
                                        val user = userDao.getUserByEmail(email)
                                        if ((user != null) && (user.passwordHash == password.toSha256())) {
                                            userEmail = email
                                            sessionManager.saveUserSession(email, rememberMe)
                                            currentScreen = AuthState.DASHBOARD
                                            Toast.makeText(
                                                this@MainActivity,
                                                "¡Bienvenido de nuevo!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "Correo o contraseña incorrectos",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },
                                onNavigateToRegister = {
                                    currentScreen = AuthState.REGISTER
                                },
                            )
                        }

                        AuthState.REGISTER -> {
                            RegisterScreen(
                                onRegisterSuccess = { name, email, password ->
                                    lifecycleScope.launch {
                                        val existingUser = userDao.getUserByEmail(email)
                                        if (existingUser == null) {
                                            val newUser = UserEntity(
                                                email = email,
                                                fullName = name,
                                                passwordHash = password.toSha256()
                                            )
                                            userDao.registerUser(newUser)
                                            userEmail = email
                                            sessionManager.saveUserSession(email, rememberMe = true)
                                            currentScreen = AuthState.DASHBOARD
                                            Toast.makeText(
                                                this@MainActivity,
                                                "¡Cuenta registrada con éxito!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@MainActivity,
                                                "El correo ya se encuentra registrado",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                },
                                onNavigateToLogin = {
                                    currentScreen = AuthState.LOGIN
                                },
                            )
                        }

                        AuthState.DASHBOARD -> {
                            DashboardScreen(
                                userEmail = userEmail,
                                onLogout = {
                                    sessionManager.clearSession()
                                    Esp32WifiManager.unbindWifi(applicationContext)
                                    userEmail = ""
                                    currentScreen = AuthState.LOGIN
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
