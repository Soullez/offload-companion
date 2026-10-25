package com.offload.companion.history

import com.offload.companion.state.MatchState

interface MatchHistory {
    fun record(state: MatchState)
    fun undo(): MatchState?
    fun clear()
}
