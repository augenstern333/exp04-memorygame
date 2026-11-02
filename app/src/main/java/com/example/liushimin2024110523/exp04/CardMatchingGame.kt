package com.example.liushimin2024110523.exp04

import java.io.Serializable

/** Model: owns scoring, selection, mismatch resolution and saved game state. */
class CardMatchingGame(initialCards: List<Card>) : Serializable {
    companion object {
        private const val serialVersionUID = 1L

        /** Deal an even number of each suit, then randomize the whole board. */
        private fun drawCards(count: Int): List<Card> {
            require(count in 0..48 && count % 2 == 0)
            val fullRounds = count / 8
            val extraPairs = count % 8 / 2
            return Card.validSuits.flatMapIndexed { suitIndex, suit ->
                val suitCount = 2 * (fullRounds + if (suitIndex < extraPairs) 1 else 0)
                Card.rankStrings.toList().shuffled().take(suitCount).map { rank -> Card(suit, rank) }
            }.shuffled()
        }
    }

    constructor(count: Int) : this(drawCards(count))

    // Own the supplied card states instead of sharing mutable input objects.
    private val cards = initialCards.map {
        Card(it.suit, it.rank, it.isChosen, it.isMatched)
    }

    var score = 0
        private set

    val cardCount: Int
        get() = cards.size

    val matchedPairs: Int
        get() = cards.count { it.isMatched } / 2

    val isComplete: Boolean
        get() = cards.all { it.isMatched }

    /** A mismatch stays visible until the Controller ends its observation delay. */
    val hasPendingMismatch: Boolean
        get() = cards.count { it.isChosen && !it.isMatched } >= 2

    val hasAvailableMatch: Boolean
        get() {
            for (first in cards.indices) {
                if (cards[first].isMatched) continue
                for (second in first + 1 until cards.size) {
                    if (!cards[second].isMatched && cards[first].match(arrayOf(cards[second])) > 0) {
                        return true
                    }
                }
            }
            return false
        }

    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 10
    val COST_TO_CHOOSE = 0

    fun cardAtIndex(index: Int): Card = cards[index]

    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (hasPendingMismatch || card.isMatched) return
        if (card.isChosen) {
            card.isChosen = false
            return
        }

        val otherCard = cards.firstOrNull { it.isChosen && !it.isMatched }
        card.isChosen = true
        if (otherCard != null) {
            if (card.match(arrayOf(otherCard)) > 0) {
                score += MATCH_BONUS
                otherCard.isMatched = true
                card.isMatched = true
            } else {
                score -= MISMATCH_PENALTY
            }
        }
    }

    /** Hide both mismatched cards together without changing the score. */
    fun dismissMismatch() {
        if (!hasPendingMismatch) return
        cards.filter { !it.isMatched }.forEach { it.isChosen = false }
    }
}
