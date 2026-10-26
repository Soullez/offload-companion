# Offload Companion

Native Android companion application for the physical **Offload** rugby board game.

## Current version

**v0.1.6 — Undo, Persistence & Reset**

This patch preserves the QA-approved scoreboard, Offload, Turn, Half and Active Coach systems while adding whole-MatchState Undo, offline local match persistence/restoration and confirmed Reset Match.

## Functional behaviour

### Scoreboard
- Try = +5
- Conversion = +2
- Red and Blue scores are independent.
- Tap a SCORE box for −1 / +1 correction with CANCEL and DONE.
- Scores clamp at zero.
- Committed scoring and score correction are Undoable and persisted.

### Offload Tracks
- Each Coach independently moves through 2+, 3+, 4+, 5+, 6+.
- Minus/plus clamp at 2+ and 6+.
- REST resets both Coaches to 2+.
- Meaningful changes and REST are Undoable and persisted.

### Turns / Active Coach
- Red and Blue use independent START → 1 → 2 → 3 → 4 → 5 → 6 → 7 → OT tracks.
- START TURN advances only that Coach and makes that Coach Active.
- Consecutive turns by the same Coach are supported.
- OT clamps and does not advance further.
- Meaningful Start Turn changes are Undoable and persisted.

### Half
- A fresh match begins in H1.
- Selecting H2 opens a confirmation.
- START H2 changes to H2, resets both Turn Tracks to START and clears Active Coach while preserving scores and Offload Tracks.
- The H1 → H2 transition is Undoable as one whole-state action and is persisted.

### Undo
- UNDO stores complete MatchState snapshots, not partial variable reversals.
- State-neutral inputs do not add history.
- Score, correction, Offload, REST, Start Turn and H2 transitions are supported.
- Undo restores and persists the previous complete state.
- Undo history intentionally resets after full process recreation; the current match still restores correctly.

### Persistence
- Current MatchState is stored locally with Android SharedPreferences.
- No internet/cloud storage is used.
- Scores, Offload targets, Turn markers, Half and Active Coach restore after force-stop/process recreation.
- Invalid stored state safely falls back to a fresh MatchState.

### Reset Match
- RESET opens a confirmation dialog.
- CANCEL preserves the current match.
- Confirmed RESET returns scores to 0, Offloads to 2+, Turns to START, Half to H1 and Active Coach to none.
- Reset clears Undo history, persists the initial state and is intentionally not Undoable.

Phases/Actions menus, Dice Roller and Rules remain intentionally deferred.

## Stack

- Kotlin / Jetpack Compose
- Android Gradle Plugin 9.4.1
- Gradle 9.6.x compatible
- AndroidX Activity Compose 1.13.0
- compileSdk 36 / targetSdk 36
- minSdk 26
- Java 17

## App identity

- App: `Offload Companion`
- Package: `com.offload.companion`
- Landscape only
- Offline-first: no INTERNET permission
- No sound/haptics in v0.1

## Development build

```bash
gradle :app:testDebugUnitTest :app:assembleDebug
```

CI runs full Score/Offload/Turn/Half regression tests plus Undo/history and persistence-codec tests, installs the APK in an Android emulator, performs force-stop/relaunch persistence QA, Reset QA and three landscape visual checks at 2400×1080, 2340×1080 and 1920×1080.

## v0.1.x roadmap

- v0.1.1 — Android Project Foundation — complete
- v0.1.2 — Approved Visual Shell — complete
- v0.1.3 — Scoreboard Controls — complete
- v0.1.4 — Offload Tracks & REST — complete
- v0.1.5 — Turns, Half & Active Coach — complete
- v0.1.6 — Undo, Persistence & Reset — current
- v0.1.7 — Phases & Actions Menus
- v0.1.8 — Final Polish & v0.1 QA
