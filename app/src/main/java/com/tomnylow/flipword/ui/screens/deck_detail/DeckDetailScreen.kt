package com.tomnylow.flipword.ui.screens.deck_detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.model.Language
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailScreen(
    viewModel: DeckDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onLearnClick: (Long) -> Unit,
    onRepeatClick: (Long) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.deck?.name ?: "Загрузка...",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    state.deck?.id?.let { id ->
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
            FloatingActionButton(onClick = viewModel::onShowCreateDialog) {
                Icon(Icons.Default.Add, contentDescription = "Добавить карточку")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                state.cards.isEmpty() -> EmptyState()
                else -> CardsList(
                    cards = state.cards,
                    onCardLongClick = viewModel::onShowEditDialog
                )
            }
        }
    }

    DeckDetailDialogs(
        dialogState = state.dialogState,
        newCardState = state.newCard,
        isAutoFilling = state.isAutoFilling,
        onWordChange = viewModel::onWordChange,
        onTranslationChange = viewModel::onTranslationChange,
        onDefinitionChange = viewModel::onDefinitionChange,
        onExampleChange = viewModel::onExampleChange,
        onAutoFill = viewModel::autoFillCard,
        onDismiss = viewModel::onDismissDialog,
        onConfirm = viewModel::processCard,
        onNavigateToDelete = viewModel::onShowDeleteDialog,
        onConfirmDelete = {
            (state.dialogState as? DialogState.Delete)?.card?.let(viewModel::deleteCard)
        }
    )
}

@Composable
private fun DeckDetailDialogs(
    dialogState: DialogState,
    newCardState: NewCardState,
    isAutoFilling: Boolean,
    onWordChange: (String) -> Unit,
    onTranslationChange: (String) -> Unit,
    onDefinitionChange: (String) -> Unit,
    onExampleChange: (String) -> Unit,
    onAutoFill: (Language, Language) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onNavigateToDelete: (Card) -> Unit,
    onConfirmDelete: () -> Unit
) {
    when (dialogState) {
        is DialogState.Hidden -> Unit
        is DialogState.Create -> {
            NewCardDialog(
                state = newCardState,
                isAutoFilling = isAutoFilling,
                onWordChange = onWordChange,
                onTranslationChange = onTranslationChange,
                onDefinitionChange = onDefinitionChange,
                onExampleChange = onExampleChange,
                onAutoFill = onAutoFill,
                onDismiss = onDismiss,
                onConfirm = onConfirm
            )
        }

        is DialogState.Edit -> {
            EditCardDialog(
                state = newCardState,
                isAutoFilling = isAutoFilling,
                onWordChange = onWordChange,
                onTranslationChange = onTranslationChange,
                onDefinitionChange = onDefinitionChange,
                onExampleChange = onExampleChange,
                onAutoFill = onAutoFill,
                onDismiss = onDismiss,
                onConfirm = onConfirm,
                onDelete = { onNavigateToDelete(dialogState.card) }
            )
        }

        is DialogState.Delete -> {
            DeleteCardConfirmationDialog(
                onDismiss = onDismiss,
                onConfirm = onConfirmDelete
            )
        }
    }
}

@Composable
private fun CardsList(
    cards: List<Card>,
    onCardLongClick: (Card) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cards, key = { it.id }) { card ->
            CardItem(
                card = card,
                onLongClick = { onCardLongClick(card) }
            )
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardItem(card: Card, onLongClick: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .animateContentSize()
            .combinedClickable(
                onClick = { isExpanded = !isExpanded },
                onLongClick = onLongClick
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = card.word, style = MaterialTheme.typography.titleMedium)
            
            card.translation?.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            card.definition?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            card.usageExample?.takeIf { it.isNotBlank() }?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
