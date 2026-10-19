package com.example.luminico

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private val CardSurface = Color(0xFF1A1528)
private val InputBackground = Color(0xFF120E20)
private val NeonPurpleAccent = Color(0xFFC084FC)
private val TextSecondary = Color(0xFFA7A3C1)

@Composable
fun ProfileTab(
    userEmail: String,
    onLogout: () -> Unit,
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var imageUri by remember {
        mutableStateOf(
            sessionManager.getProfileImageUri().let { if (it.isNotBlank()) Uri.parse(it) else null }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            sessionManager.saveProfileImageUri(uri.toString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // AVATAR CON AURA (CLICKEABLE PARA SELECCIONAR IMAGEN)
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(InputBackground)
                .border(2.dp, NeonPurpleAccent, CircleShape)
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center,
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = NeonPurpleAccent,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Text(
            text = userEmail.ifBlank { "Usuario Local" },
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Toca el avatar para cambiar tu foto",
            color = TextSecondary,
            fontSize = 11.sp
        )

        // TARJETA DE ESTADO DE LA CUENTA
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NeonPurpleAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Estado de Seguridad Local",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "• Credenciales cifradas localmente con SHA-256.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "• Persistencia de datos gestionada por Room.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // BOTÓN CERRAR SESIÓN
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2D1525),
                contentColor = Color(0xFFFF5252)
            )
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
        }
    }
}
