package me.elias.unabshop.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val user get() = FirebaseAuth.getInstance().currentUser

    private fun txCol() =
        user?.let { db.collection("users").document(it.uid).collection("transactions") }
            ?: db.collection("debug_transactions")

    fun listenAll(onChange: (List<Transaction>) -> Unit) {
        txCol().addSnapshotListener { snap, _ ->
            val list = snap?.documents?.mapNotNull { doc ->
                doc.toObject(Transaction::class.java)?.copy(id = doc.id)
            } ?: emptyList()
            onChange(list)
        }
    }

    fun addTransaction(title: String, category: String, amount: Double) {
        val tx = Transaction(
            title = title,
            category = category,
            amount = amount,
            timestamp = System.currentTimeMillis()
        )

        txCol().add(tx)
    }

    fun clearAll() {
        txCol().get().addOnSuccessListener { snap ->
            val batch = db.batch()
            snap.documents.forEach { batch.delete(it.reference) }
            batch.commit()
        }
    }
}




