package com.example.liushimin2024110523.exp04

import java.io.Serializable

/** Model owns selection, scoring and a balanced, solvable deal. */
class CardMatchingGame(initialCards: List<Card>, val matchSize: Int = 3) : Serializable {
    companion object {
        private const val serialVersionUID = 2L
        private fun drawCards(count: Int, matchSize: Int): List<Card> {
            require(matchSize in 2..3)
            require(count in 0..48 && count % matchSize == 0)
            val groups = count / matchSize
            return Card.validSuits.flatMapIndexed { index, suit ->
                val size = (groups / 4 + if (index < groups % 4) 1 else 0) * matchSize
                Card.rankStrings.toList().shuffled().take(size).map { Card(suit, it) }
            }.shuffled()
        }
    }
    constructor(count: Int, matchSize: Int = 3) : this(drawCards(count, matchSize), matchSize)
    init { require(matchSize in 2..3) }
    private var cards = initialCards.map { Card(it.suit, it.rank, it.isChosen, it.isMatched) }
    var score = 0
        private set
    val cardCount get() = cards.size
    val matchedGroups get() = cards.count { it.isMatched } / matchSize
    val matchedPairs get() = matchedGroups
    val selectedCount get() = cards.count { it.isChosen && !it.isMatched }
    val isComplete get() = cards.all { it.isMatched }
    val hasPendingMismatch get() = selectedCount >= matchSize
    val hasAvailableMatch get() = cards.filter { !it.isMatched }.groupingBy { it.suit }
        .eachCount().values.any { it >= matchSize }
    val MISMATCH_PENALTY = 2
    val MATCH_BONUS = 10
    val COST_TO_CHOOSE = 0
    fun cardAtIndex(index: Int): Card = cards[index]

    fun chooseCardAtIndex(index: Int) {
        val card = cards.getOrNull(index) ?: return
        if (hasPendingMismatch || card.isMatched) return
        if (card.isChosen) {
            card.isChosen = false
            return
        }
        val others = cards.filter { it.isChosen && !it.isMatched }
        card.isChosen = true
        if (others.size == matchSize - 1) {
            if (card.match(others.toTypedArray()) > 0) {
                score += MATCH_BONUS
                (others + card).forEach { it.isMatched = true }
            } else score -= MISMATCH_PENALTY
        }
    }
    fun dismissMismatch() {
        if (hasPendingMismatch) cards.filter { !it.isMatched }.forEach { it.isChosen = false }
    }
    /** Keep the chosen mode and card count, deal again, clear every state. */
    fun reset() {
        cards = drawCards(cardCount, matchSize)
        score = 0
    }
}
