package com.example.liushimin2024110523.exp04

import org.junit.Assert.*
import org.junit.Test
import java.io.*

class ThreeCardGameTest {
    private fun game() = CardMatchingGame(listOf(
        Card("♥", "A"), Card("♥", "2"), Card("♥", "3"),
        Card("♠", "A"), Card("♠", "2"), Card("♠", "3")))

    @Test fun onlyTheThirdCardCompletesAGroup() {
        val game = game()
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertEquals(0, game.score)
        assertEquals(2, game.selectedCount)
        assertFalse(game.hasPendingMismatch)
        game.chooseCardAtIndex(2)
        assertEquals(10, game.score)
        assertEquals(1, game.matchedGroups)
        assertTrue((0..2).all { game.cardAtIndex(it).isMatched })
        game.chooseCardAtIndex(0)
        assertEquals(10, game.score)
    }
    @Test fun mismatchWaitsForThreeAndLocksAllInputUntilDismissed() {
        val game = game()
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(3)
        assertFalse(game.hasPendingMismatch)
        assertEquals(0, game.score)
        game.chooseCardAtIndex(1)
        assertEquals(-2, game.score)
        assertTrue(game.hasPendingMismatch)
        game.chooseCardAtIndex(4)
        game.chooseCardAtIndex(0)
        assertEquals(3, game.selectedCount)
        assertFalse(game.cardAtIndex(4).isChosen)
        game.dismissMismatch()
        assertEquals(0, game.selectedCount)
        assertEquals(-2, game.score)
        for (index in 0..2) game.chooseCardAtIndex(index)
        assertEquals(8, game.score)
    }
    @Test fun cancellingOneOfTwoSelectionsKeepsTheOther() {
        val game = game()
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(0)
        assertEquals(1, game.selectedCount)
        assertTrue(game.cardAtIndex(1).isChosen)
        assertEquals(0, game.score)
    }
    @Test fun twentyFourCardDealCanFinishAllEightGroups() {
        repeat(20) {
            val game = CardMatchingGame(24)
            val bySuit = (0 until 24).groupBy { game.cardAtIndex(it).suit }
            assertEquals(24, (0 until 24).map { game.cardAtIndex(it).toString() }.toSet().size)
            assertEquals(4, bySuit.size)
            bySuit.values.forEach { indices ->
                assertEquals(6, indices.size)
                indices.forEach { game.chooseCardAtIndex(it) }
            }
            assertTrue(game.isComplete)
            assertFalse(game.hasAvailableMatch)
            assertEquals(8, game.matchedGroups)
            assertEquals(80, game.score)
        }
    }
    @Test fun resetClearsScoreMatchedAndSelectedStates() {
        val game = game()
        (0..2).forEach { game.chooseCardAtIndex(it) }
        game.chooseCardAtIndex(3)
        game.reset()
        assertEquals(6, game.cardCount)
        assertEquals(3, game.matchSize)
        assertEquals(0, game.score)
        assertEquals(0, game.matchedGroups)
        assertEquals(0, game.selectedCount)
        assertTrue(game.hasAvailableMatch)
        assertTrue((0 until 6).none { game.cardAtIndex(it).isMatched })
    }
    @Test fun resetClearsPendingMismatch() {
        val game = game()
        listOf(0, 1, 3).forEach { game.chooseCardAtIndex(it) }
        game.reset()
        assertFalse(game.hasPendingMismatch)
        assertEquals(0, game.score)
        assertEquals(0, game.selectedCount)
    }
    @Test fun serializationPreservesTwoSelectionsAndContinuesTheGroup() {
        val game = game()
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        val stream = ByteArrayOutputStream()
        ObjectOutputStream(stream).use { it.writeObject(game) }
        val restored = ObjectInputStream(ByteArrayInputStream(stream.toByteArray())).use {
            it.readObject() as CardMatchingGame
        }
        assertEquals(3, restored.matchSize)
        assertEquals(2, restored.selectedCount)
        restored.chooseCardAtIndex(2)
        assertEquals(10, restored.score)
        assertEquals(2, game.selectedCount)
    }
    @Test fun sameRankAcrossSuitsCannotMatchThreeCards() {
        assertEquals(0, Card("♥", "A").match(arrayOf(Card("♠", "A"), Card("♦", "A"))))
        assertEquals(0, Card("♥", "A").match(arrayOf(Card("♥", "2"), Card("♠", "3"))))
    }
    @Test(expected = IllegalArgumentException::class)
    fun dealRejectsIncompleteGroups() { CardMatchingGame(23) }
}
