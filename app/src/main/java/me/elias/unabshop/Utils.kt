package me.elias.unabshop.utils

import java.text.NumberFormat
import java.util.Locale

fun formatMoney(value: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    nf.maximumFractionDigits = 2
    return nf.format(value)
}
