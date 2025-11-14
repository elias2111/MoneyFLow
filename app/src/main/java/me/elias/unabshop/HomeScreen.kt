package me.elias.unabshop

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.elias.unabshop.data.FirestoreRepository
import me.elias.unabshop.data.Transaction
import me.elias.unabshop.utils.formatMoney
import kotlin.math.min

@Composable
fun HomeScreen() {

    val transactions = remember { mutableStateListOf<Transaction>() }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        FirestoreRepository.listenAll { list ->
            transactions.clear()
            transactions.addAll(list)
        }
    }

    // ================= Cálculos =================
    val totalIncome = transactions.filter { it.amount > 0 }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.amount < 0 }.sumOf { -it.amount }
    val balance = totalIncome - totalExpense

    // ========= EMOJI dinámico según balance =========
    val emoji = when {
        balance > 0 -> "😄"
        balance < 0 -> "😟"
        else -> "😐"
    }

    // ANIMACIÓN DEL EMOJI
    val scale by animateFloatAsState(
        targetValue = if (balance != 0.0) 1.2f else 1f,
        animationSpec = tween(600, easing = FastOutSlowInEasing)
    )

    // ========= FONDO dinámico según balance =========
    val backgroundGradient = when {
        balance > 0 -> Brush.verticalGradient(
            listOf(Color(0xFF9BE7FF), Color(0xFFC6F6D5))
        )
        balance < 0 -> Brush.verticalGradient(
            listOf(Color(0xFFFFB5B5), Color(0xFFF7D4D4))
        )
        else -> Brush.verticalGradient(
            listOf(Color(0xFFE0E0E0), Color.White)
        )
    }

    // ========= ANIMACIÓN DEL BALANCE =========
    var animatedBalance by remember { mutableStateOf(balance) }

    LaunchedEffect(balance) {
        animate(
            initialValue = animatedBalance.toFloat(),
            targetValue = balance.toFloat(),
            animationSpec = tween(600, easing = LinearOutSlowInEasing)
        ) { value, _ ->
            animatedBalance = value.toDouble()
        }
    }

    // ========= PROGRESO GRÁFICO =========
    val progress = if (totalIncome + totalExpense == 0.0) {
        0f
    } else {
        min(
            1f,
            (totalIncome / (totalIncome + totalExpense)).toFloat()
        )
    }

    // ===================== UI =========================
    Box(
        Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(20.dp)
    ) {

        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                "MoneyFlow",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color(0xFF3A0CA3),
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(Modifier.height(10.dp))

            // ANIMATED EMOJI
            AnimatedContent(
                targetState = emoji,
                transitionSpec = {
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                }
            ) { e ->
                Text(
                    e,
                    fontSize = 80.sp,
                    modifier = Modifier.scale(scale)
                )
            }

            Spacer(Modifier.height(10.dp))

            // CARD BALANCE
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White.copy(alpha = 0.95f),
                shadowElevation = 10.dp
            ) {
                Column(
                    Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text("Balance total", color = Color.Gray)

                    Text(
                        formatMoney(animatedBalance),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = if (balance >= 0) Color(0xFF00A676) else Color(0xFFB00020)
                        )
                    )

                    Spacer(Modifier.height(16.dp))
                    Divider()
                    Spacer(Modifier.height(16.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ingresos", color = Color.Gray)
                            Text(
                                formatMoney(totalIncome),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00A676)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Gastos", color = Color.Gray)
                            Text(
                                formatMoney(totalExpense),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB00020)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // ⭐ MINI GRÁFICA
            BalanceMiniGraph(progress)

            Spacer(Modifier.height(20.dp))

            Text(
                "Registra tus ingresos o gastos con el botón de abajo",
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
        }

        // FAB
        FloatingActionButton(
            onClick = { showDialog = true },
            containerColor = Color(0xFF00C8A2),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar")
        }
    }

    if (showDialog) AddTransactionDialog { showDialog = false }
}

@Composable
fun BalanceMiniGraph(progress: Float) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.92f),
        shadowElevation = 8.dp
    ) {
        Box(Modifier.padding(16.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {

                // Fondo
                drawRoundRect(
                    color = Color.LightGray.copy(alpha = .4f),
                    cornerRadius = CornerRadius(20f, 20f)
                )

                // Progreso
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF00A676), Color(0xFF4CC9F0))
                    ),
                    cornerRadius = CornerRadius(20f, 20f),
                    size = size.copy(width = size.width * progress)
                )
            }
        }
    }
}






