package com.tomnylow.flipword.ui.screens.repeat


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.MoodBad
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

import com.tomnylow.flipword.R
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.sm2.Rating


@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun RepeatScreen(
    onNavigateBack: () -> Unit,
    onFinishSessionClick: () -> Unit
) {
    var repetition by remember { mutableIntStateOf(0) }
    val viewModel: RepeatViewModel = hiltViewModel(key = "Repeat $repetition")
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.repeat_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back_description)
                        )
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            RepeatUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is RepeatUiState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    AnimatedContent(
                        modifier = Modifier.weight(1f),
                        targetState = state.card,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(300)).togetherWith(
                                fadeOut(
                                    animationSpec = tween(300)
                                ) + slideOutHorizontally(animationSpec = tween(300)) { -it })
                        },
                        label = "card_transition"
                    ) { card ->
                        FlippableCard(
                            frontContent = {
                                Text(
                                    modifier = Modifier.padding(16.dp),
                                    text = card.word,
                                    style = MaterialTheme.typography.headlineLarge,
                                    maxLines = 7,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            },
                            backContent = {
                                Text(
                                    modifier = Modifier.padding(16.dp),
                                    text = card.translation ?: "",
                                    style = MaterialTheme.typography.headlineLarge,
                                    maxLines = 7,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                    RatingButtons { viewModel.onRatingSelected(it) }
                }
            }

            is RepeatUiState.SessionFinished -> {
                SessionStats(
                    modifier = Modifier.padding(padding),
                    againCount = state.againCount,
                    normalCount = state.normalCount,
                    perfectCount = state.perfectCount,
                    onContinueRepetitionClick = { repetition++ },
                    onFinishSessionClick = onFinishSessionClick,
                    isContinueEnabled = state.againCount > 0
                )
            }

            RepeatUiState.NoCardsToRepeat -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.no_cards_to_repeat_text),
                        textAlign = TextAlign.Center
                    )
                }
            }

        }
    }
}

@Composable
fun SessionStats(
    modifier: Modifier = Modifier,
    againCount: Int,
    normalCount: Int,
    perfectCount: Int,
    onContinueRepetitionClick: () -> Unit,
    onFinishSessionClick: () -> Unit,
    isContinueEnabled: Boolean
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.repeat_finished_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.repeat_finished_stats),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.MoodBad,
                contentDescription = stringResource(R.string.dont_know_description),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.repeat_dont_know_stats, againCount),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.SentimentNeutral,
                contentDescription = stringResource(R.string.normal_description),
                tint = MaterialTheme.colorScheme.tertiary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.repeat_normal_stats, normalCount),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Mood,
                contentDescription = stringResource(R.string.perfect_description),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.repeat_perfect_stats, perfectCount),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onContinueRepetitionClick,
            enabled = isContinueEnabled
        ) {
            Text(stringResource(R.string.continue_repeat))
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onFinishSessionClick) {
            Text(stringResource(R.string.finish_repeat))
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
            .padding(horizontal = 24.dp, vertical = 32.dp)
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
private fun RatingButtons(modifier: Modifier = Modifier, onRatingSelected: (Rating) -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Icon(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.error)
                .clickable { onRatingSelected(Rating.AGAIN) },
            imageVector = Icons.Default.MoodBad,
            contentDescription = stringResource(R.string.dont_know_description),
            tint = MaterialTheme.colorScheme.onPrimary,
        )

        Icon(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiary)
                .clickable { onRatingSelected(Rating.NORMAL) },
            imageVector = Icons.Default.SentimentNeutral,
            contentDescription = stringResource(R.string.normal_description),
            tint = MaterialTheme.colorScheme.onPrimary
        )

        Icon(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onRatingSelected(Rating.PERFECT) },
            imageVector = Icons.Default.Mood,
            contentDescription = stringResource(R.string.perfect_description),
            tint = MaterialTheme.colorScheme.onPrimary
        )

    }
}

sealed interface RepeatUiState {
    object Loading : RepeatUiState
    data class Success(val card: Card) : RepeatUiState
    data class SessionFinished(val againCount: Int, val normalCount: Int, val perfectCount: Int) :
        RepeatUiState

    object NoCardsToRepeat : RepeatUiState
}