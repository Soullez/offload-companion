package com.offload.companion.state

enum class Coach {
    RED,
    BLUE
}

enum class MatchHalf {
    H1,
    H2
}

enum class TurnMarker {
    START,
    ONE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    OT
}

enum class OffloadTarget(val label: String) {
    TWO_PLUS("2+"),
    THREE_PLUS("3+"),
    FOUR_PLUS("4+"),
    FIVE_PLUS("5+"),
    SIX_PLUS("6+")
}

data class MatchState(
    val redScore: Int = 0,
    val blueScore: Int = 0,
    val redOffload: OffloadTarget = OffloadTarget.TWO_PLUS,
    val blueOffload: OffloadTarget = OffloadTarget.TWO_PLUS,
    val redTurn: TurnMarker = TurnMarker.START,
    val blueTurn: TurnMarker = TurnMarker.START,
    val half: MatchHalf = MatchHalf.H1,
    val activeCoach: Coach? = null
)
