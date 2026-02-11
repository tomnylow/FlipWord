package com.tomnylow.flipword.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.tomnylow.flipword.data.local.LocalDateSerializer
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.sm2.SM2Params
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Entity(
    tableName = "cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["deckId"])]
)
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val translation: String? = null,
    val definition: String? = null,
    val usageExample: String? = null,
    val deckId: Long,

    val easeFactor: Double,
    val interval: Int,
    val repetition: Int,
    @Serializable(with = LocalDateSerializer::class)
    val nextReviewDate: LocalDate,

    val updatedAt: Long = System.currentTimeMillis(),
)

fun CardEntity.toDomain(): Card {
    return Card(
        id = id,
        word = word,
        translation = translation,
        definition = definition,
        usageExample = usageExample,
        deckId = deckId,
        sm2Params = SM2Params(
            easeFactor = easeFactor,
            interval = interval,
            repetition = repetition,
            nextReviewDate = nextReviewDate
        ),
        updatedAt = updatedAt
    )
}

fun Card.toEntity(): CardEntity {
    return CardEntity(
        id = id,
        word = word,
        translation = translation,
        definition = definition,
        usageExample = usageExample,
        deckId = deckId,
        easeFactor = sm2Params.easeFactor,
        interval = sm2Params.interval,
        repetition = sm2Params.repetition,
        nextReviewDate = sm2Params.nextReviewDate,
        updatedAt = updatedAt
    )
}
