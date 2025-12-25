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

    private val _isAutoFilling = MutableStateFlow(false)
    val isAutoFilling = _isAutoFilling.asStateFlow()

    init {
        viewModelScope.launch {
            _deck.value = getDeckByIdUseCase(deckId)
            getCardsForDeckUseCase(deckId).onEach { cards ->
                _cards.value = cards
            }.launchIn(this)
        }
    }

    fun insertCard(word: String, translation: String, definition: String? = null, example: String? = null) {
        viewModelScope.launch {
            insertCardUseCase(
                Card(
                    word = word,
                    translation = translation,
                    definition = definition,
                    usageExample = example,
                    deckId = deckId
                )
            )
        }
    }

    suspend fun autoFillCard(word: String): Triple<String?, String?, String?> {
        _isAutoFilling.value = true
        return try {
            val translationDeferred = viewModelScope.async { getTranslationUseCase(word).getOrNull() }
            val definitionDeferred = viewModelScope.async { getDefinitionUseCase(word).getOrNull() }
            val examplesDeferred = viewModelScope.async { getUsageExamplesUseCase(word).getOrNull() }

            Triple(
                translationDeferred.await(),
                definitionDeferred.await(),
                examplesDeferred.await()?.firstOrNull()
            )
            // TODO: Добавить тост при ошибке загрузки
        } finally {
            _isAutoFilling.value = false
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("deckId") deckId: Long): DeckDetailViewModel
    }
}
