package com.tomnylow.flipword.ui.screens.deck_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.domain.usecase.card.GetCardsForDeckUseCase
import com.tomnylow.flipword.domain.usecase.card.InsertCardUseCase
import com.tomnylow.flipword.domain.usecase.deck.GetDeckByIdUseCase
import com.tomnylow.flipword.domain.usecase.external.GetDictionaryDataUseCase
import com.tomnylow.flipword.domain.usecase.external.GetTranslationUseCase
import com.tomnylow.flipword.domain.usecase.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

@HiltViewModel
class DeckDetailViewModel @Inject constructor(
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val getCardsForDeckUseCase: GetCardsForDeckUseCase,
    private val insertCardUseCase: InsertCardUseCase,
    private val getTranslationUseCase: GetTranslationUseCase,
    private val getDictionaryDataUseCase: GetDictionaryDataUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val deckId: Long = savedStateHandle.get<Long>("deckId")!!

    private val _state = MutableStateFlow(DeckDetailState())
    val state = _state.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    private var autoFillJob: Job? = null

    init {
        loadDeckAndCards()
    }

    private fun loadDeckAndCards() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val deck = getDeckByIdUseCase(deckId)
                val settings = getSettingsUseCase().first()

                getCardsForDeckUseCase(deckId).onEach { cards ->
                    _state.update { state ->
                        state.copy(
                            deck = deck,
                            cards = cards,
                            newCard = NewCardState(
                                nativeLanguage = settings.nativeLanguage,
                                learningLanguage = settings.learningLanguage
                            ),
                            isLoading = false
                        )
                    }
                }.launchIn(this)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
                _snackbarMessage.emit("Ошибка загрузки колоды")
            }
        }
    }

    fun onWordChange(word: String) {
        _state.update { it.copy(newCard = it.newCard.copy(word = word)) }
    }

    fun onTranslationChange(translation: String) {
        _state.update { it.copy(newCard = it.newCard.copy(translation = translation)) }
    }

    fun onDefinitionChange(definition: String) {
        _state.update { it.copy(newCard = it.newCard.copy(definition = definition)) }
    }

    fun onExampleChange(example: String) {
        _state.update { it.copy(newCard = it.newCard.copy(example = example)) }
    }

    fun autoFillCard(learningLanguage: Language, nativeLanguage: Language) {
        val word = _state.value.newCard.word
        if (word.isBlank()) return

        autoFillJob?.cancel()
        autoFillJob = viewModelScope.launch {
            _state.update { it.copy(isAutoFilling = true) }

            var translation: String? = null
            var definition: String? = null
            var example: String? = null

            var translationSuccess = false
            var dictionarySuccess = false

            translation = withTimeoutOrNull(8000L) {
                getTranslationUseCase(word, learningLanguage.code, nativeLanguage.code)
            }?.also { translationSuccess = true }

            withTimeoutOrNull(8000L) {
                getDictionaryDataUseCase(word, learningLanguage)
            }?.let {
                definition = it.definition
                example = it.example
                dictionarySuccess = true
            }

            _state.update { state ->
                state.copy(
                    newCard = state.newCard.copy(
                        translation = translation ?: state.newCard.translation,
                        definition = definition ?: state.newCard.definition,
                        example = example ?: state.newCard.example
                    ),
                    isAutoFilling = false
                )
            }

            when {
                !translationSuccess && !dictionarySuccess -> {
                    _snackbarMessage.emit("Не удалось получить данные: проверьте подключение")
                }

                !translationSuccess -> {
                    _snackbarMessage.emit("Не удалось получить перевод")
                }

                !dictionarySuccess -> {
                    _snackbarMessage.emit("Не удалось получить определение или пример")
                }
            }
        }
    }

    fun insertCard() {
        val card = _state.value.newCard
        if (card.word.isBlank() || card.translation.isBlank()) return

        viewModelScope.launch {
            try {
                insertCardUseCase(
                    Card(
                        word = card.word,
                        translation = card.translation,
                        definition = card.definition.ifBlank { null },
                        usageExample = card.example.ifBlank { null },
                        deckId = deckId
                    )
                )
                clearNewCardState()
            } catch (e: Exception) {
                _snackbarMessage.emit("Ошибка сохранения карточки")
            }
        }
    }

    fun clearNewCardState() {
        autoFillJob?.cancel()
        autoFillJob = null
        _state.update { it.copy(newCard = NewCardState(learningLanguage = it.newCard.learningLanguage, nativeLanguage = it.newCard.nativeLanguage)) }
    }
}

data class DeckDetailState(
    val deck: Deck? = null,
    val cards: List<Card> = emptyList(),
    val newCard: NewCardState = NewCardState(),
    val isLoading: Boolean = false,
    val isAutoFilling: Boolean = false
)

data class NewCardState(
    val word: String = "",
    val translation: String = "",
    val definition: String = "",
    val example: String = "",
    val nativeLanguage: Language = Language.RUSSIAN,
    val learningLanguage: Language = Language.ENGLISH,
)