# Screen 12 — Profile

**Reference image**: `reference.png`
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
The user's profile and lifetime stats. Reachable from the bottom nav (Profile icon) and from the avatar tap on Home.

## Layout

Cream `#F5EFD8` background.

> **Note**: The reference image shows a fake iOS status bar ("Vetrus", 3:18 AM, 100% battery). Ignore this — it's just a mockup detail. The real app should use the system's actual status bar.

**Top:**
- Back arrow `<` in deep green at the top-left (~24dp from edges)
- 24dp top safe area for status bar

**Profile photo:**
- Centered circular avatar, ~140dp diameter
- 32dp gap below the back arrow
- Soft thin border in leaf green `#5C9A4A` at 30% (subtle)
- Soft drop shadow
- If user hasn't uploaded a photo, show their initial centered in a leaf green circle as a fallback

**User name:**
- Centered below the avatar, 16dp gap
- "Dmitri" in leaf green `#5C9A4A`, 700 Bold, ~44sp

**Profile field cards (3 stacked):**
- 32dp gap below the name
- Same white pill style as Voice Coach rows: full width minus 24dp side margins, ~64dp tall, 28dp corner radius (full pill), soft shadow, 12dp gap between

Each field row:
- Leading icon in deep green (24dp)
- Label in deep green, SemiBold 20sp
- Value right-aligned in deep green, SemiBold 20sp

The 3 fields:
1. **Person icon** — "Age" — "28"
2. **Ruler icon** — "Height" — "180 cm"
3. **Weight scale icon** — "Weight" — "75 kg"

(Read-only by default. Tap "Edit Profile" button to make them editable.)

**Edit Profile button:**
- 24dp gap below the last field
- Full-width pill minus 24dp side margins, ~56dp tall
- Filled deep green `#0F3D2E`, white text "Edit Profile", 18sp SemiBold, centered
- Soft shadow

**"Your Stats" section heading:**
- 32dp gap below the button
- "Your Stats" left-aligned, 24dp left padding, 700 Bold deep green, ~36sp

**Stats card:**
- 16dp gap below heading
- Single white pill card, full width minus 24dp side margins, ~96dp tall, 24dp corner radius, soft shadow
- Two equal columns separated by a thin vertical divider in deep green at 20%
- Each column: small label on top, large value below
  - Left column: "Total Runs" label, "18" value
  - Right column: "Total Distance" label, "102.8 km" value
- Labels: 14sp Regular deep green at 70%, centered
- Values: 28sp Bold deep green, centered

24dp bottom safe area.

## Behavior

### Profile photo
- Tap → opens system photo picker
- After selection, save the URI/file to DataStore and update the avatar image
- Use Coil or similar for image loading with circle crop

### Edit Profile button
- Tap → toggles the field rows into editable state
- The button label changes to "Save"
- Each field becomes a tappable inline edit (name = text input, age/height/weight = number wheel pickers)
- Tap "Save" → validate, persist to DataStore, return to read-only state

### Stats
- Read from Room database aggregates:
  - Total Runs: COUNT of all runs
  - Total Distance: SUM(distance_meters) / 1000 → display as "X.X km"

### Back navigation
- Returns to wherever the user came from (bottom nav state preserved, or returns to Settings if reached via Settings → Edit Profile path)

## Typography
- User name: 44sp Bold leaf green
- Field labels: 20sp SemiBold deep green
- Field values: 20sp SemiBold deep green (right-aligned)
- "Edit Profile" button: 18sp SemiBold white
- "Your Stats" heading: 36sp Bold deep green
- Stats card labels: 14sp Regular deep green at 70%
- Stats card values: 28sp Bold deep green

## Components used
- Profile field row (white pill, similar to settings but with right-aligned value instead of chevron)
- Primary button (filled deep green pill)
- Stats card (custom two-column white pill)
