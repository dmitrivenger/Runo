# Runo Design System

Source of truth: `design/runo-design-system.png`

---

## Screens (13 total)

### Dark Mode Screens

| # | Screen | Purpose |
|---|--------|---------|
| 01 | **Splash** | App launch — RUNO logo centred on black, runner silhouette background, tagline below |
| 02 | **Welcome** | First-launch landing — hero landscape/running image, "Welcome to Runo" heading, tagline, "Let's get started" primary button |
| 03 | **Onboarding** | "Let's get to know you" — input fields for name, age, height, weight, Continue button |
| 04 | **Home** | Main dashboard — greeting + first name, large distance stat, recent run tiles, bottom nav |
| 05 | **Active Run** | Full-screen topographic map with green route line, metric overlay (distance, pace, time), pause/stop controls at bottom |
| 06 | **Analytics** | "Great work!" header, large primary stat, weekly/monthly bar charts, period toggle |

### Light Mode Screens

| # | Screen | Purpose |
|---|--------|---------|
| 07 | **Run Paused** | Paused state overlay — "Run Paused" label, large metrics frozen, Resume + End Run buttons |
| 08 | **Settings** | List of setting rows with toggles and chevrons — units, voice coach, notifications, theme |
| 09 | **Run Paused (alt)** | Secondary paused view — confirms pause with resume/end actions |
| 10 | **Summary** | Post-run — "Run Punched!" celebration header, key stats grid, route map thumbnail, Done button |
| 11 | **Run Detail** | Full run breakdown — date/time, stats grid, full-width route map, pace-per-km breakdown |
| 12 | **Analytics (light)** | Same as #06 in light mode — weekly distance bars, average pace line |
| 13 | **Voice Coach / Appearance** | Voice coach toggle + interval selector, plus appearance/theme toggle section |

---

## Functional Features Per Screen

### 01 Splash
- Display RUNO logo and wordmark
- Auto-advance to Welcome after ~2 seconds
- No user interaction required

### 02 Welcome
- Hero image (runner / landscape)
- "Welcome to Runo" heading
- Tagline: "Every step counts toward something bigger"
- "Let's get started" button → navigates to Onboarding

### 03 Onboarding
- Text input: Name
- Number inputs: Age, Height (cm), Weight (kg)
- Goal selector (optional)
- "Continue" button — validates fields then saves to memory/DataStore
- Progress indicator (step dots)

### 04 Home
- Personalised greeting ("Good morning, [Name]")
- Total distance stat (large display)
- Recent runs list — each tile shows: date, distance, duration, pace
- "+" FAB → starts countdown → Active Run
- Bottom navigation: Home, Analytics, Profile
- Tap run tile → Run Detail

### 05 Active Run
- Full-screen MapLibre map with OSM/topo tiles
- Green polyline drawn in real time as GPS updates
- Metric overlay: Distance (km), Pace (min/km), Time (elapsed)
- Pause button (centre bottom)
- Stop button (bottom left)
- Camera follows current position

### 06 / 12 Analytics
- "Great work!" or period header
- Primary stat hero number (total distance)
- Secondary stats: runs count, avg pace
- Bar chart: distance per day/week
- Period toggle: Week / Month / Year
- Smooth bar chart animations

### 07 / 09 Run Paused
- "PAUSED" badge on map
- Metrics frozen at pause point
- Resume button (primary)
- End Run button (secondary/destructive)

### 08 Settings
- Units toggle: km / miles
- Voice coach: on/off
- Voice interval selector (every 1 km, 2 km, etc.)
- Notifications: on/off
- Theme: Dark / Light toggle
- App version info

### 10 Summary
- Celebration header ("Run Punched!" / "Run Complete")
- Stats grid: Distance, Duration, Avg Pace, Calories
- Route map thumbnail (non-interactive)
- Pace-per-km chart (if ≥ 2 km)
- "Done" button → Home

### 11 Run Detail
- Full date and time header
- Stats grid: Distance, Duration, Avg Pace, Calories
- Full-width interactive route map
- Per-km pace breakdown list
- Back navigation

### 13 Voice Coach / Appearance
- Voice Coach section: master toggle, announcement interval (every 1 km / 2 km / 5 km)
- Appearance section: Dark / Light mode toggle (live preview)
- Saved automatically via DataStore

---

## Color Palette

### Dark Mode
| Role | Hex | Usage |
|------|-----|-------|
| Background | `#0D0D0D` | Screen backgrounds |
| Surface | `#1A1A1A` | Cards, bottom sheet |
| Surface Variant | `#242424` | Input fields, secondary cards |
| Primary (Green) | `#22C55E` | Buttons, active states, route line |
| Primary Variant | `#16A34A` | Pressed/hover state |
| Secondary (Blue) | `#3B82F6` | Secondary data, charts |
| Accent (Red) | `#EF4444` | Stop button, alerts |
| On Background | `#F0F0F0` | Primary text |
| On Surface Muted | `#6B6B6B` | Secondary text, labels |
| On Primary | `#000000` | Text on green buttons |

### Light Mode
| Role | Hex | Usage |
|------|-----|-------|
| Background | `#F5F5F5` | Screen backgrounds |
| Surface | `#FFFFFF` | Cards |
| Surface Variant | `#EFEFEF` | Input fields, secondary cards |
| Primary (Green) | `#22C55E` | Buttons, active states, route line |
| Primary Variant | `#16A34A` | Pressed state |
| Secondary (Blue) | `#3B82F6` | Charts, secondary data |
| Accent (Red) | `#EF4444` | Stop, alerts |
| On Background | `#111111` | Primary text |
| On Surface Muted | `#6B7280` | Secondary text, labels |
| On Primary | `#FFFFFF` | Text on green buttons |

---

## Typography

**Font family: Satoshi** (use Inter as fallback if unavailable; do NOT use system default)

| Style | Weight | Size | Usage |
|-------|--------|------|-------|
| Display Large | Bold 700 | 72 sp | Countdown numbers |
| Display Medium | Bold 700 | 48 sp | Large metric values (distance, time) |
| Display Small | Bold 700 | 36 sp | Secondary metric values |
| Headline Large | SemiBold 600 | 28 sp | Screen titles |
| Headline Medium | SemiBold 600 | 22 sp | Card headings, section titles |
| Title Large | SemiBold 600 | 18 sp | Run tile primary text |
| Title Medium | Medium 500 | 15 sp | Button labels, secondary headings |
| Body Large | Regular 400 | 16 sp | Body copy, descriptions |
| Body Medium | Regular 400 | 14 sp | List items, secondary info |
| Label Large | Medium 500 | 14 sp | Metric labels (DISTANCE, PACE, TIME) |
| Label Medium | Medium 500 | 12 sp | Tags, captions, units |

---

## Components

### Primary Button (RunoPrimaryButton)
- Background: Primary Green `#22C55E`
- Text: Black (dark) / White (light), Title Medium
- Height: 56 dp
- Corner radius: 16 dp
- Full width by default
- Pressed: scale down to 0.97, darken to Primary Variant

### Secondary Button (RunoSecondaryButton)
- Background: transparent
- Border: 1.5 dp, Primary Green
- Text: Primary Green, Title Medium
- Height: 56 dp
- Corner radius: 16 dp

### Card (RunoCard)
- Background: Surface (`#1A1A1A` dark / `#FFFFFF` light)
- Corner radius: 16 dp
- Elevation: 2 dp (light mode), 0 dp with subtle border in dark mode
- Padding: 16 dp

### Input Field (RunoTextField)
- Background: Surface Variant
- Border: 1 dp outline, on focus → Primary Green
- Corner radius: 12 dp
- Label: floats above on focus
- Height: 56 dp

### Bottom Navigation (RunoBottomNav)
- Background: Surface
- 3 items: Home (house), Analytics (chart), Profile (person)
- Active: Primary Green icon + label
- Inactive: Muted grey icon, no label
- Height: 64 dp
- Top border: 1 dp divider

### Toggle Switch (RunoToggle)
- Track ON: Primary Green
- Track OFF: Surface Variant / muted grey
- Thumb: White
- Standard Material 3 Switch sizing

---

## Icons

Style: **Rounded, filled** — 24 dp default, 32 dp for FAB context

| Icon | Usage |
|------|-------|
| Home / House | Bottom nav — Home tab |
| Bar chart | Bottom nav — Analytics tab |
| Person / Profile | Bottom nav — Profile tab |
| Play arrow | Resume run, start |
| Pause | Pause run |
| Stop / Square | End run |
| Add / Plus | FAB — start new run |
| Settings / Gear | Settings entry point |
| Arrow back | Back navigation |
| Mic / Voice | Voice coach |
| Checkmark | Completion, confirmation |
| Flame | Calories |
| Timer / Clock | Duration |
| Speed / Bolt | Pace |
| Map pin | Location, route |
| Sun | Light mode |
| Moon | Dark mode |
| Chevron right | List row navigation |

---

## Layout Patterns

| Token | Value |
|-------|-------|
| Screen horizontal padding | 20 dp |
| Card internal padding | 16 dp |
| Section vertical spacing | 24 dp |
| Item vertical spacing | 12 dp |
| Corner radius — large (cards, buttons) | 16 dp |
| Corner radius — medium (inputs, chips) | 12 dp |
| Corner radius — small (tags, badges) | 8 dp |
| Corner radius — pill (toggles, FAB) | 50 dp (full) |
| FAB size | 72 dp |
| Bottom nav height | 64 dp |
| Top app bar height | 56 dp |

---

## Map Style

- **Renderer:** MapLibre Android SDK
- **Tiles:** OpenStreetMap (free, no key) — upgrade path to Stadia Maps Alidade topo
- **Route line:** `#22C55E` (Primary Green), 10 dp width, rounded caps and joins
- **Dark mode map:** Dark/satellite-style tiles preferred (Stadia Alidade Smooth Dark)
- **Light mode map:** Light/topo tiles (Stadia Alidade Smooth)
- Camera follows runner at zoom level 16 during active run
- Post-run: camera auto-fits full route bounds with 64 dp padding
