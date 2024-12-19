package com.programacionandroid.examenfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.programacionandroid.examenfinal.ui.theme.ExamenFinalTheme

class MainActivity : ComponentActivity() {
    private val repository = ItemRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExamenFinalTheme {
                MainScreen(repository)
            }
        }
    }
}

@Composable
fun MainScreen(repository: ItemRepository) {
    Scaffold(content = { paddingValues ->
        MainContent(modifier = Modifier.padding(paddingValues), repository = repository)
    })
}

@Composable
fun MainContent(modifier: Modifier = Modifier, repository: ItemRepository) {
    val items = remember { mutableStateOf<List<Item>>(emptyList()) }
    val message = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        repository.getItems(onSuccess = { itemList ->
            items.value = itemList
            message.value = "Ítems obtenidos: ${itemList.size}"
        }, onFailure = { exception ->
            message.value = "Error al obtener ítems: ${exception.message}"
        })
    }


    val newItem = Item(title = "Topic", description = "Message")
    repository.addItem(item = newItem, onSuccess = {
        message.value = "Ítem agregado exitosamente con ID: ${newItem.id}"
    }, onFailure = { exception ->
        message.value = "Error al agregar el ítem: ${exception.message}"
    })

    Text(
        text = message.value, modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
    ExamenFinalTheme {
        MainScreen(repository = ItemRepository())
    }
}
