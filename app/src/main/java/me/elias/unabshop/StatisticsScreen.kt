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

    val items = remember { mutableStateListOf<Transaction>() }

    LaunchedEffect(Unit) {
        FirestoreRepository.listenAll { list ->
            items.clear()
            items.addAll(list)
        }
    }

    val ingresos = items.filter { it.amount > 0 }.sumOf { it.amount }
    val gastos = items.filter { it.amount < 0 }.sumOf { -it.amount }

    Box(
        Modifier
            .fillMaxSize()
            .background(gradient)
            .padding(20.dp),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                "Statistics",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color(0xFF3A0CA3)
                )
            )

            Spacer(Modifier.height(20.dp))

            if (items.isEmpty()) {
                Text("No data yet")
            } else {

                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    factory = { ctx ->
                        PieChart(ctx).apply {

                            val entries = mutableListOf<PieEntry>()
                            if (ingresos > 0) entries.add(PieEntry(ingresos.toFloat(), "Ingresos"))
                            if (gastos > 0) entries.add(PieEntry(gastos.toFloat(), "Gastos"))

                            val set = PieDataSet(entries, "Balance").apply {
                                colors = listOf(
                                    Color(0xFF00A676).toArgb(), // Ingresos
                                    Color(0xFFB00020).toArgb()  // Gastos
                                )
                                valueTextSize = 14f
                                valueTextColor = android.graphics.Color.WHITE
                            }

                            data = PieData(set)

                            description.isEnabled = false
                            legend.isEnabled = true

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





