package com.tomnylow.flipword.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.model.GlobalStatistics
import com.tomnylow.flipword.domain.model.User
import com.tomnylow.flipword.domain.usecase.card.AddWordOfTheDayToDeckUseCase
import com.tomnylow.flipword.domain.usecase.card.GetAllDueCardsUseCase
import com.tomnylow.flipword.domain.usecase.deck.GetAllDecksUseCase
import com.tomnylow.flipword.domain.usecase.stats.GetGlobalStatsUseCase
import com.tomnylow.flipword.domain.usecase.user.GetUserUseCase
import com.tomnylow.flipword.domain.usecase.word.GetWordOfTheDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getGlobalStatsUseCase: GetGlobalStatsUseCase,
    getWordOfTheDayUseCase: GetWordOfTheDayUseCase,
    getAllDueCardsUseCase: GetAllDueCardsUseCase,
    getUserUseCase: GetUserUseCase,
    getAllDecksUseCase: GetAllDecksUseCase,
    private val addWordOfTheDayToDeckUseCase: AddWordOfTheDayToDeckUseCase
) : ViewModel() {

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    private val wordOfTheDayFlow = flow {
        emit(getWordOfTheDayUseCase())
    }

    private val _showDeckSelectionDialog = MutableStateFlow(false)

    val state: StateFlow<HomeState> = combine(
        getGlobalStatsUseCase(),
        getAllDueCardsUseCase(),
        getUserUseCase(),
        wordOfTheDayFlow,
        getAllDecksUseCase(),
        _showDeckSelectionDialog
    ) { flows ->
        val stats = flows[0] as GlobalStatistics?
        val dueCards = flows[1] as List<Card>
        val user = flows[2] as User?
        val (word, definition) = flows[3] as Pair<String, String?>
        val decks = flows[4] as List<Deck>
        val showDialog = flows[5] as Boolean

        HomeState(
            username = formatUsername(user?.displayName),
            stats = stats,
            wordOfTheDay = word,
            definitionOfTheDay = definition,
            dueCards = dueCards,
            decks = decks,
            showDeckSelectionDialog = showDialog
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState()
    )

    fun onAddWordOfTheDayClick() {
        _showDeckSelectionDialog.value = true
    }

    fun onDismissDeckSelectionDialog() {
        _showDeckSelectionDialog.value = false
    }

    fun onDeckSelected(deckId: Long) {
        viewModelScope.launch {
            addWordOfTheDayToDeckUseCase(deckId)
            _showDeckSelectionDialog.value = false
            _snackbarMessage.emit("Слово добавлено в вашу колоду")
        }
    }

    private fun formatUsername(name: String?): String {
        return if (!name.isNullOrBlank()) ", $name" else ""
    }
}

data class HomeState(
    val username: String = "",
    val stats: GlobalStatistics? = null,
    val wordOfTheDay: String = "",
    val definitionOfTheDay: String? = null,
    val dueCards: List<Card> = emptyList(),
    val decks: List<Deck> = emptyList(),
    val showDeckSelectionDialog: Boolean = false
)
