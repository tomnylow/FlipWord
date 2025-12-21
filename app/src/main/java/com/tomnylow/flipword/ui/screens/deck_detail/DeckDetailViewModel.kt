package com.tomnylow.flipword.ui.screens.deck_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.usecase.card.GetCardsForDeckUseCase
import com.tomnylow.flipword.domain.usecase.card.InsertCardUseCase
import com.tomnylow.flipword.domain.usecase.deck.GetDeckByIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = DeckDetailViewModel.Factory::class)
class DeckDetailViewModel @AssistedInject constructor(
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val getCardsForDeckUseCase: GetCardsForDeckUseCase,
    private val insertCardUseCase: InsertCardUseCase,
   @Assisted("deckId") private val deckId: Long
) : ViewModel() {

    private val _deck = MutableStateFlow<Deck?>(null)
    val deck = _deck.asStateFlow()

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards = _cards.asStateFlow()


    init {
        getDeckDetails()
        getCards()
    }

    private fun getDeckDetails() {
        viewModelScope.launch {
            _deck.value = getDeckByIdUseCase(deckId)
        }
    }

    private fun getCards() {
        getCardsForDeckUseCase(deckId).onEach { cards ->
                _cards.value = cards
            }
            .launchIn(viewModelScope)
    }

    fun insertCard(word: String, translation: String) {
        viewModelScope.launch {
            insertCardUseCase(Card(word = word, translation = translation, deckId = deckId))
        }
    }
    @AssistedFactory
    interface Factory {
        fun create(@Assisted("deckId") deckId: Long): DeckDetailViewModel
    }
}
