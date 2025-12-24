package com.tomnylow.flipword.ui.screens.learn

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.usecase.card.GetCardsForDeckUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel(assistedFactory = LearnViewModel.Factory::class)
class LearnViewModel @AssistedInject constructor(
    private val getCardsForDeckUseCase: GetCardsForDeckUseCase,
    @Assisted private val deckId: Long
) : ViewModel() {


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
    @AssistedFactory
    interface Factory {
        fun create(deckId: Long): LearnViewModel
    }
}
