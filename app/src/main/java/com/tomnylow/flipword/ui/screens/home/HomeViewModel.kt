@file:OptIn(ExperimentalCoroutinesApi::class)

package com.tomnylow.flipword.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tomnylow.flipword.R
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.usecase.card.GetAllDueCardsUseCase
import com.tomnylow.flipword.domain.usecase.card.GetDueCardsForDeckUseCase
import com.tomnylow.flipword.domain.usecase.deck.GetAllDecksUseCase
import com.tomnylow.flipword.domain.usecase.user.GetDailyStreakUseCase
import com.tomnylow.flipword.domain.usecase.word.GetWordOfTheDayUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
// Убедитесь, что этот импорт добавлен
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.flowOf

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDailyStreakUseCase: GetDailyStreakUseCase,
    private val getWordOfTheDayUseCase: GetWordOfTheDayUseCase,
    private val getAllDueCardsUseCase: GetAllDueCardsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        observeDailyStreak()
        getWordOfTheDay()
        observeDueCards()
    }
    private fun observeDailyStreak() {
        getDailyStreakUseCase().onEach { streak ->
            _state.update {
                it.copy(dailyStreak = streak)
            }
        }.launchIn(viewModelScope)
    }
    private fun getWordOfTheDay() {
        viewModelScope.launch {
            _state.update {
                val wordOfTheDay = getWordOfTheDayUseCase()
                it.copy(wordOfTheDay = wordOfTheDay.first, difinitionOfTheDay = wordOfTheDay.second)
            }
        }
    }
    private fun observeDueCards() {
        getAllDueCardsUseCase().onEach { dueCards ->
            _state.update { it.copy(dueCards = dueCards) }
        }.launchIn(viewModelScope)
    }
}
data class HomeState(
    val dailyStreak: Int = 0,
    val wordOfTheDay: String = "",
    val difinitionOfTheDay: String? = null,
    val dueCards: List<Card> = emptyList()
)
