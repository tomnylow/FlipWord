package com.tomnylow.flipword.ui.screens.repeat


import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.sm2.Rating

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatScreen(
    deckId: Long,
    viewModel: RepeatViewModel = hiltViewModel(creationCallback = {
        factory: RepeatViewModel.Factory ->
        factory.create(deckId)
    }),
    onNavigateBack: () -> Unit
) {
    val currentCard by viewModel.currentCard.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Повторение") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (currentCard == null) {
                // Session complete or no cards to review
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Карточки для повторения закончились!")
                }
            } else {
                RepeatCard(card = currentCard!!, onRatingSelected = viewModel::onRatingSelected)
            }
        }
    }
}

@Composable
fun RepeatCard(card: Card, onRatingSelected: (Rating) -> Unit) {
    var flipped by remember { mutableStateOf(false) }


    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 64.dp)
                .clickable{ flipped = !flipped }

        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (!flipped) {
                    Text(text = card.word, style = MaterialTheme.typography.headlineLarge)
                } else {
                    Text(
                        text = card.translation ?: "",
                        style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }

        RatingButtons(onRatingSelected)

    }
}

@Composable
private fun RatingButtons(onRatingSelected: (Rating) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { onRatingSelected(Rating.AGAIN) }) { Text("Не знаю") }
        Button(onClick = { onRatingSelected(Rating.NORMAL) }) { Text("Нормально") }
        Button(onClick = { onRatingSelected(Rating.PERFECT) }) { Text("Отлично") }
    }
}
