# Offload Companion

Native Android companion application for the physical **Offload** rugby board game.

## Current version

**v0.1.3 — Scoreboard Controls**

This patch keeps the QA-approved v0.1.2 visual shell locked while adding Red/Blue Try and Conversion scoring, live MatchState-backed score displays and a score-correction dialog.

## Score behaviour

- Try = +5
- Conversion = +2
- Red and Blue scores are independent.
- Tap a SCORE box to open correction controls.
- Correction supports −1 / +1, CANCEL and DONE.
- Scores clamp at zero.
- Scoring does not change the active Coach.

## Stack

- Kotlin (AGP 9 built-in Kotlin)
- Jetpack Compose
- Android Gradle Plugin 9.4.1
- Kotlin Compose Compiler plugin 2.4.10
- Gradle 9.6.x compatible
- AndroidX Activity Compose 1.13.0
- compileSdk 36 / targetSdk 36
- minSdk 26
- Java 17

## App identity

- App: `Offload Companion`
- Package: `com.offload.companion`
- Orientation: landscape only
- Offline-first: no INTERNET permission
- Sound/haptics: not used in v0.1

## Included supplied assets

- Official Offload logo
- HVD Comic Serif Pro
- Moderniz
- Poppins Light
- Offload Colour Profile reference PDF

## Development build

From a configured Android environment:

```bash
gradle :app:testDebugUnitTest :app:assembleDebug
```

CI runs score unit tests, installs and launches the APK in an Android emulator, exercises the scoreboard/correction interactions, and captures landscape QA screenshots at 2400×1080, 2340×1080 and 1920×1080.

## v0.1.x roadmap

- v0.1.1 — Android Project Foundation — complete
- v0.1.2 — Approved Visual Shell — complete
- v0.1.3 — Scoreboard Controls — current
- v0.1.4 — Offload Tracks & REST
- v0.1.5 — Turns, Half & Active Coach
- v0.1.6 — Undo, Persistence & Reset
- v0.1.7 — Phases & Actions Menus
- v0.1.8 — Final Polish & v0.1 QA
