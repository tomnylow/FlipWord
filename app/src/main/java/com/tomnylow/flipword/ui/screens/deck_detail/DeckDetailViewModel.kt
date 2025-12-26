package com.tomnylow.flipword.ui.screens.deck_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.usecase.card.GetCardsForDeckUseCase
import com.tomnylow.flipword.domain.usecase.card.InsertCardUseCase
import com.tomnylow.flipword.domain.usecase.deck.GetDeckByIdUseCase
import com.tomnylow.flipword.domain.usecase.external.GetDefinitionUseCase
import com.tomnylow.flipword.domain.usecase.external.GetTranslationUseCase
import com.tomnylow.flipword.domain.usecase.external.GetUsageExamplesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DeckDetailViewModel.Factory::class)
class DeckDetailViewModel @AssistedInject constructor(
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val getCardsForDeckUseCase: GetCardsForDeckUseCase,
    private val insertCardUseCase: InsertCardUseCase,
    private val getTranslationUseCase: GetTranslationUseCase,
    private val getDefinitionUseCase: GetDefinitionUseCase,
    private val getUsageExamplesUseCase: GetUsageExamplesUseCase,
    @Assisted("deckId") private val deckId: Long
) : ViewModel() {

    private val _deck = MutableStateFlow<Deck?>(null)
    val deck = _deck.asStateFlow()

    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards = _cards.asStateFlow()

    private val _newCardState = MutableStateFlow(NewCardState())
    val newCardState = _newCardState.asStateFlow()

    init {
        viewModelScope.launch {
            _deck.value = getDeckByIdUseCase(deckId)
            getCardsForDeckUseCase(deckId).onEach { cards ->
                _cards.value = cards
            }.launchIn(this)
        }
    }

    fun onWordChange(word: String) {
        _newCardState.update { it.copy(word = word) }
    }

    fun onTranslationChange(translation: String) {
        _newCardState.update { it.copy(translation = translation) }
    }

    fun onDefinitionChange(definition: String) {
        _newCardState.update { it.copy(definition = definition) }
    }

    fun onExampleChange(example: String) {
        _newCardState.update { it.copy(example = example) }
    }

    fun autoFillCard() {
        val word = _newCardState.value.word
        if (word.isBlank()) return

        viewModelScope.launch {
            _newCardState.update { it.copy(isAutoFilling = true) }
            try {
                val translationDeferred = async { getTranslationUseCase(word).getOrNull() }
                val definitionDeferred = async { getDefinitionUseCase(word).getOrNull() }
                val examplesDeferred = async { getUsageExamplesUseCase(word).getOrNull() }

                val translation = translationDeferred.await()
                val definition = definitionDeferred.await()
                val example = examplesDeferred.await()?.firstOrNull()

                _newCardState.update { state ->
                    state.copy(
                        translation = translation ?: state.translation,
                        definition = definition ?: state.definition,
                        example = example ?: state.example,
                        isAutoFilling = false
                    )
                }
            } catch (e: Exception) {
                _newCardState.update { it.copy(isAutoFilling = false) }
            }
        }
    }

    fun insertCard() {
        val state = _newCardState.value
        if (state.word.isBlank() || state.translation.isBlank()) return

        viewModelScope.launch {
            insertCardUseCase(
                Card(
                    word = state.word,
                    translation = state.translation,
                    definition = state.definition.ifBlank { null },
                    usageExample = state.example.ifBlank { null },
                    deckId = deckId
                )
            )
            _newCardState.value = NewCardState() // Сброс после добавления
        }
    }

    fun clearNewCardState() {
        _newCardState.value = NewCardState()
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("deckId") deckId: Long): DeckDetailViewModel
    }
}

data class NewCardState(
    val word: String = "",
    val translation: String = "",
    val definition: String = "",
    val example: String = "",
    val isAutoFilling: Boolean = false
)
