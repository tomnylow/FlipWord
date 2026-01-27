package com.tomnylow.flipword.ui.screens.decks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
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
    var dialogState by remember { mutableStateOf<DialogState>(DialogState.Hidden) }

    Scaffold(
        topBar = {
            DecksTopBar(onAddClick = { dialogState = DialogState.Create })
        }
    ) { innerPadding ->
        DecksContent(
            decks = decks,
            onDeckClick = onDeckClick,
            onDeckLongClick = { deck ->
                dialogState = DialogState.Edit(deck)
            },
            modifier = Modifier.padding(innerPadding)
        )
    }

    DecksDialogs(
        dialogState = dialogState,
        onDismiss = { dialogState = DialogState.Hidden },
        onCreateDeck = { name ->
            viewModel.insertDeck(name)
            dialogState = DialogState.Hidden
        },
        onEditDeck = { id, name ->
            viewModel.updateDeck(id, name)
            dialogState = DialogState.Hidden
        },
        onDeleteDeck = { deck ->
            viewModel.deleteDeck(deck)
            dialogState = DialogState.Hidden
        },
        onNavigateToEdit = { deck -> dialogState = DialogState.Edit(deck) },
        onNavigateToDelete = { deck -> dialogState = DialogState.Delete(deck) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DecksTopBar(onAddClick: () -> Unit) {
    TopAppBar(
        title = { Text("Колоды") },
        actions = {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Создать колоду"
                )
            }
        }
    )
}


@Composable
private fun DecksContent(
    decks: List<DeckUiModel>,
    onDeckClick: (Long) -> Unit,
    onDeckLongClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (decks.isEmpty()) {
            EmptyState()
        } else {
            DecksList(
                decks = decks,
                onDeckClick = onDeckClick,
                onDeckLongClick = onDeckLongClick
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "У вас пока нет колод.\nНажмите + чтобы создать первую!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DecksList(
    decks: List<DeckUiModel>,
    onDeckClick: (Long) -> Unit,
    onDeckLongClick: (Deck) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(decks, key = { it.deck.id }) { deckUi ->
            DeckItem(
                deckUi = deckUi,
                onClick = { onDeckClick(deckUi.deck.id) },
                onLongClick = { onDeckLongClick(deckUi.deck) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DeckItem(
    deckUi: DeckUiModel,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deckUi.deck.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${deckUi.learnedCards} / ${deckUi.totalCards}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            LinearProgressIndicator(
                progress = { deckUi.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(MaterialTheme.shapes.small),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
@Composable
private fun DecksDialogs(
    dialogState: DialogState,
    onDismiss: () -> Unit,
    onCreateDeck: (String) -> Unit,
    onEditDeck: (Long, String) -> Unit,
    onDeleteDeck: (Deck) -> Unit,
    onNavigateToEdit: (Deck) -> Unit,
    onNavigateToDelete: (Deck) -> Unit
) {
    when (dialogState) {
        is DialogState.Hidden -> Unit

        is DialogState.Create -> {
            DeckNameDialog(
                onDismiss = onDismiss,
                onConfirm = onCreateDeck
            )
        }

        is DialogState.Edit -> {
            DeckEditDialog(
                deck = dialogState.deck,
                onDismiss = onDismiss,
                onSave = { newName -> onEditDeck(dialogState.deck.id, newName) },
                onDelete = { onNavigateToDelete(dialogState.deck) }
            )
        }

        is DialogState.Delete -> {
            DeleteConfirmationDialog(
                deck = dialogState.deck,
                onDismiss = onDismiss,
                onConfirm = { onDeleteDeck(dialogState.deck) }
            )
        }
    }
}

@Composable
private fun DeckNameDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val isNameValid = name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая колода") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Название колоды") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.trim()) },
                enabled = isNameValid
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun DeckEditDialog(
    deck: Deck,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    onDelete: () -> Unit
) {
    var name by remember { mutableStateOf(deck.name) }
    val isNameValid = name.isNotBlank()
    val hasChanges = name.trim() != deck.name

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редактирование") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название колоды") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Удалить колоду")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name.trim()) },
                enabled = isNameValid && hasChanges
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
private fun DeleteConfirmationDialog(
    deck: Deck,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Удалить колоду?") },
        text = {
            Text("Колода «${deck.name}» и все карточки внутри будут удалены безвозвратно.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Удалить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

private sealed interface DialogState {
    data object Hidden : DialogState
    data object Create : DialogState
    data class Edit(val deck: Deck) : DialogState
    data class Delete(val deck: Deck) : DialogState
}
