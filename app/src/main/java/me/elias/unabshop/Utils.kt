package me.elias.unabshop.utils

import java.text.NumberFormat
import java.util.*

fun formatMoney(value: Double): String =
    NumberFormat.getCurrencyInstance(Locale.getDefault()).format(value)

