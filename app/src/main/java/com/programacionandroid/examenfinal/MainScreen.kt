package com.programacionandroid.examenfinal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.util.Log

@Composable
fun MainScreen() {
    var message by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }

    val repository = remember { FirebaseRepository() }


    LaunchedEffect(Unit) {
        repository.getItems(onSuccess = { itemsList ->
            items = itemsList
        }, onFailure = { exception ->
            Log.e("MainScreen", "Error loading items", exception)
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = topic,
            onValueChange = { topic = it },
            label = { Text("Topic") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val newItem = Item(
                    title = topic, description = message
                )
                repository.addItem(item = newItem, onSuccess = {

                    topic = ""
                    message = ""

                    repository.getItems(onSuccess = { updatedList ->
                        items = updatedList
                    }, onFailure = { exception ->
                        Log.e("MainScreen", "Error reloading items", exception)
                    })
                }, onFailure = { exception ->
                    Log.e("MainScreen", "Error adding item", exception)
                })
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Enviar Mensaje")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Mensajes Recibidos:", style = MaterialTheme.typography.bodySmall)

        LazyColumn(
            modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = item.title, style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.description, style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}