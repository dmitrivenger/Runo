# Screen 10 — Settings

**Reference image**: `reference.png`
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
App preferences and account options. Entry point for sub-screens like Appearance, Voice Coach, Notifications, etc.

## Layout

Cream `#F5EFD8` background with the entire content sitting inside an elevated cream `#FBF7E8` container that has rounded corners — the whole "settings panel" looks like a single card on top of the main background.

**Top of the card:**
- 24dp top padding
- Left: back arrow icon (`<`) in deep green, ~24dp
- Right: search icon (magnifying glass) in deep green, ~24dp

**Heading:**
- "Settings" left-aligned, 700 Bold deep green, ~44sp, 24dp left padding

**Settings rows:**
A vertical stack of 6 white pill-shaped rows, full width minus 24dp side margins, ~64dp tall, 28dp corner radius (full pill), 12dp gap between.

Each row layout (left to right):
- 20dp internal padding
- Leading line icon (~24dp) in deep green
- Label in deep green, 600 SemiBold, ~20sp
- (Optional) value text in deep green at 70%, 16sp Regular, right-aligned before the chevron
- Chevron arrow `>` in deep green at 70%

**The 6 rows in order:**
1. **Sun-with-moon icon** — "Appearance" — chevron only → navigates to Appearance (Screen 13)
2. **Ruler icon** — "Units" — value "Metric" → opens a sheet/dialog with Metric/Imperial toggle
3. **Speaker icon** — "Voice Coach" — value "On" → navigates to Voice Coach (Screen 11)
4. **Bell icon** — "Notifications" — chevron only → navigates to Notifications (placeholder for now)
5. **Database/disk-stack icon** — "Data & Storage" — chevron only → navigates to Data & Storage (placeholder)
6. **Info-circle icon** — "About Runo" — chevron only → navigates to About (placeholder)

**Log Out — separate row:**
- 24dp gap below the last regular row (visually separated)
- Same pill shape and padding
- Logout icon in red `#C0392B` (looks like an arrow exiting a box)
- Label "Log Out" in red `#C0392B`, 600 SemiBold, 20sp
- No value, no chevron

24dp bottom safe area.

## Behavior

### Each row
- Tap navigates to the appropriate sub-screen or opens a sheet
- For now, only Appearance, Voice Coach, and Units have functional destinations. The others can route to placeholder screens with "Coming soon".

### Units toggle
- Show a bottom sheet with two options: Metric / Imperial
- Save selection to DataStore
- Affects how distances and weights are displayed throughout the app

### Voice Coach value
- Read from DataStore: shows "On" if voice coach is enabled, "Off" if disabled
- Tap navigates to Voice Coach screen where the user can toggle and configure

### Log Out
- Show confirmation: "Log out of Runo?"
- [Log Out, Cancel]
- For now (no backend yet), this just shows a toast "Logged out". When Supabase is added later, this will actually clear the session and route to Welcome.

## Search icon
- The magnifying glass at the top right is a placeholder for searching settings — implement as no-op for now (or a toast "Search coming soon")

## Typography
- "Settings" heading: 44sp Bold deep green
- Row labels: 20sp SemiBold deep green
- Row values: 16sp Regular deep green at 70%
- Log Out label: 20sp SemiBold red

## Components used
- White pill row (settings list item) — likely a new reusable component `SettingsRow`
- Custom row variant for destructive actions (red Log Out)
