package com.offload.companion.state

import com.offload.companion.data.MatchPersistence
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchControllerTest {

    private class FakePersistence(initial: MatchState? = null) : MatchPersistence {
        var saved: MatchState? = initial
        override fun load(): MatchState? = saved
        override fun save(state: MatchState) { saved = state }
        override fun clear() { saved = null }
    }

    private fun controller(state: MatchState = MatchState()): Pair<MatchController, FakePersistence> {
        val persistence = FakePersistence(state)
        return MatchController(state, persistence) to persistence
    }

    @Test
    fun scoreUndoRestoresZero() {
        val (controller, persistence) = controller()
        controller.commit { ScoreboardLogic.addTry(it, Coach.RED) }
        assertEquals(5, controller.state.redScore)
        assertTrue(controller.canUndo)
        controller.undo()
        assertEquals(0, controller.state.redScore)
        assertEquals(controller.state, persistence.saved)
    }

    @Test
    fun multipleActionsUndoInReverseOrder() {
        val (controller, _) = controller()
        controller.commit { ScoreboardLogic.addTry(it, Coach.RED) }
        controller.commit { ScoreboardLogic.addConversion(it, Coach.RED) }
        assertEquals(7, controller.state.redScore)
        controller.undo()
        assertEquals(5, controller.state.redScore)
        controller.undo()
        assertEquals(0, controller.state.redScore)
        assertFalse(controller.canUndo)
    }

    @Test
    fun scoreCorrectionIsOneUndoableCommit() {
        val (controller, _) = controller(MatchState(redScore = 12))
        controller.commit { ScoreboardLogic.correctScore(it, Coach.RED, 10) }
        assertEquals(10, controller.state.redScore)
        assertEquals(1, controller.historySize)
        controller.undo()
        assertEquals(12, controller.state.redScore)
    }

    @Test
    fun neutralCommitCreatesNoHistory() {
        val (controller, _) = controller(MatchState(redOffload = OffloadTarget.SIX_PLUS))
        assertFalse(controller.commit { OffloadLogic.increase(it, Coach.RED) })
        assertEquals(0, controller.historySize)
        assertFalse(controller.canUndo)
    }

    @Test
    fun restAtInitialStateCreatesNoHistory() {
        val (controller, _) = controller()
        assertFalse(controller.commit { OffloadLogic.rest(it) })
        assertEquals(0, controller.historySize)
    }

    @Test
    fun restUndoRestoresBothTracks() {
        val initial = MatchState(
            redOffload = OffloadTarget.FIVE_PLUS,
            blueOffload = OffloadTarget.SIX_PLUS
        )
        val (controller, _) = controller(initial)
        controller.commit { OffloadLogic.rest(it) }
        assertEquals(OffloadTarget.TWO_PLUS, controller.state.redOffload)
        assertEquals(OffloadTarget.TWO_PLUS, controller.state.blueOffload)
        controller.undo()
        assertEquals(initial, controller.state)
    }

    @Test
    fun startTurnUndoRestoresTurnAndActiveCoach() {
        val (controller, _) = controller()
        controller.commit { TurnLogic.startTurn(it, Coach.RED) }
        assertEquals(TurnMarker.ONE, controller.state.redTurn)
        assertEquals(Coach.RED, controller.state.activeCoach)
        controller.undo()
        assertEquals(TurnMarker.START, controller.state.redTurn)
        assertNull(controller.state.activeCoach)
    }

    @Test
    fun activeCoachSwitchUndoRestoresPriorCoachAndTurns() {
        val initial = MatchState(redTurn = TurnMarker.THREE, activeCoach = Coach.RED)
        val (controller, _) = controller(initial)
        controller.commit { TurnLogic.startTurn(it, Coach.BLUE) }
        assertEquals(Coach.BLUE, controller.state.activeCoach)
        controller.undo()
        assertEquals(initial, controller.state)
    }

    @Test
    fun overtimeStartTurnIsMeaningfulOnlyWhenActiveCoachChanges() {
        val initial = MatchState(redTurn = TurnMarker.OT, activeCoach = Coach.BLUE)
        val (controller, _) = controller(initial)
        assertTrue(controller.commit { TurnLogic.startTurn(it, Coach.RED) })
        assertEquals(TurnMarker.OT, controller.state.redTurn)
        assertEquals(Coach.RED, controller.state.activeCoach)
        assertFalse(controller.commit { TurnLogic.startTurn(it, Coach.RED) })
        assertEquals(1, controller.historySize)
    }

    @Test
    fun secondHalfWhenAlreadyH2CreatesNoHistory() {
        val initial = MatchState(half = MatchHalf.H2)
        val (controller, _) = controller(initial)
        assertFalse(controller.commit { TurnLogic.startSecondHalf(it) })
        assertEquals(0, controller.historySize)
    }

    @Test
    fun secondHalfUndoRestoresExactAdvancedH1State() {
        val initial = MatchState(
            redScore = 17,
            blueScore = 12,
            redOffload = OffloadTarget.FOUR_PLUS,
            blueOffload = OffloadTarget.SIX_PLUS,
            redTurn = TurnMarker.SEVEN,
            blueTurn = TurnMarker.FIVE,
            half = MatchHalf.H1,
            activeCoach = Coach.BLUE
        )
        val (controller, _) = controller(initial)
        controller.commit { TurnLogic.startSecondHalf(it) }
        assertEquals(MatchHalf.H2, controller.state.half)
        controller.undo()
        assertEquals(initial, controller.state)
    }

    @Test
    fun resetReturnsFreshStateClearsHistoryAndPersists() {
        val complex = MatchState(
            redScore = 17,
            blueScore = 12,
            redOffload = OffloadTarget.FIVE_PLUS,
            blueOffload = OffloadTarget.SIX_PLUS,
            redTurn = TurnMarker.FIVE,
            blueTurn = TurnMarker.THREE,
            half = MatchHalf.H2,
            activeCoach = Coach.BLUE
        )
        val (controller, persistence) = controller(complex)
        controller.commit { ScoreboardLogic.addTry(it, Coach.RED) }
        assertTrue(controller.canUndo)
        controller.reset()
        assertEquals(MatchState(), controller.state)
        assertFalse(controller.canUndo)
        assertEquals(MatchState(), persistence.saved)
        assertFalse(controller.undo())
    }
}
