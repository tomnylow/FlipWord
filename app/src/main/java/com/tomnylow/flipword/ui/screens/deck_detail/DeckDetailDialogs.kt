package com.tomnylow.flipword.ui.screens.deck_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tomnylow.flipword.R
import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.ui.icons.customIcons


@Composable
fun NewCardDialog(
    state: NewCardState,
    isAutoFilling: Boolean,
    onWordChange: (String) -> Unit,
    onTranslationChange: (String) -> Unit,
    onDefinitionChange: (String) -> Unit,
    onExampleChange: (String) -> Unit,
    onAutoFill: (Language, Language) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    CardDialog(
        title = stringResource(R.string.new_card_dialog_title),
        word = state.word,
        translation = state.translation,
        definition = state.definition,
        example = state.example,
        learningLanguage = state.learningLanguage,
        nativeLanguage = state.nativeLanguage,
        isAutoFilling = isAutoFilling,
        onWordChange = onWordChange,
        onTranslationChange = onTranslationChange,
        onDefinitionChange = onDefinitionChange,
        onExampleChange = onExampleChange,
        onAutoFill = onAutoFill,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        confirmButtonText = stringResource(R.string.add_button)
    )
}

@Composable
fun EditCardDialog(
    state: NewCardState,
    isAutoFilling: Boolean,
    onWordChange: (String) -> Unit,
    onTranslationChange: (String) -> Unit,
    onDefinitionChange: (String) -> Unit,
    onExampleChange: (String) -> Unit,
    onAutoFill: (Language, Language) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDelete: () -> Unit
) {
    CardDialog(
        title = stringResource(R.string.edit_card_dialog_title),
        word = state.word,
        translation = state.translation,
        definition = state.definition,
        example = state.example,
        learningLanguage = state.learningLanguage,
        nativeLanguage = state.nativeLanguage,
        isAutoFilling = isAutoFilling,
        onWordChange = onWordChange,
        onTranslationChange = onTranslationChange,
        onDefinitionChange = onDefinitionChange,
        onExampleChange = onExampleChange,
        onAutoFill = onAutoFill,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        confirmButtonText = stringResource(R.string.save_button),
        additionalContent = {
            Spacer(modifier = Modifier.height(16.dp))
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
                Text(stringResource(R.string.remove_card_button))
            }
        }
    )
}

@Composable
private fun CardDialog(
    title: String,
    word: String,
    translation: String,
    definition: String,
    example: String,
    learningLanguage: Language,
    nativeLanguage: Language,
    isAutoFilling: Boolean,
    onWordChange: (String) -> Unit,
    onTranslationChange: (String) -> Unit,
    onDefinitionChange: (String) -> Unit,
    onExampleChange: (String) -> Unit,
    onAutoFill: (Language, Language) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmButtonText: String,
    additionalContent: @Composable () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = word,
                    onValueChange = onWordChange,
                    label = {
                        Text(
                            stringResource(
                                R.string.card_word_prefix,
                                learningLanguage.displayName
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (word.isNotBlank()) {
                            IconButton(
                                onClick = { onAutoFill(learningLanguage, nativeLanguage) },
                                enabled = !isAutoFilling
                            ) {
                                if (isAutoFilling) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        modifier = Modifier.size(16.dp),
                                        imageVector = customIcons.FontAwesomeMagic,
                                        contentDescription = stringResource(
                                            R.string.autofill_button
                                        )
                                    )
                                }
                            }
                        }
                    }
                )
                OutlinedTextField(
                    value = translation,
                    onValueChange = onTranslationChange,
                    label = {
                        Text(
                            stringResource(
                                R.string.card_translation_prefix,
                                nativeLanguage.displayName
                            )
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = definition,
                    onValueChange = onDefinitionChange,
                    label = { Text(stringResource(R.string.card_definition_prefix)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = example,
                    onValueChange = onExampleChange,
                    label = { Text(stringResource(R.string.card_usages_prefix)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                additionalContent()
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = word.isNotBlank() && translation.isNotBlank() && !isAutoFilling
            ) {
                Text(confirmButtonText)
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
fun DeleteCardConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.remove_card_dialog_title)) },
        text = { Text(stringResource(R.string.remove_card_dialog_text)) },
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