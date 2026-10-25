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
  python3 - "$png" <<'PY'
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
PY
}

mkdir -p qa

# Initial install/launch and process/activity verification.
launch_app

# Validate two Galaxy-class landscape display sizes.
adb shell wm size 2400x1080
launch_app
adb exec-out screencap -p > qa/landscape-2400x1080.png
test -s qa/landscape-2400x1080.png
assert_landscape_png qa/landscape-2400x1080.png

adb shell wm size 2340x1080
launch_app
adb exec-out screencap -p > qa/landscape-2340x1080.png
test -s qa/landscape-2340x1080.png
assert_landscape_png qa/landscape-2340x1080.png

# Restore emulator display and verify a final relaunch.
adb shell wm size reset
launch_app

echo "Emulator QA passed."
