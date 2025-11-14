package me.elias.unabshop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.elias.unabshop.data.FirestoreRepository

@Composable
fun SettingsScreen() {

    val gradient = Brush.verticalGradient(listOf(Color(0xFF7EE8FA), Color(0xFFEEC0C6)))

    Column(
        Modifier.fillMaxSize().background(gradient),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Button(
            onClick = { FirestoreRepository.clearAll() },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Eliminar TODAS las transacciones")
        }
    }
}





