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
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class RepeatViewModel @Inject constructor(
    private val getAllDueCardsUseCase: GetAllDueCardsUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val getDueCardsForDeckUseCase: GetDueCardsForDeckUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val deckId: Long? = savedStateHandle.get<String>("deckId")?.toLongOrNull()

    private val _cardsToReview = MutableStateFlow<List<Card>>(emptyList())

    private val _currentCard = MutableStateFlow<Card?>(null)
    val currentCard = _currentCard.asStateFlow()

    private var currentIndex = 0

    init {
        loadCardsToReview()
    }

    private fun loadCardsToReview() {
        viewModelScope.launch {
            _cardsToReview.value = if (deckId == null) {
                getAllDueCardsUseCase().first()
            } else {
                getDueCardsForDeckUseCase(deckId).first()
            }
            _currentCard.value = _cardsToReview.value.getOrNull(currentIndex)
        }
    }

    fun onRatingSelected(rating: Rating) {
        viewModelScope.launch {
            _currentCard.value?.let { card ->
                val newParams = SM2Algorithm.calculateNextReview(card.sm2Params, rating)
                updateCardUseCase(card.copy(sm2Params = newParams))
                moveToNextCard()
            }
        }
    }

    private fun moveToNextCard() {
        currentIndex++
        _currentCard.value = _cardsToReview.value.getOrNull(currentIndex)
    }
}
