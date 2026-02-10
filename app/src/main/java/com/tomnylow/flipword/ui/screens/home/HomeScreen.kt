package com.tomnylow.flipword.ui.screens.home

import android.annotation.SuppressLint
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tomnylow.flipword.R
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.model.Deck

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRepeatWordsClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    val totalLearned = state.stats?.totalLearnedWords ?: 0
    val accuracy = state.stats?.accuracyPercentage?.toInt() ?: 0
    val streak = state.stats?.dayStreak ?: 0

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { resId ->
            snackbarHostState.showSnackbar(context.getString(resId))
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
            TopAppBar(title = {
                Text(stringResource(R.string.home_screen_greeting) + (state.username?.let { ", $it" }
                    ?: ""))
            })
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
                title = stringResource(R.string.streake_title),
                value = "$streak",
                valueStyle = MaterialTheme.typography.displayLarge
            )

            WordOfTheDayCard(
                card = state.wordOfTheDayCard,
                onAddWordOfTheDayClick = viewModel::onAddWordOfTheDayClick
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
                    title = stringResource(R.string.words_learned_stat),
                    value = "$totalLearned",
                    modifier = Modifier.weight(1f)
                )

                HomeStatCard(
                    title = stringResource(R.string.accuracy_stat),
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
        title = { Text(stringResource(R.string.add_word_of_the_day_dialog_title)) },
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
    card: Card?,
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
                text = stringResource(R.string.word_of_the_day_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            if (card == null) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = card.word,
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = card.translation ?: stringResource(R.string.no_translation),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.secondary
                )
                card.definition?.let {
                    Text(
                        text = card.definition,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onAddWordOfTheDayClick,
                    enabled = card.translation != null
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.padding(start = 8.dp))
                    Text(stringResource(R.string.add_button))
                }
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
                    text = stringResource(R.string.home_repeat_button_title),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.home_repeat_button_message),
                    style = MaterialTheme.typography.labelLarge
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AssignmentTurnedIn,
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.home_repeat_no_words),
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
