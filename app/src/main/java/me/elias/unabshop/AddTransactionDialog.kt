package me.elias.unabshop

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.elias.unabshop.data.FirestoreRepository

@Composable
fun AddTransactionDialog(onClose: () -> Unit) {

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Gasto") }
    var category by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onClose,
        title = { Text("Registrar transacción") },
        text = {
            Column {

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Razón / descripción") }
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Monto") }
                )

                Spacer(Modifier.height(12.dp))

                // Tipo
                Row {
                    RadioButton(selected = type == "Ingreso", onClick = { type = "Ingreso" })
                    Text("Ingreso")

                    Spacer(Modifier.width(20.dp))

                    RadioButton(selected = type == "Gasto", onClick = { type = "Gasto" })
                    Text("Gasto")
                }

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Categoría") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (title.isNotBlank() && amount.isNotBlank()) {
                    val amt = amount.toDouble()
                    val finalAmt = if (type == "Ingreso") amt else -amt

                    FirestoreRepository.addTransaction(title, category, finalAmt)
                    onClose()
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onClose) { Text("Cancelar") }
        }
    )
}
