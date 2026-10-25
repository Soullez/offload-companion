package com.offload.companion.state

import org.junit.Assert.assertEquals
import org.junit.Test

class ScoreboardLogicTest {

    @Test
    fun redTryAddsFive() {
        val state = ScoreboardLogic.addTry(MatchState(), Coach.RED)
        assertEquals(5, state.redScore)
        assertEquals(0, state.blueScore)
    }

    @Test
    fun redConversionAddsTwo() {
        val state = ScoreboardLogic.addConversion(MatchState(), Coach.RED)
        assertEquals(2, state.redScore)
    }

    @Test
    fun redTryAndConversionTotalSeven() {
        val afterTry = ScoreboardLogic.addTry(MatchState(), Coach.RED)
        val afterConversion = ScoreboardLogic.addConversion(afterTry, Coach.RED)
        assertEquals(7, afterConversion.redScore)
    }

    @Test
    fun blueChangesDoNotChangeRed() {
        val red = ScoreboardLogic.correctScore(MatchState(), Coach.RED, 12)
        val blue = ScoreboardLogic.addTry(red, Coach.BLUE)
        assertEquals(12, blue.redScore)
        assertEquals(5, blue.blueScore)
    }

    @Test
    fun redChangesDoNotChangeBlue() {
        val blue = ScoreboardLogic.correctScore(MatchState(), Coach.BLUE, 9)
        val red = ScoreboardLogic.addConversion(blue, Coach.RED)
        assertEquals(2, red.redScore)
        assertEquals(9, red.blueScore)
    }

    @Test
    fun repeatedScoringProducesCorrectLargeTotal() {
        var state = MatchState()
        repeat(20) { state = ScoreboardLogic.addTry(state, Coach.RED) }
        repeat(10) { state = ScoreboardLogic.addConversion(state, Coach.RED) }
        assertEquals(120, state.redScore)
    }

    @Test
    fun correctionDecrementAndIncrementWork() {
        assertEquals(6, ScoreboardLogic.adjustCorrection(7, -1))
        assertEquals(8, ScoreboardLogic.adjustCorrection(7, 1))
    }

    @Test
    fun correctionCannotGoBelowZero() {
        assertEquals(0, ScoreboardLogic.adjustCorrection(0, -1))
        assertEquals(0, ScoreboardLogic.correctScore(MatchState(), Coach.RED, -99).redScore)
    }

    @Test
    fun correctionOnlyAppliesWhenCommittedByCaller() {
        val original = MatchState(redScore = 7)
        val draft = ScoreboardLogic.adjustCorrection(original.redScore, -1)
        assertEquals(6, draft)
        assertEquals(7, original.redScore)
        assertEquals(6, ScoreboardLogic.correctScore(original, Coach.RED, draft).redScore)
    }
}
