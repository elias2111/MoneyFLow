package me.elias.unabshop.data

data class Transaction(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val amount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)




