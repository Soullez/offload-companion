package com.offload.companion.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.offload.companion.data.MatchPersistence

class MatchController(
    initialState: MatchState,
    private val persistence: MatchPersistence
) {
    private val history = mutableListOf<MatchState>()

    var state by mutableStateOf(initialState)
        private set

    val canUndo: Boolean
        get() = history.isNotEmpty()

    val historySize: Int
        get() = history.size

    fun commit(transform: (MatchState) -> MatchState): Boolean {
        val before = state
        val after = transform(before)
        if (after == before) return false
        history += before
        state = after
        persistence.save(after)
        return true
    }

    fun undo(): Boolean {
        val previous = history.removeLastOrNull() ?: return false
        state = previous
        persistence.save(previous)
        return true
    }

    fun reset() {
        history.clear()
        state = MatchState()
        persistence.save(state)
    }
}
