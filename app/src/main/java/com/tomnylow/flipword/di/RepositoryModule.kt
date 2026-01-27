package com.tomnylow.flipword.di

import com.tomnylow.flipword.data.repository.AuthRepositoryImpl
import com.tomnylow.flipword.data.repository.CardRepositoryImpl
import com.tomnylow.flipword.data.repository.DeckRepositoryImpl
import com.tomnylow.flipword.data.repository.ExternalWordRepositoryImpl
import com.tomnylow.flipword.data.repository.SessionRepositoryImpl
import com.tomnylow.flipword.data.repository.SettingsRepositoryImpl
import com.tomnylow.flipword.data.repository.StatisticsRepositoryImpl
import com.tomnylow.flipword.domain.repository.AuthRepository
import com.tomnylow.flipword.domain.repository.CardRepository
import com.tomnylow.flipword.domain.repository.DeckRepository
import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import com.tomnylow.flipword.domain.repository.SessionRepository
import com.tomnylow.flipword.domain.repository.SettingsRepository
import com.tomnylow.flipword.domain.repository.StatisticsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryTest: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDeckRepository(
        deckRepositoryImpl: DeckRepositoryImpl
    ): DeckRepository

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        cardRepositoryImpl: CardRepositoryImpl
    ): CardRepository

    @Binds
    @Singleton
    abstract fun bindExternalWordRepository(
        externalWordRepositoryImpl: ExternalWordRepositoryImpl
    ): ExternalWordRepository


    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindStatsRepository(
        statisticsRepositoryImpl: StatisticsRepositoryImpl
    ): StatisticsRepository
}
