# Screen 05 — Active Run

**Reference image**: `reference.png`
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
Live tracking screen during a run. Always shown in **dark mode** regardless of system theme — outdoor visibility and battery efficiency.

## Layout

Pure black `#000000` background.

**Top — GPS indicator:**
- Small pill, top-left, ~24dp from edges
- Background: dark gray pill with `#5C9A4A` border at 30%
- Content: "GPS" text in `#5C9A4A` leaf green + three small dots (signal strength: 3 filled = strong)
- Text: 12sp Medium

**Hero distance — large number:**
- Below GPS pill, left-aligned with 24dp side padding
- Massive distance: "5.21" in **white**, 300 Light weight, ~140sp, letter-spacing -3
- Below the number, "km" in white at 50% opacity, 24sp Medium

**Map — middle section:**
- Full width minus 24dp side margins, ~360dp tall
- 20dp corner radius
- Custom dark map style:
  - Background: `#1F1F1F`
  - Roads: `#2E2E2E` (subtle)
  - Building blocks: slightly lighter than background
  - **No labels, no POIs, no business names**
- Route polyline drawn in `#5C9A4A` leaf green, **6dp thick**, **sharp corners** (not rounded — deliberate stylistic choice)
- Start point: solid leaf green dot ~12dp diameter at the start of route
- Current position (end of route): solid leaf green dot ~16dp diameter with a soft glow halo (`#5C9A4A` at 30% opacity, ~24dp radius)

**Stats row — three columns with dividers:**
- Below map, ~32dp gap
- Three equal columns separated by thin vertical dividers (`#FFFFFF @ 15%`)
- Each column has a label on top and value below:
  - **Pace** label, "6'24"" value, "/km" small subscript
  - **Duration** label, "32:18" value (timer style)
  - **Calories** label, "412" value, "kcal" small subscript
- Labels: 14sp Regular white at 70%
- Values: 32sp Bold white
- Subscripts: 14sp Regular white at 50%

**Action buttons row — at bottom:**
- Three buttons in a row, evenly distributed
- Bottom safe area: 32dp
- **Lock button (left):** outlined circle, ~56dp diameter, white border at 30%, lock icon in white centered
- **Pause button (center):** filled leaf green `#5C9A4A` circle, ~80dp diameter, white pause icon (two vertical bars) centered. Soft green glow shadow.
- **Stop button (right):** outlined rounded square, ~56dp size, white border at 30%, stop square icon in white centered

## Behavior

### Starting a run (entering this screen)
1. Show a 3-2-1 countdown overlay (large numbers, voice "Three… Two… One… Go!")
2. After countdown, GPS starts tracking
3. Voice says "Let's run" once tracking begins

### During the run
- GPS updates location every 1 second
- Route polyline grows as user moves
- Distance, pace, duration, and calories update in real-time
- **Voice feedback** at each kilometer milestone: "1 kilometer completed. Pace: 6 minutes 24 seconds per kilometer."
- **Keep screen on** while running (use `keepScreenOn`)
- Use a **ForegroundService** for tracking so it works when phone is locked

### Lock button (left)
- Tap once: dims the screen to ~30% brightness and disables all touches except a long-press
- Long-press to unlock (small "Long press to unlock" hint appears)
- Prevents accidental taps mid-run

### Pause button (center)
- Tap → pauses GPS tracking and shows the **Pause overlay (Screen 09)** on top of this screen

### Stop button (right)
- Tap → shows confirmation: "End run?" with Yes/Cancel
- Yes → save run to database, navigate to Summary (Screen 06)

### Permissions
- Request `ACCESS_FINE_LOCATION` and `ACCESS_COARSE_LOCATION` before starting a run
- Request `POST_NOTIFICATIONS` (for the foreground service notification)
- If user declines, show a friendly explainer and route them back to Home

## Calorie calculation
Use the Mifflin-St Jeor formula × MET value for running:
```
calories = (METS × weight_kg × 3.5 / 200) × duration_minutes
```
Where METS for running ≈ 8 (moderate pace).

## System bars
- Apply `WindowInsets.safeDrawing` so no controls are hidden behind status bar or system nav bar
- Status bar icons should be light (white) on this screen

## Typography
- Hero distance "5.21": 140sp Light white
- "km" label: 24sp Medium white at 50%
- Stat labels: 14sp Regular white at 70%
- Stat values: 32sp Bold white
- GPS pill: 12sp Medium leaf green

## Background services
- ForegroundService class: `RunTrackingService`
- Notification: persistent during run with current distance + pace + a "Stop" action
- Wake lock: hold partial wake lock during active run
