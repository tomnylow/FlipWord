package com.tomnylow.flipword.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.model.GlobalStatistics
import com.tomnylow.flipword.domain.usecase.card.GetAllDueCardsUseCase
import com.tomnylow.flipword.domain.usecase.stats.GetGlobalStatsUseCase
import com.tomnylow.flipword.domain.usecase.user.GetUserUseCase
import com.tomnylow.flipword.domain.usecase.word.GetWordOfTheDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getGlobalStatsUseCase: GetGlobalStatsUseCase,
    getWordOfTheDayUseCase: GetWordOfTheDayUseCase,
    getAllDueCardsUseCase: GetAllDueCardsUseCase,
    getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val wordOfTheDayFlow = flow {
        emit(getWordOfTheDayUseCase())
    }

    val state: StateFlow<HomeState> = combine(
        getGlobalStatsUseCase(),
        getAllDueCardsUseCase(),
        getUserUseCase(),
        wordOfTheDayFlow
    ) { stats, dueCards, user, (word, definition) ->

        HomeState(
            username = formatUsername(user?.displayName),
            stats = stats,
            wordOfTheDay = word,
            definitionOfTheDay = definition,
            dueCards = dueCards
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeState()
    )

    private fun formatUsername(name: String?): String {
        return if (!name.isNullOrBlank()) ", $name" else ""
    }
}

data class HomeState(
    val username: String = "",
    val stats: GlobalStatistics? = null,
    val wordOfTheDay: String = "",
    val definitionOfTheDay: String? = null,
    val dueCards: List<Card> = emptyList()
)