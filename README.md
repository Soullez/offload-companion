# Offload Companion

Native Android companion application for the physical **Offload** rugby board game.

## Current version

**v0.1.2 — Approved Visual Shell**

This patch implements the locked static Offload Companion match-screen design in native Jetpack Compose. Gameplay interactions remain intentionally deferred to later v0.1.x patches.

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

## v0.1.2 visual shell

The main screen now includes the approved branded header; Red and Blue Coach panels; static Try/Conversion controls and score displays; Red and Blue Offload Tracks with a central REST control; Red and Blue Turn Tracks with the central HALF control; and four bottom controls for Phases of Play, Actions, Dice Roller and Rules. Settings, Stats and Help remain visible but inactive.

## Development build

With Android Studio, open the project and build the `app` debug variant.

From a configured command-line Android development envelopment:

```bash
gradle :app:assembleDebug
```

CI builds, installs and launches the APK in an Android emulator and captures landscape QA screenshots at 2400×1080, 2340×1080 and 1920×1080.

## v0.1.x roadmap

- v0.1.1 — Android Project Foundation — complete
- v0.1.2 — Approved Visual Shell — current
- v0.1.3 — Scoreboard Controls
- v0.1.4 — Offload Tracks & REST
- v0.1.5 — Turns, Half & Active Coach
- v0.1.6 — Undo, Persistence & Reset
- v0.1.7 — Phases & Actions Menus
- v0.1.8 — Final Polish & v0.1 QA
