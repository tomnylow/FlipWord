package com.tomnylow.flipword.ui.screens.repeat


import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.SentimentNeutral
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.sm2.Rating

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepeatScreen(
    viewModel: RepeatViewModel = hiltViewModel(),
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
        if (currentCard == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Карточки для повторения закончились!")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                FlippableCard(
                    modifier = Modifier.weight(1f),
                    frontContent = {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = currentCard!!.word,
                            style = MaterialTheme.typography.headlineLarge,
                            maxLines = 7,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    backContent = {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = currentCard!!.translation ?: "",
                            style = MaterialTheme.typography.headlineLarge,
                            maxLines = 7,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
                RatingButtons { viewModel.onRatingSelected(it) }
            }
        }
    }
}

@Composable
fun FlippableCard(
    modifier: Modifier = Modifier,
    frontContent: @Composable () -> Unit,
    backContent: @Composable () -> Unit
) {
    var flipped by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(600),
        label = "card_rotation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 64.dp)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .clickable { flipped = !flipped }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (rotation < 90f) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    frontContent()
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = 180f },
                    contentAlignment = Alignment.Center
                ) {
                    backContent()
                }
            }
        }
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

        Icon(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onRatingSelected(Rating.AGAIN) },
            imageVector = Icons.Default.MoodBad,
            contentDescription = "Не знаю",
            tint = MaterialTheme.colorScheme.onPrimary,
        )

        Icon(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onRatingSelected(Rating.NORMAL) },
            imageVector = Icons.Default.SentimentNeutral,
            contentDescription = "Нормально",
            tint = MaterialTheme.colorScheme.onPrimary
        )

        Icon(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onRatingSelected(Rating.PERFECT) },
            imageVector = Icons.Default.Mood,
            contentDescription = "Отлично",
            tint = MaterialTheme.colorScheme.onPrimary
        )

    }
}
