package com.tomnylow.flipword.ui.screens.deck_detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.ui.icons.FontAwesomeMagic
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    deckId: Long,
    viewModel: DeckDetailViewModel = hiltViewModel(creationCallback = { factory: DeckDetailViewModel.Factory ->
        factory.create(deckId)
    }),
    onNavigateBack: () -> Unit,
    onLearnClick: (Long) -> Unit,
    onRepeatClick: (Long) -> Unit
) {
    val deck by viewModel.deck.collectAsState()
    val cards by viewModel.cards.collectAsState()
    val isAutoFilling by viewModel.isAutoFilling.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(deck?.name ?: "Загрузка...") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    deck?.id?.let { id ->
                        TextButton(onClick = { onLearnClick(id) }) {
                            Text("Учить")
                        }
                        TextButton(onClick = { onRepeatClick(id) }) {
                            Text("Повтор")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить карточку")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (cards.isEmpty()) {
                EmptyState()
            } else {
                CardsList(cards = cards)
            }
        }
    }

    if (showDialog) {
        NewCardDialog(
            isAutoFilling = isAutoFilling,
            onDismiss = { showDialog = false },
            onConfirm = { word, translation, definition, example ->
                viewModel.insertCard(word, translation, definition, example)
                showDialog = false
            },
            onAutoFill = { word ->
                viewModel.autoFillCard(word)
            }
        )
    }
}

@Composable
private fun CardsList(cards: List<Card>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cards, key = { it.id }) { card ->
            CardItem(card = card)
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "В этой колоде пока нет карточек.\nДобавьте первую!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CardItem(card: Card) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = card.word, style = MaterialTheme.typography.titleMedium)
            if (!card.translation.isNullOrBlank()) {
                Text(
                    text = card.translation,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!card.definition.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = card.definition,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun NewCardDialog(
    isAutoFilling: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String?, String?) -> Unit,
    onAutoFill: suspend (String) -> Triple<String?, String?, String?>
) {
    var word by remember { mutableStateOf("") }
    var translation by remember { mutableStateOf("") }
    var definition by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая карточка") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = word,
                    onValueChange = { word = it },
                    label = { Text("Слово") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (word.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        val (t, d, e) = onAutoFill(word)
                                        if (t != null) translation = t
                                        if (d != null) definition = d
                                        if (e != null) example = e
                                    }
                                },
                                enabled = !isAutoFilling
                            ) {
                                if (isAutoFilling) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(imageVector = FontAwesomeMagic, contentDescription = "Заполнить")
                                }
                            }
                        }
                    }
                )
                OutlinedTextField(
                    value = translation,
                    onValueChange = { translation = it },
                    label = { Text("Перевод") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = definition,
                    onValueChange = { definition = it },
                    label = { Text("Определение (необязательно)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = example,
                    onValueChange = { example = it },
                    label = { Text("Пример (необязательно)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (word.isNotBlank() && translation.isNotBlank()) onConfirm(word, translation, definition, example) },
                enabled = word.isNotBlank() && translation.isNotBlank() && !isAutoFilling
            ) {
                Text("Создать")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
