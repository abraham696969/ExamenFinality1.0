package com.programacionandroid.examenfinal

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val itemsCollection = db.collection("items")

    fun getItems(onSuccess: (List<Item>) -> Unit, onFailure: (Exception) -> Unit) {
        itemsCollection.orderBy(
                "timestamp",
                com.google.firebase.firestore.Query.Direction.DESCENDING
            ).get().addOnSuccessListener { result ->
                val itemsList = mutableListOf<Item>()
                for (document in result) {
                    try {
                        val item = Item(
                            id = document.id,
                            title = document.getString("title") ?: "",
                            description = document.getString("description") ?: ""
                        )
                        itemsList.add(item)
                    } catch (e: Exception) {
                        onFailure(e)
                    }
                }
                onSuccess(itemsList)
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun addItem(item: Item, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val itemData = hashMapOf(
            "title" to item.title,
            "description" to item.description,
            "timestamp" to FieldValue.serverTimestamp()
        )

        itemsCollection.add(itemData).addOnSuccessListener { documentReference ->
                item.id = documentReference.id
                onSuccess()
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}