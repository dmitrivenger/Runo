# Screen 09 — Pause

**Reference image**: `reference.png`
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
A modal **overlay** shown when the user pauses an active run from Screen 05. NOT a separate navigation destination — this overlays whatever screen the user paused from.

## Layout

The screen behind (Active Run map + stats) is visible but **dimmed**:
- Add a `#0F3D2E` deep green overlay at 70% opacity over the entire underlying screen
- This gives the dark green tinted look you see in the reference

**Pause modal — centered text and buttons:**
- Centered vertically on the screen
- "Run Paused" headline, white, 700 Bold, ~44sp
- 8dp gap, then "What would you like to do?" subtitle, white at 80%, 18sp Regular

**Action buttons — stacked, centered:**
- 32dp gap below subtitle

1. **Resume** primary button:
   - Full-width pill minus 32dp side margins, ~56dp tall
   - Filled leaf green `#5C9A4A` (note: lighter green than usual primary — this stands out on the dim background)
   - White text "Resume", 18sp SemiBold, centered

2. **End Run** secondary button:
   - 16dp gap
   - Same shape as Resume
   - Transparent fill, 1.5dp red `#C0392B` border
   - Red text "End Run", 18sp SemiBold, centered

3. **Save and Exit** text link:
   - 24dp gap below End Run
   - Centered, no background, no border
   - Leaf green `#5C9A4A` text, 18sp Medium, **underlined**

24dp bottom safe area.

## Behavior

### Tapping Resume
- Dismiss the overlay (animate out)
- Resume GPS tracking on the underlying Active Run screen
- Voice says "Resuming"
- Hardware back button does the same

### Tapping End Run
- Confirmation dialog: "End your run? Your progress will be saved."
- [End Run, Cancel]
- End Run → save run to database → navigate to Summary (Screen 06)
- Cancel → close dialog, stay in pause overlay

### Tapping Save and Exit
- Save the run as-is to the database
- Navigate to Home (Screen 04), pop the run flow
- This is for when the user wants to bail without seeing the summary

### While paused
- GPS is paused — duration timer stops
- The user's distance/pace/calories on the underlying screen freeze
- The foreground service notification updates to show "Paused"

## Implementation note
This is a Compose **overlay**, not a navigated screen. Implement as a Composable shown conditionally on top of Active Run when `isPaused == true` in the ViewModel.

```kotlin
if (isPaused) {
    PauseOverlay(
        onResume = { ... },
        onEndRun = { ... },
        onSaveAndExit = { ... }
    )
}
```

The overlay should have a semi-transparent background that dims the layer below.

## Typography
- "Run Paused": 44sp Bold white
- Subtitle: 18sp Regular white at 80%
- Resume / End Run buttons: 18sp SemiBold
- Save and Exit link: 18sp Medium leaf green, underlined

## Components used
- Overlay modal (custom)
- Primary button (filled leaf green pill — note color difference from default primary)
- Secondary button (red outlined pill — destructive variant)
- Text link
