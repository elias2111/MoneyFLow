package me.elias.unabshop.data

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreRepository {

    private val user get() = FirebaseAuth.getInstance().currentUser
    private val db = FirebaseFirestore.getInstance()

    private fun txCol() =
        user?.let { db.collection("users").document(it.uid).collection("transactions") }
            ?: db.collection("debug")

    private fun budgetDoc() =
        user?.let { db.collection("users").document(it.uid).collection("config").document("budget") }
            ?: db.collection("debug").document("budget")

    // -------------------------------------------------------------------
    // LISTEN ALL TRANSACTIONS
    // -------------------------------------------------------------------

    fun listenAll(onChange: (List<Transaction>) -> Unit) {
        txCol().addSnapshotListener { snap, _ ->
            val list = snap?.documents?.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            onChange(list)
        }
    }

    // -------------------------------------------------------------------
    // ADD TRANSACTION
    // -------------------------------------------------------------------

    fun addTransaction(
        title: String,
        category: String,
        amount: Double,
        onDone: () -> Unit = {}
    ) {
        val tx = mapOf(
            "title" to title,
            "category" to category,
            "amount" to amount,
            "timestamp" to Timestamp.now()
        )

        txCol().add(tx).addOnSuccessListener { onDone() }
    }

    // -------------------------------------------------------------------
    // BUDGET SYSTEM
    // -------------------------------------------------------------------

    fun listenBudget(onChange: (Double) -> Unit) {
        budgetDoc().addSnapshotListener { snap, _ ->
            val b = snap?.getDouble("value") ?: 0.0
            onChange(b)
        }
    }

    fun setBudget(value: Double) {
        budgetDoc().set(mapOf("value" to value))
    }

    // -------------------------------------------------------------------
    // RESET DATA
    // -------------------------------------------------------------------

    fun clearAll(onDone: () -> Unit = {}) {
        txCol().get().addOnSuccessListener { snap ->
            val batch = db.batch()
            for (doc in snap.documents) batch.delete(doc.reference)
            batch.commit().addOnSuccessListener { onDone() }
        }
    }

    // -------------------------------------------------------------------
    // SEED SAMPLE DATA (CORREGIDO)
    // -------------------------------------------------------------------

    fun seedIfEmpty() {
        txCol().get().addOnSuccessListener { snap ->
            if (!snap.isEmpty) return@addOnSuccessListener

            val sample = listOf(
                Transaction(title = "Salary", category = "Income", amount = 3000.0),
                Transaction(title = "Groceries", category = "Food", amount = -150.0),
                Transaction(title = "Coffee", category = "Food", amount = -5.0),
                Transaction(title = "Rent", category = "Home", amount = -1000.0)
            )

            val col = txCol()
            sample.forEach { col.add(it) }
        }
    }
}





