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

assert_descs() {
  dump_ui
  python3 - "$@" <<'PY'
import sys, xml.etree.ElementTree as ET
wanted=sys.argv[1:]
root=ET.parse("/tmp/offload-window.xml").getroot()
found={n.attrib.get("content-desc") for n in root.iter("node")}
missing=[x for x in wanted if x not in found]
if missing:
    raise SystemExit("Missing content-desc: " + ", ".join(missing))
for x in wanted:
    print("Found:", x)
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
  sleep 0.25
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

# Initial match state.
assert_descs   "Red Turn START selected"   "Blue Turn START selected"   "Half H1 selected"   "Half H2 not selected"   "Red Coach Inactive"   "Blue Coach Inactive"   "Red Score 0"   "Blue Score 0"   "Red Offload 2+ selected"   "Blue Offload 2+ selected"

# Consecutive Red turns are legal and do not advance Blue.
tap_desc "Red Start Turn"
assert_descs "Red Turn 1 selected" "Blue Turn START selected" "Red Coach Active" "Blue Coach Inactive"
tap_desc "Red Start Turn"
assert_descs "Red Turn 2 selected" "Blue Turn START selected" "Red Coach Active"
tap_desc "Red Start Turn"
assert_descs "Red Turn 3 selected" "Blue Turn START selected" "Red Coach Active"

# Blue independently starts a turn.
tap_desc "Blue Start Turn"
assert_descs "Red Turn 3 selected" "Blue Turn 1 selected" "Blue Coach Active" "Red Coach Inactive"

# Switch back to Red.
tap_desc "Red Start Turn"
assert_descs "Red Turn 4 selected" "Blue Turn 1 selected" "Red Coach Active" "Blue Coach Inactive"
adb exec-out screencap -p > qa/turn-active-state.png
test -s qa/turn-active-state.png
assert_landscape_png qa/turn-active-state.png

# Establish score and Offload state, verifying Active Coach isolation.
tap_desc "Red Try"
tap_desc "Blue Conversion"
tap_desc "Red Offload Plus"
tap_desc "Red Offload Plus"
tap_desc "Blue Offload Plus"
tap_desc "Blue Offload Plus"
tap_desc "Blue Offload Plus"
assert_descs   "Red Score 5" "Blue Score 2"   "Red Offload 4+ selected" "Blue Offload 5+ selected"   "Red Turn 4 selected" "Blue Turn 1 selected"   "Red Coach Active" "Half H1 selected"

# Drive Red to OT, then prove the OT clamp.
tap_desc "Red Start Turn"
assert_descs "Red Turn 5 selected"
tap_desc "Red Start Turn"
assert_descs "Red Turn 6 selected"
tap_desc "Red Start Turn"
assert_descs "Red Turn 7 selected"
tap_desc "Red Start Turn"
assert_descs "Red Turn OT selected"
tap_desc "Red Start Turn"
assert_descs   "Red Turn OT selected" "Blue Turn 1 selected"   "Red Coach Active"   "Red Score 5" "Blue Score 2"   "Red Offload 4+ selected" "Blue Offload 5+ selected"

# H2 request can be cancelled with all match state preserved.
tap_desc "Half H2 not selected"
assert_descs "Start H2 Confirmation" "Start H2" "Cancel H2"
adb exec-out screencap -p > qa/h2-confirmation-dialog.png
test -s qa/h2-confirmation-dialog.png
assert_landscape_png qa/h2-confirmation-dialog.png
tap_desc "Cancel H2"
assert_descs   "Half H1 selected" "Half H2 not selected"   "Red Turn OT selected" "Blue Turn 1 selected"   "Red Coach Active"   "Red Score 5" "Blue Score 2"   "Red Offload 4+ selected" "Blue Offload 5+ selected"

# Confirm H2: only Turn tracks and Active Coach reset.
tap_desc "Half H2 not selected"
tap_desc "Start H2"
assert_descs   "Half H2 selected" "Half H1 not selected"   "Red Turn START selected" "Blue Turn START selected"   "Red Coach Inactive" "Blue Coach Inactive"   "Red Score 5" "Blue Score 2"   "Red Offload 4+ selected" "Blue Offload 5+ selected"
adb exec-out screencap -p > qa/h2-active-state.png
test -s qa/h2-active-state.png
assert_landscape_png qa/h2-active-state.png

# Regression after half transition: scoring, correction, Offload and REST still work.
tap_desc "Blue Try"
assert_descs "Blue Score 7" "Half H2 selected" "Blue Turn START selected"
tap_desc "Red Offload Plus"
assert_descs "Red Offload 5+ selected" "Half H2 selected"
tap_desc "Red Score 5"
assert_descs "Score Correction Red Coach" "Correction Score 5"
tap_desc "Score Correction Minus"
assert_descs "Correction Score 4"
tap_desc "Score Correction Cancel"
assert_descs "Red Score 5"
tap_desc "REST Offload Tracks"
assert_descs   "Red Offload 2+ selected" "Blue Offload 2+ selected"   "Half H2 selected"   "Red Turn START selected" "Blue Turn START selected"   "Red Coach Inactive" "Blue Coach Inactive"   "Red Score 5" "Blue Score 7"

# Fresh process resets v0.1.5 state; capture clean layout QA.
capture_size 2400x1080 landscape-2400x1080
capture_size 2340x1080 landscape-2340x1080
capture_size 1920x1080 landscape-1920x1080
adb shell wm size reset
launch_app

echo "Emulator score, Offload, Turn, Half and Active Coach QA passed."
