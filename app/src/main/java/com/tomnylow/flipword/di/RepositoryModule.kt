package com.tomnylow.flipword.di

import com.tomnylow.flipword.data.repository.CardRepositoryImpl
import com.tomnylow.flipword.data.repository.DeckRepositoryImpl
import com.tomnylow.flipword.data.repository.ExternalWordRepositoryImpl
import com.tomnylow.flipword.domain.repository.CardRepository
import com.tomnylow.flipword.domain.repository.DeckRepository
import com.tomnylow.flipword.domain.repository.ExternalWordRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

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
}
