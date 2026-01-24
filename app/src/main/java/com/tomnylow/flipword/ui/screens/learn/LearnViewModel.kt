package com.tomnylow.flipword.ui.screens.learn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.usecase.card.GetCardsForDeckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val getCardsForDeckUseCase: GetCardsForDeckUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val deckId = savedStateHandle.get<Long>("deckId") ?: throw IllegalArgumentException("deckId is required for LearnViewModel")

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards = _cards.asStateFlow()

    init {
        getCards()
    }

    private fun getCards() {
        getCardsForDeckUseCase(deckId)
            .onEach { cards ->
                _cards.value = cards
            }
            .launchIn(viewModelScope)
    }
}
