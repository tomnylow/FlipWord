package com.tomnylow.flipword.ui.screens.decks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tomnylow.flipword.R
import com.tomnylow.flipword.domain.model.Deck


@Composable
internal fun DecksDialogs(
    dialogState: DialogState,
    onDismiss: () -> Unit,
    onCreateDeck: (String) -> Unit,
    onEditDeck: (Long, String) -> Unit,
    onDeleteDeck: (Deck) -> Unit,
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
internal fun DeckNameDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val isNameValid = name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.new_deck_dialog_title)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.deck_name_prefix)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(name.trim()) },
                enabled = isNameValid
            ) {
                Text(stringResource(R.string.add_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}

@Composable
internal fun DeckEditDialog(
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
        title = { Text(stringResource(R.string.edit_deck_dialog_title)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(R.string.deck_name_prefix)) },
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
                    Text(stringResource(R.string.remove_button))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name.trim()) },
                enabled = isNameValid && hasChanges
            ) {
                Text(stringResource(R.string.save_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}

@Composable
internal fun DeleteConfirmationDialog(
    deck: Deck,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.remove_deck_dialog_title)) },
        text = {
            Text(stringResource(R.string.remove_deck_dialog_text, deck.name))
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.remove_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}

internal sealed interface DialogState {
    data object Hidden : DialogState
    data object Create : DialogState
    data class Edit(val deck: Deck) : DialogState
    data class Delete(val deck: Deck) : DialogState
}