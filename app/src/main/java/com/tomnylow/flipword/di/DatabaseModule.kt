package com.tomnylow.flipword.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.tomnylow.flipword.data.local.CardDao
import com.tomnylow.flipword.data.local.DeckDao
import com.tomnylow.flipword.data.local.FlipWordDatabase
import com.tomnylow.flipword.data.local.stats.StatisticsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRepeatWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideFlipWordDatabase(@ApplicationContext context: Context): FlipWordDatabase {
        return Room.databaseBuilder(
            context,
            FlipWordDatabase::class.java,
            "flipword_database"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    fun provideDeckDao(database: FlipWordDatabase): DeckDao {
        return database.deckDao()
    }

    @Provides
    fun provideCardDao(database: FlipWordDatabase): CardDao {
        return database.cardDao()
    }

    @Provides
    fun provideStatDao(database: FlipWordDatabase): StatisticsDao {
        return database.statDao()
    }
}
