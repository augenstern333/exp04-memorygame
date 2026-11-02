package com.example.liushimin2024110523.exp04

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CardMatchingGameTest {
    @Test
    fun cardsMatchOnlyBySuit() {
        val card = Card("♥", "A")
        assertEquals(1, card.match(arrayOf(Card("♥", "K"))))
        assertEquals(1, card.match(arrayOf(Card("♥", "A"))))
        assertEquals(0, card.match(arrayOf(Card("♠", "A"))))
        assertEquals(0, card.match(emptyArray()))
        assertEquals(0, card.match(arrayOf(Card("♥", "2"), Card("♥", "3"))))
    }

    @Test
    fun deckDrawsEveryCardOnceAndThenReturnsNull() {
        val deck = Deck()
        val dealtCards = List(52) { checkNotNull(deck.drawRandomCard()).toString() }
        assertEquals(52, dealtCards.toSet().size)
        assertNull(deck.drawRandomCard())
    }

    @Test
    fun choosingAndCancellingACardDoNotChangeScore() {
        val game = CardMatchingGame(listOf(Card("♥", "A"), Card("♠", "K")))
        game.chooseCardAtIndex(0)
        assertEquals(0, game.score)
        assertTrue(game.cardAtIndex(0).isChosen)
        game.chooseCardAtIndex(0)
        assertEquals(0, game.score)
        assertFalse(game.cardAtIndex(0).isChosen)
        game.chooseCardAtIndex(0)
        assertEquals(0, game.score)
    }

    @Test
    fun matchingSuitImmediatelyAwardsTenPoints() {
        val game = CardMatchingGame(listOf(Card("♥", "A"), Card("♥", "K")))
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertEquals(10, game.score)
        assertEquals(1, game.matchedPairs)
        assertTrue(game.isComplete)
        assertFalse(game.hasAvailableMatch)
        assertFalse(game.hasPendingMismatch)
        assertTrue(game.cardAtIndex(0).isMatched)
        assertTrue(game.cardAtIndex(1).isMatched)
    }

    @Test
    fun sameRankWithDifferentSuitsIsAMismatch() {
        val game = CardMatchingGame(listOf(Card("♥", "A"), Card("♠", "A")))
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertEquals(-2, game.score)
        assertEquals(0, game.matchedPairs)
        assertTrue(game.hasPendingMismatch)
        assertTrue(game.cardAtIndex(0).isChosen)
        assertTrue(game.cardAtIndex(1).isChosen)
    }

    @Test
    fun pendingMismatchRejectsThirdCardAndRepeatedClicks() {
        val game = CardMatchingGame(listOf(Card("♥", "A"), Card("♠", "K"), Card("♠", "Q")))
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertEquals(-2, game.score)
        assertTrue(game.cardAtIndex(0).isChosen)
        assertTrue(game.cardAtIndex(1).isChosen)
        assertFalse(game.cardAtIndex(2).isChosen)
        assertEquals(0, game.matchedPairs)
    }

    @Test
    fun dismissMismatchHidesBothCardsWithoutAnotherPenalty() {
        val game = CardMatchingGame(listOf(Card("♥", "A"), Card("♠", "K"), Card("♠", "Q")))
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.dismissMismatch()
        assertEquals(-2, game.score)
        assertFalse(game.hasPendingMismatch)
        assertFalse(game.cardAtIndex(0).isChosen)
        assertFalse(game.cardAtIndex(1).isChosen)
        game.dismissMismatch()
        assertEquals(-2, game.score)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)
        assertEquals(8, game.score)
        assertEquals(1, game.matchedPairs)
    }

    @Test
    fun dismissMismatchDoesNotHideAnOrdinarySingleSelection() {
        val game = CardMatchingGame(listOf(Card("♥", "A"), Card("♠", "K")))
        game.chooseCardAtIndex(0)
        game.dismissMismatch()
        assertTrue(game.cardAtIndex(0).isChosen)
        assertEquals(0, game.score)
    }

    @Test
    fun choosingMatchedCardHasNoEffect() {
        val game = CardMatchingGame(listOf(Card("♥", "A"), Card("♥", "K")))
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.dismissMismatch()
        assertEquals(10, game.score)
        assertTrue(game.cardAtIndex(0).isChosen)
        assertTrue(game.cardAtIndex(1).isChosen)
    }

    @Test
    fun availableMatchIgnoresMatchedCardsAndDetectsStrandedCards() {
        val game = CardMatchingGame(listOf(
            Card("♥", "A"), Card("♥", "K"), Card("♦", "2"), Card("♣", "3")
        ))
        assertTrue(game.hasAvailableMatch)
        assertFalse(game.isComplete)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        assertFalse(game.hasAvailableMatch)
        assertFalse(game.isComplete)
        assertEquals(1, game.matchedPairs)
        assertEquals(4, game.cardCount)
    }

    @Test
    fun constructorCopiesTheProvidedCards() {
        val original = Card("♥", "A")
        val game = CardMatchingGame(listOf(original))
        assertNotSame(original, game.cardAtIndex(0))
        original.isMatched = true
        assertFalse(game.cardAtIndex(0).isMatched)
    }

    @Test
    fun sixteenCardDealContainsFourOfEachSuitAndCanBeCompleted() {
        val game = CardMatchingGame(16)
        val indicesBySuit = (0 until game.cardCount).groupBy { game.cardAtIndex(it).suit }
        assertEquals(16, game.cardCount)
        assertEquals(16, (0 until game.cardCount).map { game.cardAtIndex(it).toString() }.toSet().size)
        assertEquals(Card.validSuits.toSet(), indicesBySuit.keys)
        indicesBySuit.values.forEach { indices ->
            assertEquals(4, indices.size)
            indices.chunked(2).forEach { pair ->
                game.chooseCardAtIndex(pair[0])
                game.chooseCardAtIndex(pair[1])
            }
        }
        assertEquals(80, game.score)
        assertEquals(8, game.matchedPairs)
        assertTrue(game.isComplete)
    }

    @Test
    fun evenDealAlwaysKeepsSuitCountsEven() {
        for (count in 0..48 step 2) {
            val game = CardMatchingGame(count)
            assertEquals(count, game.cardCount)
            val suits = (0 until count).groupingBy { game.cardAtIndex(it).suit }.eachCount()
            assertTrue(suits.values.all { it % 2 == 0 })
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun oddCardCountIsRejected() {
        CardMatchingGame(15)
    }

    @Test
    fun serializationRestoresPendingMismatchAndCanContinueAfterDismissal() {
        val game = CardMatchingGame(listOf(Card("♥", "A"), Card("♠", "K"), Card("♠", "Q")))
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        val restored = roundTrip(game)
        assertEquals(-2, restored.score)
        assertTrue(restored.hasPendingMismatch)
        assertTrue(restored.cardAtIndex(0).isChosen)
        assertTrue(restored.cardAtIndex(1).isChosen)
        restored.chooseCardAtIndex(2)
        assertFalse(restored.cardAtIndex(2).isChosen)
        restored.dismissMismatch()
        restored.chooseCardAtIndex(1)
        restored.chooseCardAtIndex(2)
        assertEquals(8, restored.score)
        assertEquals(1, restored.matchedPairs)
        assertTrue(game.hasPendingMismatch)
    }

    @Test
    fun serializationRestoresCardOrderScoreMatchedAndSingleChosenStates() {
        val game = CardMatchingGame(listOf(
            Card("♥", "A"), Card("♥", "K"), Card("♦", "2"), Card("♣", "3")
        ))
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.chooseCardAtIndex(2)
        val restored = roundTrip(game)
        assertEquals(10, restored.score)
        assertEquals(1, restored.matchedPairs)
        assertEquals(4, restored.cardCount)
        for (index in 0 until game.cardCount) {
            val expected = game.cardAtIndex(index)
            val actual = restored.cardAtIndex(index)
            assertEquals(expected.toString(), actual.toString())
            assertEquals(expected.isChosen, actual.isChosen)
            assertEquals(expected.isMatched, actual.isMatched)
        }
        assertTrue(restored.cardAtIndex(2).isChosen)
        assertFalse(restored.hasAvailableMatch)
        restored.chooseCardAtIndex(2)
        assertEquals(10, restored.score)
        assertFalse(restored.cardAtIndex(2).isChosen)
        assertTrue(game.cardAtIndex(2).isChosen)
    }

    private fun roundTrip(game: CardMatchingGame): CardMatchingGame {
        val bytes = ByteArrayOutputStream().also { stream ->
            ObjectOutputStream(stream).use { it.writeObject(game) }
        }.toByteArray()
        return ObjectInputStream(ByteArrayInputStream(bytes)).use {
            it.readObject() as CardMatchingGame
        }
    }
}
