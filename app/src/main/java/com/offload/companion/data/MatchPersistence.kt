package com.offload.companion.data

import com.offload.companion.state.MatchState

interface MatchPersistence {
    suspend fun load(): MatchState?
    suspend fun save(state: MatchState)
    suspend fun clear()
}
