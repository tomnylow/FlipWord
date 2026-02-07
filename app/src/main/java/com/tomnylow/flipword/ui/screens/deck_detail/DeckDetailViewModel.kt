package com.tomnylow.flipword.ui.screens.deck_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.model.Language
import com.tomnylow.flipword.domain.usecase.card.DeleteCardUseCase
import com.tomnylow.flipword.domain.usecase.card.GetCardsForDeckUseCase
import com.tomnylow.flipword.domain.usecase.card.InsertCardUseCase
import com.tomnylow.flipword.domain.usecase.card.UpdateCardUseCase
import com.tomnylow.flipword.domain.usecase.deck.GetDeckByIdUseCase
import com.tomnylow.flipword.domain.usecase.external.GetDictionaryDataUseCase
import com.tomnylow.flipword.domain.usecase.external.GetTranslationUseCase
import com.tomnylow.flipword.domain.usecase.settings.GetSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckDetailViewModel @Inject constructor(
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val getCardsForDeckUseCase: GetCardsForDeckUseCase,
    private val insertCardUseCase: InsertCardUseCase,
    private val updateCardUseCase: UpdateCardUseCase,
    private val deleteCardUseCase: DeleteCardUseCase,
    private val getTranslationUseCase: GetTranslationUseCase,
    private val getDictionaryDataUseCase: GetDictionaryDataUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val deckId: Long = savedStateHandle.get<Long>("deckId") ?: throw IllegalArgumentException("deckId is required for DeckDetailViewModel")

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

    fun onShowCreateDialog() {
        _state.update { it.copy(dialogState = DialogState.Create) }
    }

    fun onShowEditDialog(card: Card) {
        _state.update { state ->
            state.copy(
                dialogState = DialogState.Edit(card),
                newCard = state.newCard.copy(
                    word = card.word,
                    translation = card.translation ?: "",
                    definition = card.definition ?: "",
                    example = card.usageExample ?: ""
                )
            )
        }
    }

    fun onShowDeleteDialog(card: Card) {
        _state.update { it.copy(dialogState = DialogState.Delete(card)) }
    }

    fun onDismissDialog() {
        clearNewCardState()
        _state.update { it.copy(dialogState = DialogState.Hidden) }
    }

    fun autoFillCard(learningLanguage: Language, nativeLanguage: Language) {
        val word = _state.value.newCard.word
        if (word.isBlank()) return

        autoFillJob?.cancel()
        autoFillJob = viewModelScope.launch {
            _state.update { it.copy(isAutoFilling = true) }

            val translationDeferred = async { getTranslationUseCase(word, learningLanguage.code, nativeLanguage.code) }
            val dictionaryDeferred = async { getDictionaryDataUseCase(word, learningLanguage) }

            val translation = translationDeferred.await()
            val dictionaryData = dictionaryDeferred.await()

            _state.update { state ->
                state.copy(
                    newCard = state.newCard.copy(
                        translation = translation ?: state.newCard.translation,
                        definition = dictionaryData?.definition ?: state.newCard.definition,
                        example = dictionaryData?.example ?: state.newCard.example
                    ),
                    isAutoFilling = false
                )
            }

            when {
                translation == null && dictionaryData == null -> {
                    _snackbarMessage.emit("Не удалось получить данные: проверьте подключение")
                }
                translation == null -> {
                    _snackbarMessage.emit("Не удалось получить перевод")
                }
                dictionaryData == null -> {
                    _snackbarMessage.emit("Не удалось получить определение или пример")
                }
            }
        }
    }

    fun processCard() {
        when (val dialog = _state.value.dialogState) {
            is DialogState.Create -> insertCard()
            is DialogState.Edit -> updateCard(dialog.card)
            else -> Unit
        }
        onDismissDialog()
    }

    private fun insertCard() {
        val cardState = _state.value.newCard
        if (cardState.word.isBlank() || cardState.translation.isBlank()) return

        viewModelScope.launch {
            try {
                insertCardUseCase(
                    Card(
                        word = cardState.word,
                        translation = cardState.translation,
                        definition = cardState.definition.ifBlank { null },
                        usageExample = cardState.example.ifBlank { null },
                        deckId = deckId
                    )
                )
            } catch (e: Exception) {
                _snackbarMessage.emit("Ошибка сохранения карточки")
            }
        }
    }

    private fun updateCard(card: Card) {
        val cardState = _state.value.newCard
        if (cardState.word.isBlank() || cardState.translation.isBlank()) return

        viewModelScope.launch {
            try {
                updateCardUseCase(
                    card.copy(
                        word = cardState.word,
                        translation = cardState.translation,
                        definition = cardState.definition.ifBlank { null },
                        usageExample = cardState.example.ifBlank { null },
                    )
                )
            } catch (e: Exception) {
                _snackbarMessage.emit("Ошибка обновления карточки")
            }
        }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch {
            try {
                deleteCardUseCase(card)
            } catch (e: Exception) {
                _snackbarMessage.emit("Ошибка удаления карточки")
            }
        }
        onDismissDialog()
    }

    private fun clearNewCardState() {
        autoFillJob?.cancel()
        autoFillJob = null
        _state.update { it.copy(
            newCard = NewCardState(learningLanguage = it.newCard.learningLanguage, nativeLanguage = it.newCard.nativeLanguage),
            isAutoFilling = false
        ) }
    }
}

data class DeckDetailState(
    val deck: Deck? = null,
    val cards: List<Card> = emptyList(),
    val newCard: NewCardState = NewCardState(),
    val isLoading: Boolean = false,
    val isAutoFilling: Boolean = false,
    val dialogState: DialogState = DialogState.Hidden
)

data class NewCardState(
    val word: String = "",
    val translation: String = "",
    val definition: String = "",
    val example: String = "",
    val nativeLanguage: Language = Language.RUSSIAN,
    val learningLanguage: Language = Language.ENGLISH,
)

sealed interface DialogState {
    data object Hidden : DialogState
    data object Create : DialogState
    data class Edit(val card: Card) : DialogState
    data class Delete(val card: Card) : DialogState
}