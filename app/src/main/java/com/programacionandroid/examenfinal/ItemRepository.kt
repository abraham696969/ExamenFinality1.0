package com.programacionandroid.examenfinal

import android.os.Handler
import android.os.Looper

class ItemRepository {

    private val items = mutableListOf<Item>()


    fun getItems(onSuccess: (List<Item>) -> Unit, onFailure: (Exception) -> Unit) {

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                onSuccess(items)
            } catch (e: Exception) {
                onFailure(e)
            }
        }, 1000)
    }

    fun addItem(item: Item, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                item.id = generateId()
                items.add(item)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }, 1000)
    }

    private fun generateId(): String {
        return (items.size + 1).toString()
    }
}