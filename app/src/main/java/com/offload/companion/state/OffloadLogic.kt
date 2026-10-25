package com.offload.companion.state

object OffloadLogic {

    fun increase(state: MatchState, coach: Coach): MatchState =
        setTarget(state, coach, next(targetFor(state, coach)))

    fun decrease(state: MatchState, coach: Coach): MatchState =
        setTarget(state, coach, previous(targetFor(state, coach)))

    fun rest(state: MatchState): MatchState =
        state.copy(
            redOffload = OffloadTarget.TWO_PLUS,
            blueOffload = OffloadTarget.TWO_PLUS
        )

    private fun targetFor(state: MatchState, coach: Coach): OffloadTarget =
        when (coach) {
            Coach.RED -> state.redOffload
            Coach.BLUE -> state.blueOffload
        }

    private fun setTarget(state: MatchState, coach: Coach, target: OffloadTarget): MatchState =
        when (coach) {
            Coach.RED -> state.copy(redOffload = target)
            Coach.BLUE -> state.copy(blueOffload = target)
        }

    private fun next(target: OffloadTarget): OffloadTarget =
        when (target) {
            OffloadTarget.TWO_PLUS -> OffloadTarget.THREE_PLUS
            OffloadTarget.THREE_PLUS -> OffloadTarget.FOUR_PLUS
            OffloadTarget.FOUR_PLUS -> OffloadTarget.FIVE_PLUS
            OffloadTarget.FIVE_PLUS -> OffloadTarget.SIX_PLUS
            OffloadTarget.SIX_PLUS -> OffloadTarget.SIX_PLUS
        }

    private fun previous(target: OffloadTarget): OffloadTarget =
        when (target) {
            OffloadTarget.TWO_PLUS -> OffloadTarget.TWO_PLUS
            OffloadTarget.THREE_PLUS -> OffloadTarget.TWO_PLUS
            OffloadTarget.FOUR_PLUS -> OffloadTarget.THREE_PLUS
            OffloadTarget.FIVE_PLUS -> OffloadTarget.FOUR_PLUS
            OffloadTarget.SIX_PLUS -> OffloadTarget.FIVE_PLUS
        }
}
