package com.tomnylow.flipword.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tomnylow.flipword.data.local.model.CardEntity
import com.tomnylow.flipword.data.local.model.DeckEntity
import com.tomnylow.flipword.data.local.model.UserEntity

@Database(
    entities = [UserEntity::class, DeckEntity::class, CardEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlipWordDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun deckDao(): DeckDao
    abstract fun cardDao(): CardDao
}
