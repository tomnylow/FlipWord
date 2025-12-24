package com.tomnylow.flipword.ui.screens.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.usecase.deck.GetAllDecksUseCase
import com.tomnylow.flipword.domain.usecase.deck.InsertDeckUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val getAllDecksUseCase: GetAllDecksUseCase,
    private val insertDeckUseCase: InsertDeckUseCase
) : ViewModel() {

    private val _decks = MutableStateFlow<List<Deck>>(emptyList())
    val decks = _decks.asStateFlow()

    init {
        getDecks()
    }

    private fun getDecks() {
        getAllDecksUseCase()
            .onEach { decks ->
                _decks.value = decks
            }
            .launchIn(viewModelScope)
    }

    fun insertDeck(deckName: String) {
        viewModelScope.launch {
            insertDeckUseCase(Deck(name = deckName))
        }
    }
}
