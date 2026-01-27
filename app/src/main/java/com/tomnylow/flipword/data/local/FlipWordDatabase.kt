package com.tomnylow.flipword.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tomnylow.flipword.data.local.model.CardEntity
import com.tomnylow.flipword.data.local.model.DeckEntity
import com.tomnylow.flipword.data.local.stats.StatisticsDao
import com.tomnylow.flipword.data.local.stats.StudyLogEntity


@Database(
    entities = [ DeckEntity::class, CardEntity::class, StudyLogEntity::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlipWordDatabase : RoomDatabase() {
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
    abstract fun statDao(): StatisticsDao
}
