package com.tomnylow.flipword.ui.screens.decks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.usecase.deck.DeleteDeckUseCase
import com.tomnylow.flipword.domain.usecase.deck.GetAllDecksUseCase
import com.tomnylow.flipword.domain.usecase.deck.InsertDeckUseCase
import com.tomnylow.flipword.domain.usecase.deck.UpdateDeckUseCase
import com.tomnylow.flipword.domain.usecase.stats.GetDecksProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList

@HiltViewModel
class DecksViewModel @Inject constructor(
    getAllDecksUseCase: GetAllDecksUseCase,
    private val insertDeckUseCase: InsertDeckUseCase,
    private val updateDeckUseCase: UpdateDeckUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
    getDecksProgressUseCase: GetDecksProgressUseCase,
) : ViewModel() {

    val decks = combine(
        getAllDecksUseCase(),
        getDecksProgressUseCase()
    ) { decks, stats ->
        decks.map {deck ->
            val progress = stats.find { it.deckId == deck.id }
            DeckUiModel(
                deck = deck,
                totalCards = progress?.totalCards ?: 0,
                learnedCards = progress?.learnedCards ?: 0
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun insertDeck(deckName: String) {
        viewModelScope.launch {
            insertDeckUseCase(Deck(name = deckName))
        }
    }

    fun updateDeck(id: Long, newName: String) {
        viewModelScope.launch {
            updateDeckUseCase(Deck(id, newName))
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            deleteDeckUseCase(deck)
        }
    }
}

data class DeckUiModel(
    val deck: Deck,
    val totalCards: Int = 0,
    val learnedCards: Int = 0
) {
    val progress: Float
        get() = if (totalCards > 0) learnedCards.toFloat() / totalCards else 0f
}
