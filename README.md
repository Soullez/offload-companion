# Offload Companion

Native Android companion application for the physical **Offload** rugby board game.

## Current version

**v0.1.4 — Offload Tracks & REST**

This patch preserves the QA-approved scoreboard and visual shell while adding independent Red/Blue Offload Track controls and the central REST action.

## Functional behaviour

### Scoreboard
- Try = +5
- Conversion = +2
- Red and Blue scores are independent.
- Tap a SCORE box for −1 / +1 correction with CANCEL and DONE.
- Scores clamp at zero.

### Offload Tracks
- Each Coach independently moves through 2+, 3+, 4+, 5+, 6+.
- Minus/plus clamp at 2+ and 6+.
- The current target receives the existing active visual treatment.
- REST resets both Coaches to 2+ without changing scores.

Turn, Half, Active Coach, Undo, persistence, menus, Dice Roller and Rules remain intentionally deferred.

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

CI runs score and Offload unit tests, installs the APK in an emulator, exercises the actual score/Offload/REST controls and captures QA screenshots at 2400×1080, 2340×1080 and 1920×1080.

## v0.1.x roadmap

- v0.1.1 — Android Project Foundation — complete
- v0.1.2 — Approved Visual Shell — complete
- v0.1.3 — Scoreboard Controls — complete
- v0.1.4 — Offload Tracks & REST — current
- v0.1.5 — Turns, Half & Active Coach
- v0.1.6 — Undo, Persistence & Reset
- v0.1.7 — Phases & Actions Menus
- v0.1.8 — Final Polish & v0.1 QA
