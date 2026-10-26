#!/usr/bin/env bash
set -euo pipefail

APK="app/build/outputs/apk/debug/app-debug.apk"
test -s "$APK"

echo "Installing $APK"
adb install -r "$APK"
adb shell pm clear com.offload.companion >/dev/null || true

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

capture_png() {
  local name="$1"
  adb exec-out screencap -p > "qa/$name.png"
  test -s "qa/$name.png"
  assert_landscape_png "qa/$name.png"
}

capture_size() {
  local size="$1"
  local name="$2"
  adb shell wm size "$size"
  launch_app
  capture_png "$name"
}

reset_storage() {
  adb shell pm clear com.offload.companion >/dev/null
  launch_app
}

mkdir -p qa
adb shell settings put secure immersive_mode_confirmations confirmed || true
adb shell settings put global hide_error_dialogs 1 || true
adb shell wm size 2400x1080
launch_app

# Fresh state / Undo unavailable.
assert_descs   "Undo Unavailable"   "Red Score 0" "Blue Score 0"   "Red Offload 2+ selected" "Blue Offload 2+ selected"   "Red Turn START selected" "Blue Turn START selected"   "Half H1 selected"   "Red Coach Inactive" "Blue Coach Inactive"
capture_png "undo-disabled-state"

# Score Undo: Try.
tap_desc "Red Try"
assert_descs "Red Score 5" "Undo Last Action"
capture_png "undo-available-state"
tap_desc "Undo Last Action"
assert_descs "Red Score 0" "Undo Unavailable"

# Multiple score actions undo in reverse order.
tap_desc "Red Try"
tap_desc "Red Conversion"
assert_descs "Red Score 7"
tap_desc "Undo Last Action"
assert_descs "Red Score 5"
tap_desc "Undo Last Action"
assert_descs "Red Score 0" "Undo Unavailable"

# Score correction is one committed Undo step; CANCEL adds none.
tap_desc "Red Try"
tap_desc "Red Try"
tap_desc "Red Conversion"
assert_descs "Red Score 12"
tap_desc "Red Score 12"
tap_desc "Score Correction Minus"
tap_desc "Score Correction Minus"
assert_descs "Correction Score 10"
tap_desc "Score Correction Done"
assert_descs "Red Score 10"
tap_desc "Undo Last Action"
assert_descs "Red Score 12"

tap_desc "Red Score 12"
tap_desc "Score Correction Minus"
assert_descs "Correction Score 11"
tap_desc "Score Correction Cancel"
assert_descs "Red Score 12"
tap_desc "Undo Last Action"
assert_descs "Red Score 10"

# Isolate the remaining sections on clean local storage.
reset_storage
assert_descs "Undo Unavailable" "Red Score 0"

# Offload Undo.
tap_desc "Red Offload Plus"
assert_descs "Red Offload 3+ selected" "Undo Last Action"
tap_desc "Undo Last Action"
assert_descs "Red Offload 2+ selected" "Undo Unavailable"

# REST Undo restores both differing values.
tap_desc "Red Offload Plus"
tap_desc "Red Offload Plus"
tap_desc "Red Offload Plus"
tap_desc "Blue Offload Plus"
tap_desc "Blue Offload Plus"
tap_desc "Blue Offload Plus"
tap_desc "Blue Offload Plus"
assert_descs "Red Offload 5+ selected" "Blue Offload 6+ selected"
tap_desc "REST Offload Tracks"
assert_descs "Red Offload 2+ selected" "Blue Offload 2+ selected"
tap_desc "Undo Last Action"
assert_descs "Red Offload 5+ selected" "Blue Offload 6+ selected"

# Turn and Active Coach Undo.
reset_storage
tap_desc "Red Start Turn"
assert_descs "Red Turn 1 selected" "Red Coach Active" "Undo Last Action"
tap_desc "Undo Last Action"
assert_descs "Red Turn START selected" "Red Coach Inactive" "Blue Coach Inactive" "Undo Unavailable"

tap_desc "Red Start Turn"
tap_desc "Blue Start Turn"
assert_descs "Red Turn 1 selected" "Blue Turn 1 selected" "Blue Coach Active"
tap_desc "Undo Last Action"
assert_descs "Red Turn 1 selected" "Blue Turn START selected" "Red Coach Active"

# Half transition Undo restores the exact prior H1 snapshot.
tap_desc "Red Start Turn"
tap_desc "Red Start Turn"
tap_desc "Blue Start Turn"
tap_desc "Red Start Turn"
assert_descs "Red Turn 4 selected" "Blue Turn 1 selected" "Red Coach Active" "Half H1 selected"
tap_desc "Half H2 not selected"
tap_desc "Start H2"
assert_descs "Half H2 selected" "Red Turn START selected" "Blue Turn START selected" "Red Coach Inactive" "Blue Coach Inactive"
tap_desc "Undo Last Action"
assert_descs "Half H1 selected" "Red Turn 4 selected" "Blue Turn 1 selected" "Red Coach Active"

# Build a deliberately complex H1 state for persistence validation.
reset_storage
# Red 17 = 3 tries + conversion.
tap_desc "Red Try"
tap_desc "Red Try"
tap_desc "Red Try"
tap_desc "Red Conversion"
# Blue 12 = 2 tries + conversion.
tap_desc "Blue Try"
tap_desc "Blue Try"
tap_desc "Blue Conversion"
# Red 4+, Blue 6+.
tap_desc "Red Offload Plus"
tap_desc "Red Offload Plus"
tap_desc "Blue Offload Plus"
tap_desc "Blue Offload Plus"
tap_desc "Blue Offload Plus"
tap_desc "Blue Offload Plus"
# Red turn 5, Blue turn 3, Active Blue.
for _ in 1 2 3 4 5; do tap_desc "Red Start Turn"; done
for _ in 1 2 3; do tap_desc "Blue Start Turn"; done
assert_descs   "Red Score 17" "Blue Score 12"   "Red Offload 4+ selected" "Blue Offload 6+ selected"   "Red Turn 5 selected" "Blue Turn 3 selected"   "Half H1 selected" "Blue Coach Active"
capture_png "persisted-h1-before-relaunch"

# Force-stop/relaunch must restore exact state. Undo history may reset.
launch_app
assert_descs   "Red Score 17" "Blue Score 12"   "Red Offload 4+ selected" "Blue Offload 6+ selected"   "Red Turn 5 selected" "Blue Turn 3 selected"   "Half H1 selected" "Blue Coach Active"   "Undo Unavailable"
capture_png "persisted-h1-after-relaunch"

# Persist H2 and another complex turn/active state.
tap_desc "Half H2 not selected"
tap_desc "Start H2"
tap_desc "Red Start Turn"
tap_desc "Red Start Turn"
tap_desc "Blue Start Turn"
assert_descs   "Half H2 selected"   "Red Turn 2 selected" "Blue Turn 1 selected" "Blue Coach Active"   "Red Score 17" "Blue Score 12"   "Red Offload 4+ selected" "Blue Offload 6+ selected"
launch_app
assert_descs   "Half H2 selected"   "Red Turn 2 selected" "Blue Turn 1 selected" "Blue Coach Active"   "Red Score 17" "Blue Score 12"   "Red Offload 4+ selected" "Blue Offload 6+ selected"   "Undo Unavailable"
capture_png "persisted-h2-after-relaunch"

# Reset CANCEL preserves the whole complex saved state.
tap_desc "Reset Match"
assert_descs "Reset Match Confirmation" "Cancel Reset" "Confirm Reset Match"
capture_png "reset-confirmation-dialog"
tap_desc "Cancel Reset"
assert_descs   "Half H2 selected"   "Red Turn 2 selected" "Blue Turn 1 selected" "Blue Coach Active"   "Red Score 17" "Blue Score 12"   "Red Offload 4+ selected" "Blue Offload 6+ selected"

# Confirmed Reset returns exact fresh state, clears Undo, and persists.
tap_desc "Reset Match"
tap_desc "Confirm Reset Match"
assert_descs   "Undo Unavailable"   "Red Score 0" "Blue Score 0"   "Red Offload 2+ selected" "Blue Offload 2+ selected"   "Red Turn START selected" "Blue Turn START selected"   "Half H1 selected"   "Red Coach Inactive" "Blue Coach Inactive"

launch_app
assert_descs   "Undo Unavailable"   "Red Score 0" "Blue Score 0"   "Red Offload 2+ selected" "Blue Offload 2+ selected"   "Red Turn START selected" "Blue Turn START selected"   "Half H1 selected"   "Red Coach Inactive" "Blue Coach Inactive"

# Layout regression at all required resolutions using persisted fresh state.
capture_size 2400x1080 landscape-2400x1080
capture_size 2340x1080 landscape-2340x1080
capture_size 1920x1080 landscape-1920x1080
adb shell wm size reset
launch_app

echo "Emulator Undo, persistence, Reset and full regression QA passed."
