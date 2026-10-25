package com.offload.companion.state

object TurnLogic {

    fun startTurn(state: MatchState, coach: Coach): MatchState =
        when (coach) {
            Coach.RED -> state.copy(
                redTurn = next(state.redTurn),
                activeCoach = Coach.RED
            )
            Coach.BLUE -> state.copy(
                blueTurn = next(state.blueTurn),
                activeCoach = Coach.BLUE
            )
        }

    fun startSecondHalf(state: MatchState): MatchState {
        if (state.half == MatchHalf.H2) return state
        return state.copy(
            redTurn = TurnMarker.START,
            blueTurn = TurnMarker.START,
            half = MatchHalf.H2,
            activeCoach = null
        )
    }

    private fun next(marker: TurnMarker): TurnMarker =
        when (marker) {
            TurnMarker.START -> TurnMarker.ONE
            TurnMarker.ONE -> TurnMarker.TWO
            TurnMarker.TWO -> TurnMarker.THREE
            TurnMarker.THREE -> TurnMarker.FOUR
            TurnMarker.FOUR -> TurnMarker.FIVE
            TurnMarker.FIVE -> TurnMarker.SIX
            TurnMarker.SIX -> TurnMarker.SEVEN
            TurnMarker.SEVEN -> TurnMarker.OT
            TurnMarker.OT -> TurnMarker.OT
        }
}
