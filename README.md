# Offload Companion

Native Android companion application for the physical **Offload** rugby board game.

## Current version

**v0.1.1 — Android Project Foundation**

This patch establishes the native Android/Kotlin/Jetpack Compose foundation. The approved scoreboard visual shell is intentionally deferred to v0.1.2.

## Stack

- Kotlin (AGP 9 built-in Kotlin)
- Jetpack Compose
- Android Gradle Plugin 9.4.1
- Kotlin Compose Compiler plugin 2.4.10
- Gradle 9.6.x compatible
- Compose BOM 2026.09.00
- AndroidX Activity Compose 1.13.0
- compileSdk 37 / targetSdk 36
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

With Android Studio, open the project and build the `app` debug variant.

From a configured command-line Android development environment:

```bash
gradle :app:assembleDebug
```

The expected debug APK path is:

```text
app/build/outputs/apk/debug/app-debug.apk
```

For CI, `.github/workflows/android.yml` installs Java/Android SDK/Gradle and builds the debug APK.

## v0.1.x roadmap

- v0.1.1 — Android Project Foundation
- v0.1.2 — Approved Visual Shell
- v0.1.3 — Scoreboard Controls
- v0.1.4 — Offload Tracks & REST
- v0.1.5 — Turns, Half & Active Coach
- v0.1.6 — Undo, Persistence & Reset
- v0.1.7 — Phases & Actions Menus
- v0.1.8 — Final Polish & v0.1 QA
