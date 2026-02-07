package com.tomnylow.flipword.ui.screens.repeat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.sm2.Rating
import com.tomnylow.flipword.domain.sm2.SM2Algorithm
import com.tomnylow.flipword.domain.usecase.card.GetAllDueCardsUseCase
import com.tomnylow.flipword.domain.usecase.card.GetDueCardsForDeckUseCase
import com.tomnylow.flipword.domain.usecase.card.UpdateCardUseCase
import com.tomnylow.flipword.domain.usecase.stats.LogStudyEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RepeatViewModel @Inject constructor(
    private val getAllDueCardsUseCase: GetAllDueCardsUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val getDueCardsForDeckUseCase: GetDueCardsForDeckUseCase,
    private val logStudyEventUseCase: LogStudyEventUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val deckId: Long? = savedStateHandle.get<String>("deckId")?.toLongOrNull()

    private val _uiState = MutableStateFlow<RepeatUiState>(RepeatUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private var cardsToReview = mutableListOf<Card>()
    private var currentIndex = 0

    private var againCount = 0
    private var normalCount = 0
    private var perfectCount = 0

    init {
        loadCardsToReview()
    }

    private fun loadCardsToReview() {
        viewModelScope.launch {
            val dueCards = if (deckId == null) {
                getAllDueCardsUseCase().first()
            } else {
                getDueCardsForDeckUseCase(deckId).first()
            }
            cardsToReview.addAll(dueCards)
            if (cardsToReview.isEmpty()) {
                _uiState.value = RepeatUiState.NoCardsToRepeat
            } else {
                _uiState.value = RepeatUiState.Success(cardsToReview[currentIndex])
            }
        }
    }

    fun onRatingSelected(rating: Rating) {
        viewModelScope.launch {
            when (rating) {
                Rating.AGAIN -> againCount++
                Rating.NORMAL -> normalCount++
                Rating.PERFECT -> perfectCount++
            }

            val card = cardsToReview[currentIndex]
            val newParams = SM2Algorithm.calculateNextReview(card.sm2Params, rating)
            updateCardUseCase(card.copy(sm2Params = newParams))
            logStudyEventUseCase(card.id, card.deckId, rating.value)
            moveToNextCard()
        }
    }

    private fun moveToNextCard() {
        currentIndex++
        if (currentIndex < cardsToReview.size) {
            _uiState.value = RepeatUiState.Success(cardsToReview[currentIndex])
        } else {
            _uiState.value = RepeatUiState.SessionFinished(againCount, normalCount, perfectCount)
        }
    }
}
