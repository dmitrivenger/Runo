# Screen 13 — Appearance

**Reference image**: `reference.png` (use the **LEFT** layout — segmented pill with sun/moon icons. Ignore the right-side "phone tiles" version.)
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
Choose the app's visual mode (Light / System / Dark) and accent color. Reachable from Settings → Appearance.

## Layout

Cream `#F5EFD8` background.

**Top:**
- Back arrow `<` in deep green at the top-left (~24dp from edges)
- 24dp top safe area

**Heading:**
- "Appearance" left-aligned, 24dp left padding, 700 Bold deep green, ~44sp
- Below it (8dp gap): subtitle "Choose how Runo looks on your device." in deep green at 70%, 16sp Regular

**Theme selector (segmented control):**
- 24dp gap below the subtitle
- Pill-shaped container, full width minus 24dp side margins, ~80dp tall (taller than usual to fit icons + labels)
- 1.5dp leaf green border, transparent fill background

Inside, three equal segments:

1. **Light** (selected by default if user prefers light)
   - Icon: sun (line style, ~28dp)
   - Label: "Light"
   - Selected state: filled deep green `#0F3D2E` segment, white sun icon and white text
2. **System** (default for new users)
   - Icon: half-sun-half-moon (line style)
   - Label: "System"
   - Inactive state: transparent, deep green icon and text
3. **Dark**
   - Icon: moon (line style)
   - Label: "Dark"
   - Inactive state: transparent, deep green icon and text

Layout inside each segment: icon centered above the label.

**Accent Color section:**
- 32dp gap below segmented control
- "Accent Color" heading left-aligned, 24dp left padding, 600 SemiBold deep green, ~24sp
- 16dp gap below heading

**Color circle row:**
- 5 circles evenly distributed across the row, ~48dp diameter each, 24dp side padding overall
- Colors:
  1. **Leaf green** `#5C9A4A` — selected (has a small white check icon `✓` centered, plus a thin ring around it)
  2. **Blue** `#3B82F6`
  3. **Purple** `#A855F7`
  4. **Soft red/coral** `#E76F51`
  5. **Orange** `#F97316`
- Tap a circle to select that accent. For now, only green is fully wired up — others are visible but show a "Coming soon" toast on tap.

**Map Style section:**
- 32dp gap below the color row
- "Map Style" heading left-aligned, 24dp left padding, 600 SemiBold deep green, ~24sp
- 16dp gap below heading

**Map Style row:**
- Single white pill row, full width minus 24dp side margins, ~64dp tall, 28dp corner radius, soft shadow
- Internal padding 20dp
- Label "Outdoor" in deep green, SemiBold 20sp (left-aligned)
- Chevron `>` on the right
- Tap → opens a bottom sheet with options:
  - Outdoor (default)
  - Streets
  - Satellite

24dp bottom safe area.

## Behavior

### Theme selection
- Tap a segment → applies immediately across the entire app
- Save selection to DataStore: `theme_mode` = "light" | "system" | "dark"
- App-wide theme reads from DataStore on launch and applies via `MaterialTheme(colorScheme = ...)`

### System mode
- When "System" is selected, the app follows the phone's OS-level theme setting
- Use `isSystemInDarkTheme()` to determine which colorScheme to apply

### Accent color (future)
- For now, only the green accent is functional
- The other colors are visible to demonstrate the future capability — tapping them shows a brief toast "Coming soon"
- When implemented, this will dynamically swap the primary color in the theme

### Map style (future)
- For now, only "Outdoor" is functional (the existing style)
- "Streets" and "Satellite" can be placeholder options

### Hot reload
- Changing theme should NOT require an app restart — it should rebuild the UI with the new colorScheme immediately

## DataStore keys
- `theme_mode`: String — "light" | "system" | "dark"
- `accent_color`: String — "green" | "blue" | "purple" | "red" | "orange"
- `map_style`: String — "outdoor" | "streets" | "satellite"

## Typography
- "Appearance" heading: 44sp Bold deep green
- Subtitle: 16sp Regular deep green at 70%
- Section headings: 24sp SemiBold deep green
- Segmented control labels: 14sp SemiBold (white when active, deep green when inactive)
- Map Style row label: 20sp SemiBold deep green

## Components used
- Segmented control (3-segment, with icon + label per segment)
- Color circle picker row
- Settings row (white pill, reused from Settings screen)
