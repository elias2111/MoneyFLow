package me.elias.unabshop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import me.elias.unabshop.data.FirestoreRepository
import me.elias.unabshop.data.Transaction
import me.elias.unabshop.utils.formatMoney
import java.util.*

@Composable
fun HomeScreen() {

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF7EE8FA), Color(0xFFEEC0C6))
    )

    val items = remember { mutableStateListOf<Transaction>() }
    var budget by remember { mutableStateOf(0.0) }

    LaunchedEffect(Unit) {
        FirestoreRepository.seedIfEmpty()
        FirestoreRepository.listenAll { list ->
            items.clear()
            items.addAll(list)
        }
        FirestoreRepository.listenBudget { b -> budget = b }
    }

    val total = items.sumOf { it.amount }
    val now = Calendar.getInstance()

    // gastos del mes
    val expensesThisMonth = items.filter {
        it.amount < 0 &&
                Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    .get(Calendar.MONTH) == now.get(Calendar.MONTH)
    }.sumOf { -it.amount }

    val budgetRemaining = (budget - expensesThisMonth).coerceAtLeast(0.0)

    // UI
    Box(
        Modifier.fillMaxSize().background(gradient).padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                "MoneyFlow",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color(0xFF3A0CA3),
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(20.dp))

            Icon(
                Icons.Filled.AccountBalanceWallet,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF3A0CA3)
            )

            Spacer(Modifier.height(20.dp))

            Surface(
                color = Color.White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 6.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {

                    Text(
                        formatMoney(total),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = Color(0xFF3A0CA3),
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text("Total balance", color = Color.Gray)

                    Spacer(Modifier.height(20.dp))
                    Divider()
                    Spacer(Modifier.height(20.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(formatMoney(expensesThisMonth))
                            Text("Expense\nthis month", textAlign = TextAlign.Center)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(formatMoney(budgetRemaining))
                            Text("Budget\nremaining", textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // FAB
            FloatingActionButton(
                onClick = {
                    // Agrega un gasto de prueba
                    FirestoreRepository.addTransaction(
                        title = "Example",
                        category = "General",
                        amount = -10.0
                    )
                },
                containerColor = Color(0xFF00C8A2)
            ) {
                Icon(Icons.Filled.Add, null, tint = Color.White)
            }
        }
    }
}




