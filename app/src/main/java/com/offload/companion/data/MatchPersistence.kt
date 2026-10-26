package com.offload.companion.data

import android.content.Context
import com.offload.companion.state.Coach
import com.offload.companion.state.MatchHalf
import com.offload.companion.state.MatchState
import com.offload.companion.state.OffloadTarget
import com.offload.companion.state.TurnMarker

interface MatchPersistence {
    fun load(): MatchState?
    fun save(state: MatchState)
    fun clear()
}

object MatchStateCodec {
    private const val SCHEMA = "1"

    fun encode(state: MatchState): Map<String, String> = mapOf(
        "schema" to SCHEMA,
        "redScore" to state.redScore.toString(),
        "blueScore" to state.blueScore.toString(),
        "redOffload" to state.redOffload.name,
        "blueOffload" to state.blueOffload.name,
        "redTurn" to state.redTurn.name,
        "blueTurn" to state.blueTurn.name,
        "half" to state.half.name,
        "activeCoach" to (state.activeCoach?.name ?: "NONE")
    )

    fun decode(values: Map<String, String>): MatchState? = runCatching {
        if (values["schema"] != SCHEMA) return null
        MatchState(
            redScore = values.getValue("redScore").toInt().coerceAtLeast(0),
            blueScore = values.getValue("blueScore").toInt().coerceAtLeast(0),
            redOffload = OffloadTarget.valueOf(values.getValue("redOffload")),
            blueOffload = OffloadTarget.valueOf(values.getValue("blueOffload")),
            redTurn = TurnMarker.valueOf(values.getValue("redTurn")),
            blueTurn = TurnMarker.valueOf(values.getValue("blueTurn")),
            half = MatchHalf.valueOf(values.getValue("half")),
            activeCoach = when (val raw = values.getValue("activeCoach")) {
                "NONE" -> null
                else -> Coach.valueOf(raw)
            }
        )
    }.getOrNull()
}

class SharedPreferencesMatchPersistence(context: Context) : MatchPersistence {
    private val preferences =
        context.applicationContext.getSharedPreferences("offload_match_state", Context.MODE_PRIVATE)

    override fun load(): MatchState? {
        if (!preferences.contains("schema")) return null
        val values = preferences.all.mapNotNull { (key, value) ->
            (value as? String)?.let { key to it }
        }.toMap()
        return MatchStateCodec.decode(values) ?: MatchState()
    }

    override fun save(state: MatchState) {
        val editor = preferences.edit().clear()
        MatchStateCodec.encode(state).forEach { (key, value) -> editor.putString(key, value) }
        editor.commit()
    }

    override fun clear() {
        preferences.edit().clear().commit()
    }
}
