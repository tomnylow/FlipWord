package com.tomnylow.flipword.data.local.model

import com.tomnylow.flipword.domain.model.Card
import com.tomnylow.flipword.domain.model.Deck
import com.tomnylow.flipword.domain.sm2.SM2Params

fun DeckEntity.toDomain(): Deck {
    return Deck(
        id = id,
        name = name,
        updatedAt = updatedAt

    )
}

fun Deck.toEntity(): DeckEntity {
    return DeckEntity(
        id = id,
        name = name,
        updatedAt = updatedAt
    )
}

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

