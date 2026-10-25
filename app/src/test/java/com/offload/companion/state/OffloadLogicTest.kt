package com.offload.companion.state

import org.junit.Assert.assertEquals
import org.junit.Test

class OffloadLogicTest {

    @Test
    fun initialStateStartsBothAtTwoPlus() {
        val state = MatchState()
        assertEquals(OffloadTarget.TWO_PLUS, state.redOffload)
        assertEquals(OffloadTarget.TWO_PLUS, state.blueOffload)
    }

    @Test
    fun redPlusMovesTwoToThree() {
        val state = OffloadLogic.increase(MatchState(), Coach.RED)
        assertEquals(OffloadTarget.THREE_PLUS, state.redOffload)
        assertEquals(OffloadTarget.TWO_PLUS, state.blueOffload)
    }

    @Test
    fun redProgressesToSixAndClamps() {
        var state = MatchState()
        repeat(10) { state = OffloadLogic.increase(state, Coach.RED) }
        assertEquals(OffloadTarget.SIX_PLUS, state.redOffload)
        state = OffloadLogic.increase(state, Coach.RED)
        assertEquals(OffloadTarget.SIX_PLUS, state.redOffload)
    }

    @Test
    fun redMinusMovesSixToFiveAndClampsAtTwo() {
        var state = MatchState(redOffload = OffloadTarget.SIX_PLUS)
        state = OffloadLogic.decrease(state, Coach.RED)
        assertEquals(OffloadTarget.FIVE_PLUS, state.redOffload)
        repeat(10) { state = OffloadLogic.decrease(state, Coach.RED) }
        assertEquals(OffloadTarget.TWO_PLUS, state.redOffload)
    }

    @Test
    fun blueProgressesIndependently() {
        var state = MatchState(redOffload = OffloadTarget.FOUR_PLUS)
        repeat(3) { state = OffloadLogic.increase(state, Coach.BLUE) }
        assertEquals(OffloadTarget.FOUR_PLUS, state.redOffload)
        assertEquals(OffloadTarget.FIVE_PLUS, state.blueOffload)
    }

    @Test
    fun blueClampsAtBothBounds() {
        var state = MatchState()
        state = OffloadLogic.decrease(state, Coach.BLUE)
        assertEquals(OffloadTarget.TWO_PLUS, state.blueOffload)
        repeat(20) { state = OffloadLogic.increase(state, Coach.BLUE) }
        assertEquals(OffloadTarget.SIX_PLUS, state.blueOffload)
    }

    @Test
    fun restResetsBothToTwoPlus() {
        val initial = MatchState(
            redOffload = OffloadTarget.FIVE_PLUS,
            blueOffload = OffloadTarget.SIX_PLUS
        )
        val state = OffloadLogic.rest(initial)
        assertEquals(OffloadTarget.TWO_PLUS, state.redOffload)
        assertEquals(OffloadTarget.TWO_PLUS, state.blueOffload)
    }

    @Test
    fun offloadChangesDoNotChangeScores() {
        val original = MatchState(
            redScore = 17,
            blueScore = 12,
            redOffload = OffloadTarget.FIVE_PLUS,
            blueOffload = OffloadTarget.SIX_PLUS
        )
        val changed = OffloadLogic.rest(OffloadLogic.decrease(original, Coach.RED))
        assertEquals(17, changed.redScore)
        assertEquals(12, changed.blueScore)
    }
}
