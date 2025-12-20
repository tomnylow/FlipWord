package com.tomnylow.flipword.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.sm2.SM2Params
import java.time.LocalDate

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val translation: String? = null,
    val definition: String? = null,
    val usageExample: String? = null,
    val deckId: Long,
    // SM2 fields
    val easeFactor: Double,
    val interval: Int,
    val repetition: Int,
    val nextReviewDate: LocalDate
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
        )
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
        nextReviewDate = sm2Params.nextReviewDate
    )
}
