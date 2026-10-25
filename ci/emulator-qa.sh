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

dump_ui() {
  adb shell uiautomator dump /sdcard/offload-window.xml >/dev/null 2>&1
  adb exec-out cat /sdcard/offload-window.xml > /tmp/offload-window.xml
}

assert_desc() {
  local desc="$1"
  dump_ui
  python3 - "$desc" <<'PY'
import sys, xml.etree.ElementTree as ET
desc=sys.argv[1]
root=ET.parse("/tmp/offload-window.xml").getroot()
if not any(n.attrib.get("content-desc")==desc for n in root.iter("node")):
    raise SystemExit(f"Missing content-desc: {desc}")
print(f"Found: {desc}")
PY
}

tap_desc() {
  local desc="$1"
  dump_ui
  local xy
  xy="$(python3 - "$desc" <<'PY'
import re, sys, xml.etree.ElementTree as ET
desc=sys.argv[1]
root=ET.parse("/tmp/offload-window.xml").getroot()
for n in root.iter("node"):
    if n.attrib.get("content-desc")==desc:
        m=re.fullmatch(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", n.attrib.get("bounds",""))
        if not m:
            raise SystemExit(f"Bad bounds for {desc}: {n.attrib.get('bounds')}")
        x1,y1,x2,y2=map(int,m.groups())
        print((x1+x2)//2,(y1+y2)//2)
        break
else:
    raise SystemExit(f"Cannot tap missing content-desc: {desc}")
PY
)"
  read -r x y <<<"$xy"
  adb shell input tap "$x" "$y"
  sleep 0.4
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
adb shell settings put secure immersive_mode_confirmations confirmed || true
adb shell settings put global hide_error_dialogs 1 || true
adb shell wm size 2400x1080
launch_app

# Fresh Offload state.
assert_desc "Red Offload 2+ selected"
assert_desc "Blue Offload 2+ selected"
assert_desc "Red Score 0"
assert_desc "Blue Score 0"

# Red progression and upper clamp.
tap_desc "Red Offload Plus"
assert_desc "Red Offload 3+ selected"
assert_desc "Blue Offload 2+ selected"
tap_desc "Red Offload Plus"
tap_desc "Red Offload Plus"
tap_desc "Red Offload Plus"
assert_desc "Red Offload 6+ selected"
tap_desc "Red Offload Plus"
assert_desc "Red Offload 6+ selected"

# Red decrement and lower clamp.
tap_desc "Red Offload Minus"
assert_desc "Red Offload 5+ selected"
tap_desc "Red Offload Minus"
tap_desc "Red Offload Minus"
tap_desc "Red Offload Minus"
assert_desc "Red Offload 2+ selected"
tap_desc "Red Offload Minus"
assert_desc "Red Offload 2+ selected"

# Blue progression is independent.
tap_desc "Blue Offload Plus"
tap_desc "Blue Offload Plus"
tap_desc "Blue Offload Plus"
assert_desc "Blue Offload 5+ selected"
assert_desc "Red Offload 2+ selected"

# Put the tracks into differing states and capture visual highlighting.
tap_desc "Red Offload Plus"
tap_desc "Red Offload Plus"
assert_desc "Red Offload 4+ selected"
tap_desc "Blue Offload Plus"
assert_desc "Blue Offload 6+ selected"
adb exec-out screencap -p > qa/offload-active-state.png
test -s qa/offload-active-state.png
assert_landscape_png qa/offload-active-state.png

# Score changes must not alter Offload values.
tap_desc "Red Try"
assert_desc "Red Score 5"
assert_desc "Blue Score 0"
assert_desc "Red Offload 4+ selected"
assert_desc "Blue Offload 6+ selected"

# REST resets both tracks and does not alter score.
tap_desc "REST Offload Tracks"
assert_desc "Red Offload 2+ selected"
assert_desc "Blue Offload 2+ selected"
assert_desc "Red Score 5"
assert_desc "Blue Score 0"

# Scoreboard regression after REST.
tap_desc "Red Conversion"
tap_desc "Blue Try"
assert_desc "Red Score 7"
assert_desc "Blue Score 5"
assert_desc "Red Offload 2+ selected"
assert_desc "Blue Offload 2+ selected"

# Correction dialog regression.
tap_desc "Red Score 7"
assert_desc "Score Correction Red Coach"
tap_desc "Score Correction Minus"
assert_desc "Correction Score 6"
tap_desc "Score Correction Cancel"
assert_desc "Red Score 7"

# Fresh process resets v0.1.4 state; capture clean layout QA at all required sizes.
capture_size 2400x1080 landscape-2400x1080
capture_size 2340x1080 landscape-2340x1080
capture_size 1920x1080 landscape-1920x1080
adb shell wm size reset
launch_app

echo "Emulator score and Offload QA passed."
