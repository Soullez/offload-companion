package com.offload.companion.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TurnLogicTest {

    @Test
    fun initialStateIsStartH1WithNoActiveCoach() {
        val state = MatchState()
        assertEquals(TurnMarker.START, state.redTurn)
        assertEquals(TurnMarker.START, state.blueTurn)
        assertEquals(MatchHalf.H1, state.half)
        assertNull(state.activeCoach)
    }

    @Test
    fun redFirstTurnAdvancesOnlyRedAndMakesRedActive() {
        val state = TurnLogic.startTurn(MatchState(), Coach.RED)
        assertEquals(TurnMarker.ONE, state.redTurn)
        assertEquals(TurnMarker.START, state.blueTurn)
        assertEquals(Coach.RED, state.activeCoach)
    }

    @Test
    fun consecutiveRedTurnsAreAllowed() {
        var state = MatchState()
        repeat(3) { state = TurnLogic.startTurn(state, Coach.RED) }
        assertEquals(TurnMarker.THREE, state.redTurn)
        assertEquals(TurnMarker.START, state.blueTurn)
        assertEquals(Coach.RED, state.activeCoach)
    }

    @Test
    fun blueAdvancesIndependently() {
        var state = MatchState(redTurn = TurnMarker.THREE)
        state = TurnLogic.startTurn(state, Coach.BLUE)
        assertEquals(TurnMarker.THREE, state.redTurn)
        assertEquals(TurnMarker.ONE, state.blueTurn)
        assertEquals(Coach.BLUE, state.activeCoach)
    }

    @Test
    fun activeCoachSwitchesWithStartTurn() {
        var state = MatchState()
        state = TurnLogic.startTurn(state, Coach.RED)
        assertEquals(Coach.RED, state.activeCoach)
        state = TurnLogic.startTurn(state, Coach.BLUE)
        assertEquals(Coach.BLUE, state.activeCoach)
        state = TurnLogic.startTurn(state, Coach.RED)
        assertEquals(Coach.RED, state.activeCoach)
    }

    @Test
    fun fullProgressionEndsAtOvertimeAndClamps() {
        var state = MatchState()
        val expected = listOf(
            TurnMarker.ONE,
            TurnMarker.TWO,
            TurnMarker.THREE,
            TurnMarker.FOUR,
            TurnMarker.FIVE,
            TurnMarker.SIX,
            TurnMarker.SEVEN,
            TurnMarker.OT
        )
        expected.forEach { marker ->
            state = TurnLogic.startTurn(state, Coach.RED)
            assertEquals(marker, state.redTurn)
        }
        repeat(3) { state = TurnLogic.startTurn(state, Coach.RED) }
        assertEquals(TurnMarker.OT, state.redTurn)
    }

    @Test
    fun secondHalfResetsTurnsAndClearsActiveCoach() {
        val state = MatchState(
            redTurn = TurnMarker.FIVE,
            blueTurn = TurnMarker.THREE,
            activeCoach = Coach.RED
        )
        val result = TurnLogic.startSecondHalf(state)
        assertEquals(MatchHalf.H2, result.half)
        assertEquals(TurnMarker.START, result.redTurn)
        assertEquals(TurnMarker.START, result.blueTurn)
        assertNull(result.activeCoach)
    }

    @Test
    fun secondHalfPreservesScoresAndOffloadTargets() {
        val state = MatchState(
            redScore = 17,
            blueScore = 12,
            redOffload = OffloadTarget.FOUR_PLUS,
            blueOffload = OffloadTarget.SIX_PLUS,
            redTurn = TurnMarker.SEVEN,
            blueTurn = TurnMarker.FIVE,
            activeCoach = Coach.BLUE
        )
        val result = TurnLogic.startSecondHalf(state)
        assertEquals(17, result.redScore)
        assertEquals(12, result.blueScore)
        assertEquals(OffloadTarget.FOUR_PLUS, result.redOffload)
        assertEquals(OffloadTarget.SIX_PLUS, result.blueOffload)
    }

    @Test
    fun attemptingSecondHalfAgainDoesNothing() {
        val state = MatchState(
            half = MatchHalf.H2,
            redTurn = TurnMarker.THREE,
            blueTurn = TurnMarker.TWO,
            activeCoach = Coach.BLUE
        )
        assertEquals(state, TurnLogic.startSecondHalf(state))
    }

    @Test
    fun scoringDoesNotAlterTurnHalfOrActiveCoach() {
        val state = MatchState(
            redTurn = TurnMarker.FOUR,
            blueTurn = TurnMarker.TWO,
            half = MatchHalf.H1,
            activeCoach = Coach.BLUE
        )
        val scored = ScoreboardLogic.addTry(state, Coach.RED)
        assertEquals(TurnMarker.FOUR, scored.redTurn)
        assertEquals(TurnMarker.TWO, scored.blueTurn)
        assertEquals(MatchHalf.H1, scored.half)
        assertEquals(Coach.BLUE, scored.activeCoach)
    }

    @Test
    fun offloadAndRestDoNotAlterTurnHalfOrActiveCoach() {
        val state = MatchState(
            redTurn = TurnMarker.FOUR,
            blueTurn = TurnMarker.TWO,
            half = MatchHalf.H1,
            activeCoach = Coach.RED
        )
        val changed = OffloadLogic.rest(OffloadLogic.increase(state, Coach.BLUE))
        assertEquals(TurnMarker.FOUR, changed.redTurn)
        assertEquals(TurnMarker.TWO, changed.blueTurn)
        assertEquals(MatchHalf.H1, changed.half)
        assertEquals(Coach.RED, changed.activeCoach)
    }
}
