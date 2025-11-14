package me.elias.unabshop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.elias.unabshop.data.FirestoreRepository
import me.elias.unabshop.data.Transaction
import me.elias.unabshop.utils.formatMoney

@Composable
fun TransactionsScreen() {

    val tx = remember { mutableStateListOf<Transaction>() }
    var filter by remember { mutableStateOf("Todos") }

    LaunchedEffect(Unit) {
        FirestoreRepository.listenAll {
            tx.clear()
            tx.addAll(it.sortedByDescending { t -> t.timestamp })
        }
    }

    val gradient = Brush.verticalGradient(listOf(Color(0xFF7EE8FA), Color(0xFFEEC0C6)))

    val filtered = when (filter) {
        "Ingresos" -> tx.filter { it.amount > 0 }
        "Gastos" -> tx.filter { it.amount < 0 }
        else -> tx
    }

    Column(
        Modifier.fillMaxSize().background(gradient).padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Transacciones", style = MaterialTheme.typography.headlineLarge)

        Spacer(Modifier.height(20.dp))

        // FILTRO
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {

            FilterButton("Todos", filter) { filter = it }
            FilterButton("Ingresos", filter) { filter = it }
            FilterButton("Gastos", filter) { filter = it }
        }

        Spacer(Modifier.height(15.dp))

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White.copy(alpha = .95f),
            shape = RoundedCornerShape(20.dp)
        ) {
            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay transacciones")
                }
            } else {
                LazyColumn(Modifier.padding(20.dp)) {
                    items(filtered) { t ->
                        Row(
                            Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(t.title, style = MaterialTheme.typography.titleMedium)
                                Text(t.category, color = Color.Gray)
                            }

                            Text(
                                formatMoney(t.amount),
                                color = if (t.amount >= 0) Color(0xFF00A676) else Color(0xFFB00020)
                            )
                        }
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun FilterButton(name: String, selected: String, onClick: (String) -> Unit) {
    Button(
        onClick = { onClick(name) },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected == name) Color(0xFF3A0CA3) else Color.LightGray
        )
    ) {
        Text(name)
    }
}








