package com.tomnylow.flipword.ui.screens.decks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
            TopAppBar(
                title = { Text("Колоды") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { dialogState = DialogState.Create }
            ) { Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Создать колоду"
            )}
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
        onNavigateToDelete = { deck -> dialogState = DialogState.Delete(deck) }
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
            text = "У вас пока нет колод.\nНажмите + чтобы создать первую",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
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
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deckUi.deck.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${deckUi.learnedCards} / ${deckUi.totalCards}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { deckUi.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(MaterialTheme.shapes.small),
                trackColor = MaterialTheme.colorScheme.secondaryContainer,
            )
        }
    }
}

