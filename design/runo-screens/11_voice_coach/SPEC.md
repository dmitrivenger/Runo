# Screen 11 — Voice Coach

**Reference image**: `reference.png`
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
Configure the voice that announces stats during runs (TextToSpeech).

## Layout

Cream `#F5EFD8` base with the same elevated `#FBF7E8` container card style as Settings.

**Top:**
- Back arrow `<` (top-left, deep green, ~24dp)
- Search icon (top-right, deep green, ~24dp) — placeholder, can be no-op

**Heading:**
- "Voice Coach" left-aligned, 700 Bold deep green, ~44sp, 24dp left padding

**Settings rows (white pill-shaped cards):**

Same row style as Settings screen — white pill, full width minus 24dp side margins, soft shadow, 12dp gap between rows.

### Row 1 — Voice Coach toggle
- Speaker icon (left)
- Label "Voice Coach" in deep green, SemiBold 20sp
- iOS-style toggle on the right
  - ON state shown by default: leaf green track, white knob to the right
- Tapping toggles voice coach on/off, save to DataStore

### Row 2 — Announcement Frequency
- Megaphone icon (left)
- Label "Announcement Frequency" in deep green, SemiBold 20sp
- Right side: value "Every 1 km" in deep green at 70%, then chevron `>`
- Tap → opens a bottom sheet with options:
  - Every 0.5 km
  - Every 1 km (default)
  - Every 2 km
  - Every 5 km

### Row 3 — Voice Speed (taller card)
- Speed/gauge icon (left)
- Label "Voice Speed" in deep green, SemiBold 20sp
- **Below the label, inside the same card**, a horizontal slider:
  - Light cream rail
  - Filled portion in leaf green `#5C9A4A` to about 45%
  - Round white knob with soft shadow
- Right of the slider: current value text "0.9x" in deep green, 600 SemiBold, ~20sp
- Slider range: 0.5x to 2.0x in increments of 0.1
- Default: 1.0x

### Row 4 — Coaching Style
- Chat-bubble icon (left)
- Label "Coaching Style" in deep green, SemiBold 20sp
- Right: chevron `>`
- Tap → opens a bottom sheet with options:
  - Motivational (default)
  - Calm
  - Drill Sergeant
  - Minimal

### Row 5 — Current style indicator
- Speaker icon (left) — same style as row 1
- Label "Motivational" in deep green, SemiBold 20sp
- Right: chevron `>`
- This row visually echoes the user's current selection. Tap → same bottom sheet as row 4.
- *(Note: rows 4 and 5 are functionally redundant in the reference design. For implementation, you may consolidate into one row labeled "Coaching Style" with the current value shown on the right. Use your judgment.)*

### Row 6 — Test Voice
- Microphone icon (left)
- Label "Test Voice" in deep green, SemiBold 20sp
- Right side: small filled leaf green circle (~36dp diameter) with a white play triangle inside
- Tap → speaks a sample announcement using current voice settings

24dp bottom safe area.

## Behavior

### Test Voice action
Use Android's built-in TextToSpeech API. Sample announcement based on current Coaching Style:
- **Motivational**: "1 kilometer down. Pace 6 minutes 24 seconds. Keep pushing!"
- **Calm**: "1 kilometer completed. Pace 6 minutes 24 seconds."
- **Drill Sergeant**: "One kilometer! Pace 6:24! Pick it up!"
- **Minimal**: "1 kilometer."

Apply the user's voice speed via `TextToSpeech.setSpeechRate(speed)`.

### Persistence
All settings saved to DataStore:
- `voice_coach_enabled: Boolean`
- `voice_frequency_km: Float` (0.5, 1.0, 2.0, 5.0)
- `voice_speed: Float` (0.5–2.0)
- `voice_style: String` ("motivational", "calm", "drill_sergeant", "minimal")

### Used elsewhere
The Active Run screen reads these settings to know when and how to announce milestones during the run. (Implemented in a follow-up prompt after this screen is built.)

## Typography
- "Voice Coach" heading: 44sp Bold deep green
- Row labels: 20sp SemiBold deep green
- Slider value: 20sp SemiBold deep green
- Sheet options: 18sp Medium

## Components used
- White pill row (`SettingsRow` reusable from Settings screen)
- Toggle switch
- Slider with value indicator
- Inline play button (small green circle with white play icon)
