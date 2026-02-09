package com.tomnylow.flipword.data.local.stats

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tomnylow.flipword.data.local.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate
@Serializable
@Entity(tableName = "study_logs")
data class StudyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Long,
    val deckId: Long,
    val isCorrect: Boolean,
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate
)