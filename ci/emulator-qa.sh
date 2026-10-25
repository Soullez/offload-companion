#!/usr/bin/env bash
set -euo pipefail

APK="app/build/outputs/apk/debug/app-debug.apk"
test -s "$APK"

echo "Installing $APK"
adb install -r "$APK"

launch_app() {
  adb shell am force-stop com.offload.companion
  adb shell am start -W -n com.offload.companion/.MainActivity
  sleep 2
  adb shell pidof com.offload.companion >/dev/null
  adb shell dumpsys activity activities | grep -q "com.offload.companion/.MainActivity"
}

assert_landscape_png() {
  local png="$1"
  python3 - "$png" <<'PY2'
import struct, sys
p=sys.argv[1]
with open(p,'rb') as f:
    sig=f.read(24)
if sig[:8] != b'\x89PNG\r\n\x1a\n':
    raise SystemExit(f"{p}: not a PNG")
w,h=struct.unpack(">II", sig[16:24])
print(f"{p}: {w}x{h}")
if w <= h:
    raise SystemExit(f"{p}: expected landscape screenshot, got {w}x{h}")
PY2
}

capture_size() {
  local size="$1"
  local name="$2"
  adb shell wm size "$size"
  launch_app
  adb exec-out screencap -p > "qa/$name.png"
  test -s "qa/$name.png"
  assert_landscape_png "qa/$name.png"
}

mkdir -p qa
# Suppress Android's one-time immersive-mode tutorial so QA screenshots capture the app itself.
adb shell settings put secure immersive_mode_confirmations confirmed || true
adb shell settings put global hide_error_dialogs 1 || true
launch_app
capture_size 2400x1080 landscape-2400x1080
capture_size 2340x1080 landscape-2340x1080
capture_size 1920x1080 landscape-1920x1080
adb shell wm size reset
launch_app

echo "Emulator QA passed."
