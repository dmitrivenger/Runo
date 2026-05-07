# Screen 08 — Run Detail

**Reference image**: `reference.png`
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
Detailed view of a single completed run. Shows full route map, stats, and per-kilometer split breakdown.

## Layout

Two-tone background:
- Top portion (behind the map): deep green `#0F3D2E` (about 25% of the screen height)
- Below that: cream `#F5EFD8` (the rest)

**Top bar:**
- 24dp top safe area for status bar
- Left: back arrow icon in white, ~24dp, 24dp from left edge
- Right: share icon (upload-style arrow) in white, ~24dp, 24dp from right edge

**Map (large hero):**
- Full width minus 24dp side margins
- ~280dp tall, 20dp corner radius
- Slight overlap with the deep green band above (the map sits "in front" of the green band)
- Light topographic style:
  - Background: `#F5EFD8` cream
  - Roads: `#FFFFFF` white
  - Water: pale blue `#D6E5EC`
  - Parks: pale green `#E5EDD8`
  - **No labels showing real city names** (the AI added "Haarlem" but that should not be in the final build — use a generic style)
- Route polyline: deep green `#0F3D2E`, **6dp thick**
- Start point: solid deep green dot ~12dp diameter
- End point: solid deep green dot ~16dp diameter (slightly larger)

**Date and run name:**
- 24dp gap below map
- 24dp side padding
- Date "May 14, 2025 · 7:32 AM" in deep green at 70%, 14sp Regular
- Below: run name "Morning Run" in deep green, 700 Bold, ~36sp
- Inline pencil edit icon (~20dp, deep green) immediately after the run name
- Tap the name or pencil → opens an inline edit text input

**Stats row — four columns:**
- 16dp gap below run name
- 24dp side padding
- Four equal columns separated by thin vertical dividers (deep green at 20%)
- Each column shows: a number on top, a small label below
  - "5.21 km" / "Distance"
  - "32:18" / "Time"
  - "6'24"" / "Avg Pace"
  - "412" / "Calories"
- Numbers: 24sp Bold deep green
- Subscript units (the "km" in "5.21 km"): 14sp Medium deep green at 70%
- Labels below: 12sp Regular deep green at 60%

**"Splits" section:**
- 32dp gap below stats row
- "Splits" heading, deep green, 600 SemiBold, ~28sp, 24dp left padding
- 16dp gap below heading

**Splits list:**
- Each split row is a white pill-shaped row, full width minus 24dp side margins
- ~56dp tall, 28dp corner radius (full pill), soft shadow
- 12dp gap between rows
- Three sections inside each:
  - Left (16dp padding): "1 km", "2 km", etc., in deep green, 600 SemiBold, ~16sp
  - Middle: a horizontal progress bar showing relative pace
    - Light cream rail
    - Filled portion in leaf green `#5C9A4A` to varying widths (proportional to pace — slower km = wider fill, but capped to 100%)
    - 12dp tall, 6dp corner radius
  - Right (16dp padding): pace time "6'20"", "6'18"", etc., in deep green, 500 Medium, ~16sp

Sample splits (mock for first build):
- 1 km — 6'20"
- 2 km — 6'18"
- 3 km — 6'25"
- 4 km — 6'30"
- 5 km — 6'28"

**Bottom navigation:**
- Activity (document) icon should be active — this is the "saved runs" path

## Behavior

### Loading the run
- Receive run ID via navigation argument
- Load run from Room database
- If splits aren't stored yet, generate fake splits per kilometer for first build

### Editing the run name
- Tap the pencil icon or the name
- Inline TextField appears in place of the static text
- Save on done/blur, update database

### Share button
- Tap → Android system share sheet
- Share text: "I just ran 5.21 km in 32:18 with Runo!"
- Optional: include a screenshot of the map (later enhancement)

### Map
- Show the full route polyline fitted to the map bounds
- Pinch-to-zoom enabled
- Tap a point on the route → show a small bubble with that segment's pace (later enhancement)

## Typography
- Run name: 36sp Bold deep green
- Date: 14sp Regular deep green at 70%
- Stat numbers: 24sp Bold deep green
- Stat labels: 12sp Regular deep green at 60%
- "Splits" heading: 28sp SemiBold deep green
- Split km label: 16sp SemiBold deep green
- Split pace value: 16sp Medium deep green

## Components used
- Light map view with deep green polyline
- Stat row with dividers
- Splits list (white pill rows)
- Bottom navigation
