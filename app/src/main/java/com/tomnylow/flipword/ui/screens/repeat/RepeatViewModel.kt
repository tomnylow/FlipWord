package com.tomnylow.flipword.ui.screens.repeat


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.sm2.Rating
import com.tomnylow.flipword.domain.sm2.SM2Algorithm
import com.tomnylow.flipword.domain.usecase.card.GetCardsForDeckUseCase
import com.tomnylow.flipword.domain.usecase.card.UpdateCardUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@HiltViewModel(assistedFactory = RepeatViewModel.Factory::class)
class RepeatViewModel @AssistedInject constructor(
    private val getCardsForDeckUseCase: GetCardsForDeckUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
   @Assisted("deckId") private val deckId: Long
) : ViewModel() {


    private val _cardsToReview = MutableStateFlow<List<Card>>(emptyList())

    private val _currentCard = MutableStateFlow<Card?>(null)
    val currentCard = _currentCard.asStateFlow()

    private var currentIndex = 0

    init {
        loadCardsToReview()
    }

    private fun loadCardsToReview() {
        viewModelScope.launch {
            val allCards = getCardsForDeckUseCase(deckId).first()
            _cardsToReview.value = allCards.filter { SM2Algorithm.isTimeForReview(it.sm2Params) }
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
    @AssistedFactory
    interface Factory {
        fun create(@Assisted("deckId") deckId: Long): RepeatViewModel
    }
}
