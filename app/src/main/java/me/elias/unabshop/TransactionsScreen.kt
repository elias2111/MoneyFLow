package me.elias.unabshop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.elias.unabshop.data.FirestoreRepository
import me.elias.unabshop.data.Transaction
import me.elias.unabshop.utils.formatMoney
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionsScreen() {

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF7EE8FA), Color(0xFFEEC0C6))
    )

    val items = remember { mutableStateListOf<Transaction>() }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        FirestoreRepository.listenAll { list ->
            items.clear()
            items.addAll(list.sortedByDescending { it.timestamp })
        }
    }

    Box(
        Modifier.fillMaxSize().background(gradient).padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                "MoneyFlow",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color(0xFF3A0CA3), fontWeight = FontWeight.Bold
                )
            )

            Text(
                "Transactions",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFF3A0CA3), fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(20.dp))

            Surface(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.95f)
            ) {

                if (items.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No transactions yet", color = Color.Gray)
                    }
                } else {
                    LazyColumn(Modifier.padding(20.dp)) {
                        items(items) { t ->
                            TransactionRow(t)
                            Divider()
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFF00C8A2)
            ) {
                Icon(Icons.Filled.Add, null, tint = Color.White)
            }
        }
    }

    if (showDialog) AddTransactionDialog { showDialog = false }
}

@Composable
private fun TransactionRow(t: Transaction) {

    val formattedDate = remember(t.timestamp) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            .format(Date(t.timestamp))
    }

    Row(
        Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Column {
            Text(t.title, fontWeight = FontWeight.Bold)
            Text("${t.category} • $formattedDate", color = Color.Gray)
        }

        Text(
            formatMoney(t.amount),
            fontWeight = FontWeight.Bold,
            color = if (t.amount >= 0) Color(0xFF00A676) else Color(0xFFB00020)
        )
    }
}

@Composable
private fun AddTransactionDialog(onDismiss: () -> Unit) {

    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }
    var amountText by remember { mutableStateOf("") }
    var isIncome by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val amount = amountText.toDoubleOrNull()

                if (title.isBlank() || amount == null) {
                    error = "Please fill title and amount correctly"
                    return@TextButton
                }

                FirestoreRepository.addTransaction(
                    title = title,
                    category = category,
                    amount = if (isIncome) amount else -amount
                )

                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        title = { Text("Add Transaction") },
        text = {
            Column {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category") }
                )

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount") }
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isIncome,
                        onCheckedChange = { isIncome = it }
                    )
                    Text("Income (unchecked = Expense)")
                }

                error?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, color = Color.Red)
                }
            }
        }
    )
}





