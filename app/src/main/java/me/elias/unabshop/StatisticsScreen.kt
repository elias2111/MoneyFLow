package me.elias.unabshop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import me.elias.unabshop.data.FirestoreRepository
import me.elias.unabshop.data.Transaction

@Composable
fun StatisticsScreen() {

    val gradient = Brush.verticalGradient(
        listOf(Color(0xFF7EE8FA), Color(0xFFEEC0C6))
    )

    val tx = remember { mutableStateListOf<Transaction>() }

    LaunchedEffect(Unit) {
        FirestoreRepository.listenAll { list ->
            tx.clear()
            tx.addAll(list)
        }
    }

    // Solo gastos (amount negativo)
    val totals = tx.filter { it.amount < 0 }
        .groupBy { it.category }
        .mapValues { (_, data) ->
            -data.sumOf { it.amount } // Hacerlos positivos para el gráfico
        }

    Box(
        Modifier.fillMaxSize().background(gradient).padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                "MoneyFlow",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color(0xFF3A0CA3)
                )
            )

            Text(
                "Statistics",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color(0xFF3A0CA3)
                )
            )

            Spacer(Modifier.height(20.dp))

            if (totals.isEmpty()) {
                Text("No expenses yet", color = Color.DarkGray)
            } else {

                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    factory = { ctx ->

                        PieChart(ctx).apply {

                            val entries = totals.map { (cat, value) ->
                                PieEntry(value.toFloat(), cat)
                            }

                            val set = PieDataSet(entries, "Expenses by category").apply {
                                colors = listOf(
                                    Color(0xFF4361EE).toArgb(),
                                    Color(0xFF3A0CA3).toArgb(),
                                    Color(0xFF4CC9F0).toArgb(),
                                    Color(0xFF7209B7).toArgb(),
                                    Color(0xFFF72585).toArgb()
                                )
                                valueTextColor = android.graphics.Color.WHITE
                                valueTextSize = 14f
                                sliceSpace = 2f
                            }

                            data = PieData(set)

                            legend.isEnabled = true
                            legend.textSize = 14f

                            setUsePercentValues(true)

                            description.isEnabled = false
                            isDrawHoleEnabled = true
                            holeRadius = 40f

                            setEntryLabelColor(android.graphics.Color.BLACK)
                            setEntryLabelTextSize(12f)

                            invalidate()
                        }
                    }
                )
            }
        }
    }
}






