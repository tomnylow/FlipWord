package com.tomnylow.flipword.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tomnylow.flipword.R
import com.tomnylow.flipword.domain.model.Deck

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRepeatWordsClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val totalLearned = state.stats?.totalLearnedWords ?: 0
    val accuracy = state.stats?.accuracyPercentage?.toInt() ?: 0
    val streak = state.stats?.dayStreak ?: 0

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    if (state.showDeckSelectionDialog) {
        DeckSelectionDialog(
            decks = state.decks,
            onDismiss = viewModel::onDismissDeckSelectionDialog,
            onDeckSelected = viewModel::onDeckSelected
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Привет" + state.username) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            HomeStatCard(
                title = "Серия дней",
                value = "$streak",
                valueStyle = MaterialTheme.typography.displayLarge
            )

            WordOfTheDayCard(
                wordOfTheDay = state.wordOfTheDay,
                definitionOfTheDay = state.definitionOfTheDay,
                onAddWordOfTheDayClick =  viewModel::onAddWordOfTheDayClick
            )

            RepeatCard(
                count = state.dueCards.size,
                onClick = onRepeatWordsClick
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                HomeStatCard(
                    title = "Изучено слов",
                    value = "$totalLearned",
                    modifier = Modifier.weight(1f)
                )

                HomeStatCard(
                    title = "Точность",
                    value = "$accuracy%",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DeckSelectionDialog(
    decks: List<Deck>,
    onDismiss: () -> Unit,
    onDeckSelected: (Long) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите колоду") },
        text = {
            LazyColumn {
                items(decks) { deck ->
                    Text(
                        text = deck.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDeckSelected(deck.id) }
                            .padding(16.dp)
                    )
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun WordOfTheDayCard(
    wordOfTheDay: String,
    definitionOfTheDay: String?,
    onAddWordOfTheDayClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Слово дня",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = wordOfTheDay,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            Text(
                text = definitionOfTheDay
                    ?: stringResource(R.string.no_definitions_found),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onAddWordOfTheDayClick,
                enabled = definitionOfTheDay != null
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text("Добавить")
            }
        }
    }
}

@Composable
private fun RepeatCard(count: Int, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (count > 0) {
                Text(
                    text = "Слов на повторение",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Нажмите, чтобы начать урок",
                    style = MaterialTheme.typography.labelLarge
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AssignmentTurnedIn,
                    contentDescription = null
                )
                Text(
                    text = "Все слова повторены!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun HomeStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    valueStyle: TextStyle = MaterialTheme.typography.headlineMedium
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = value,
                style = valueStyle,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}