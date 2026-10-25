package com.offload.companion.state

object ScoreboardLogic {
    const val TRY_POINTS = 5
    const val CONVERSION_POINTS = 2

    fun addTry(state: MatchState, coach: Coach): MatchState =
        addPoints(state, coach, TRY_POINTS)

    fun addConversion(state: MatchState, coach: Coach): MatchState =
        addPoints(state, coach, CONVERSION_POINTS)

    fun correctScore(state: MatchState, coach: Coach, score: Int): MatchState {
        val corrected = score.coerceAtLeast(0)
        return when (coach) {
            Coach.RED -> state.copy(redScore = corrected)
            Coach.BLUE -> state.copy(blueScore = corrected)
        }
    }

    fun adjustCorrection(score: Int, delta: Int): Int =
        (score + delta).coerceAtLeast(0)

    private fun addPoints(state: MatchState, coach: Coach, points: Int): MatchState =
        when (coach) {
            Coach.RED -> state.copy(redScore = state.redScore + points)
            Coach.BLUE -> state.copy(blueScore = state.blueScore + points)
        }
}
