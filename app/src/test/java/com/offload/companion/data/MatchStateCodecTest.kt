package com.offload.companion.data

import com.offload.companion.state.Coach
import com.offload.companion.state.MatchHalf
import com.offload.companion.state.MatchState
import com.offload.companion.state.OffloadTarget
import com.offload.companion.state.TurnMarker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MatchStateCodecTest {

    @Test
    fun roundTripPreservesCompleteComplexState() {
        val state = MatchState(
            redScore = 17,
            blueScore = 12,
            redOffload = OffloadTarget.FOUR_PLUS,
            blueOffload = OffloadTarget.SIX_PLUS,
            redTurn = TurnMarker.FIVE,
            blueTurn = TurnMarker.THREE,
            half = MatchHalf.H2,
            activeCoach = Coach.BLUE
        )
        assertEquals(state, MatchStateCodec.decode(MatchStateCodec.encode(state)))
    }

    @Test
    fun roundTripPreservesRedActive() {
        val state = MatchState(activeCoach = Coach.RED)
        assertEquals(state, MatchStateCodec.decode(MatchStateCodec.encode(state)))
    }

    @Test
    fun roundTripPreservesNullActiveCoach() {
        val state = MatchState(activeCoach = null)
        assertEquals(state, MatchStateCodec.decode(MatchStateCodec.encode(state)))
    }

    @Test
    fun everyOffloadAndTurnValueRoundTrips() {
        OffloadTarget.entries.forEach { offload ->
            val state = MatchState(redOffload = offload, blueOffload = offload)
            assertEquals(state, MatchStateCodec.decode(MatchStateCodec.encode(state)))
        }
        TurnMarker.entries.forEach { turn ->
            val state = MatchState(redTurn = turn, blueTurn = turn)
            assertEquals(state, MatchStateCodec.decode(MatchStateCodec.encode(state)))
        }
        MatchHalf.entries.forEach { half ->
            val state = MatchState(half = half)
            assertEquals(state, MatchStateCodec.decode(MatchStateCodec.encode(state)))
        }
    }

    @Test
    fun corruptValuesFailSafely() {
        val corrupt = MatchStateCodec.encode(MatchState()).toMutableMap()
        corrupt["redTurn"] = "NOT_A_TURN"
        assertNull(MatchStateCodec.decode(corrupt))
    }

    @Test
    fun missingSchemaFailsSafely() {
        assertNull(MatchStateCodec.decode(emptyMap()))
    }
}
