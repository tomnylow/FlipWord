package com.tomnylow.flipword.ui.screens.decks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tomnylow.flipword.domain.model.Deck

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DecksScreen(
    viewModel: DecksViewModel = hiltViewModel(),
    onDeckClick: (Long) -> Unit
) {
    val decks by viewModel.decks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Колоды") },
                actions = {
                    Icon(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .clip(CircleShape)
                            .clickable {
                                showDialog = true
                            },
                        imageVector = Icons.Filled.Add, contentDescription = "Создать колоду"
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (decks.isEmpty()) {
                EmptyState()
            } else {
                DecksList(decks = decks, onDeckClick = onDeckClick)
            }
        }
    }

    if (showDialog) {
        NewDeckDialog(
            onDismiss = { showDialog = false },
            onConfirm = { deckName ->
                viewModel.insertDeck(deckName)
                showDialog = false
            }
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("У вас пока нет колод. Создайте первую!")
    }
}

@Composable
private fun DecksList(decks: List<Deck>, onDeckClick: (Long) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(decks) { deck ->
            DeckItem(deck = deck, onDeckClick = onDeckClick)
        }
    }
}

@Composable
fun DeckItem(deck: Deck, onDeckClick: (Long) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onDeckClick(deck.id) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = deck.name, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun NewDeckDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var newDeckName by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая колода") },
        text = {
            TextField(
                value = newDeckName,
                onValueChange = { newDeckName = it },
                label = { Text("Название колоды") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newDeckName.isNotBlank()) {
                        onConfirm(newDeckName)
                    }
                }
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
