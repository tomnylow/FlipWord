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
import androidx.compose.ui.unit.dp
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.ui.icons.FontAwesomeMagic

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
        title = "Новая карточка",
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
        confirmButtonText = "Создать"
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
        title = "Редактировать карточку",
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
        confirmButtonText = "Сохранить",
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
                Text("Удалить карточку")
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
                    label = { Text("Слово (${learningLanguage.displayName})") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (word.isNotBlank()) {
                            IconButton(
                                onClick = { onAutoFill(learningLanguage, nativeLanguage) },
                                enabled = !isAutoFilling
                            ) {
                                if (isAutoFilling) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(modifier = Modifier.size(16.dp), imageVector = FontAwesomeMagic, contentDescription = "Заполнить")
                                }
                            }
                        }
                    }
                )
                OutlinedTextField(
                    value = translation,
                    onValueChange = onTranslationChange,
                    label = { Text("Перевод (${nativeLanguage.displayName})") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = definition,
                    onValueChange = onDefinitionChange,
                    label = { Text("Определение (необязательно)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                OutlinedTextField(
                    value = example,
                    onValueChange = onExampleChange,
                    label = { Text("Пример (необязательно)") },
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
                Text("Отмена")
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
        title = { Text("Удалить карточку?") },
        text = { Text("Эта карточка будет удалена безвозвратно.") },
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