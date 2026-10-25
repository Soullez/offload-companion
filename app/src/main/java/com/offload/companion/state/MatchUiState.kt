package com.offload.companion.state

data class MatchUiState(
    val match: MatchState = MatchState(),
    val activeDialog: String? = null,
    val activeMenu: String? = null
)
