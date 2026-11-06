package com.example.liushimin2024110523.exp04

import java.io.Serializable

class Card(
    val suit: String,
    val rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")
    }

    override fun toString(): String = suit + rank

    /** Accept one or two other cards of the same suit; rank identifies the dealt card. */
    fun match(otherCards: Array<Card>): Int =
        if (otherCards.size in 1..2 && otherCards.all { it.suit == suit }) 1 else 0
}
