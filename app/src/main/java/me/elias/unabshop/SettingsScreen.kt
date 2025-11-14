package me.elias.unabshop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF7EE8FA), Color(0xFFEEC0C6))
    )

    Box(
        Modifier.fillMaxSize().background(gradient).padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                "Settings",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color(0xFF3A0CA3)
                )
            )

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { FirestoreRepository.clearAll() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Reset Data")
            }
        }
    }
}




