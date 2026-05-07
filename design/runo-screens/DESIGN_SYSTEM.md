# Runo — Design System

Extracted from the 13 reference screens. This is the single source of truth for foundation work. Every screen spec references this file.

---

## Color palette

### Light mode (used on all screens except Screen 05 Active Run)

| Role | Hex | Usage |
|------|-----|-------|
| Background base | `#F5EFD8` | Warm cream — the dominant background on every light-mode screen |
| Card surface | `#FBF7E8` | Slightly lighter cream/off-white for elevated cards |
| Card pure white | `#FFFFFF` | Used for input fields and list rows on Settings/Voice Coach |
| Brand deep green | `#0F3D2E` | Primary brand color — buttons, headlines, accent text, icons |
| Leaf green accent | `#5C9A4A` | Bright accent — used for highlights, route lines, charts, the "Runo" word |
| Soft green tint | `#A8C290` | Lighter green for chart area-fills and subtle highlights |
| Primary text | `#0F3D2E` | All headlines and primary text use the brand deep green, not black |
| Secondary text | `#3A4A3F` | Warm dark gray for body text and subtitles |
| Muted gray | `#8A8A7E` | "View all" links, inactive labels, status text |
| Danger red | `#C0392B` | "Log Out" and destructive actions only |
| Pause map dim | `#0F3D2E @ 70%` | Dark green overlay used on the Pause screen |

### Dark mode (used on Screen 05 Active Run; system-wide dark mode also supported)

| Role | Hex | Usage |
|------|-----|-------|
| Background base | `#000000` | Pure black for active-run screen |
| Card surface | `#1A1A1A` | Slightly elevated dark for cards |
| Brand green inverse | `#5C9A4A` | The leaf green becomes primary action color in dark mode |
| Map base | `#1F1F1F` | Map background on Active Run |
| Map roads | `#2E2E2E` | Subtle road network |
| Route line | `#5C9A4A` | Bright leaf green polyline |
| Primary text | `#FFFFFF` | Pure white |
| Secondary text | `#FFFFFF @ 70%` | Muted white |
| Outlined button border | `#FFFFFF @ 30%` | Lock and stop button outlines |

---

## Typography

The design uses a **rounded geometric sans-serif** (looks like Satoshi, Cabinet Grotesk, or DM Sans). Use Satoshi as the primary, fall back to Inter, then system sans-serif.

### Type scale

| Style | Size (sp) | Weight | Letter-spacing | Used for |
|-------|-----------|--------|----------------|----------|
| Display | 80–96 | 300 Light or 700 Bold | -2 | Hero metrics ("5.21" on Active Run) |
| H1 | 36–44 | 700 Bold | -1 | Screen headlines ("Welcome to Runo", "Settings", "Analytics") |
| H2 | 24–28 | 600 SemiBold | -0.5 | Section headers ("Recent Runs", "Splits", "Your Stats") |
| Title | 18–20 | 600 SemiBold | 0 | List item labels ("Voice Coach", "Appearance"), card titles |
| Body | 15–16 | 400 Regular | 0 | Paragraph text, subtitles |
| Label | 13–14 | 500 Medium | 0 | Small inline labels ("Distance", "Time", values like "Metric") |
| Caption | 11–12 | 400 Regular | 0.5 | Date stamps, tertiary info |

Numbers (distances, times, calories) use **bolder weight** than surrounding text — usually 600 SemiBold or 700 Bold.

---

## Spacing rhythm

- Screen horizontal padding: **24dp**
- Major section gap: **32dp**
- Related items gap: **16dp**
- Inside cards: **20dp** padding
- Card gap (in lists): **12dp**
- Bottom safe area above system nav: **24dp** minimum

---

## Components

### Primary button (filled pill)

- Full pill shape (corner radius = half height, 28dp on a 56dp button)
- Fill: `#0F3D2E` deep green (light mode) or `#5C9A4A` leaf green (dark mode)
- Text: white, SemiBold, 18sp
- Optional trailing icon on right
- Soft drop shadow tinted with the fill color
- Used on: Welcome, Onboarding, Summary (Save Run), Profile (Edit Profile), Pause (Resume)

### Secondary button (outlined pill)

- Same pill shape
- Transparent fill
- 1.5dp border in `#0F3D2E` (light) or `#5C9A4A` (dark) or red `#C0392B` (destructive)
- Text in matching color
- Used on: Summary (View Details), Pause (End Run)

### Card (white surface)

- Background: `#FFFFFF` (pure white)
- Corner radius: **20dp**
- Soft drop shadow: `0 4 12 rgba(15, 61, 46, 0.08)`
- Internal padding: **20dp**
- Used on: Onboarding fields, Home recent runs, Settings rows, Voice Coach rows, Profile fields, Analytics cards

### Card (cream elevated surface)

- Background: `#FBF7E8` (slightly lighter than base cream)
- Same corner radius and shadow as white card
- Used on: containers that house multiple cards (e.g., the Settings outer container, Voice Coach outer container)

### Input field card (Onboarding/Profile style)

- White card, 20dp corner radius
- Layout: leading icon (32dp, in deep green) → vertical stack of small label (14sp regular, muted) on top + value (20sp SemiBold, deep green) below
- 20dp internal padding
- 72dp tall

### Bottom navigation bar

- Full-width pill bar with curved ends (40dp corner radius)
- Background: `#FBF7E8` cream surface with subtle shadow
- 5 items: Home, Stats, **Run** (center, raised), Activity (document), Profile (person)
- Active item: filled deep green pill (`#0F3D2E`) with white icon
- Inactive items: outline icon in deep green at ~50% opacity
- Center "Run" item: white circle floating above the bar with a runner-figure icon — visually distinct, the primary action
- Sits at the bottom of every main screen with a 24dp bottom safe area below

### Toggle switch (iOS style)

- Pill shape, 50dp wide × 30dp tall
- ON: track is `#5C9A4A` leaf green, knob is white on the right
- OFF: track is `#D5D5C8` muted gray, knob is white on the left
- Soft shadow under knob

### Segmented control (period selector / theme switcher)

- Pill-shaped container with 1.5dp `#5C9A4A` border, transparent fill
- Active segment: filled `#0F3D2E` deep green, white text
- Inactive segments: transparent, `#5C9A4A` text
- 48dp tall, full width minus 24dp side margins

### Section header

- Text in `#0F3D2E` deep green
- Weight: 600 SemiBold
- Size: 24-28sp
- Often paired with a small "View all" link on the right in `#8A8A7E` muted gray

---

## Iconography

- **Outline style**, 1.5–2dp stroke
- **Rounded line caps** and joins
- Color: `#0F3D2E` deep green by default, white on dark surfaces
- Sizes: 24dp inline, 32dp in input fields, 28dp in nav bar
- Examples used: person, age (28-circle), ruler, weight scale, sun-moon (appearance), bell, database, info, logout, speaker, megaphone, gauge, chat-bubble, microphone, play, pause, stop, lock, share, edit pencil, chevron-right, back arrow

---

## Imagery

- **Painterly oil-paint style** mountain landscape illustrations
- Used as background on screens 2 (Welcome) and 3 (Onboarding bottom)
- Earth tones with green accents, soft brushwork, calm dawn atmosphere
- NEVER photorealistic, NEVER cartoonish

---

## Map style

### Active Run (Screen 05) — dark
- Background: `#1F1F1F` very dark gray
- Roads: `#2E2E2E` (subtle, barely visible)
- Building blocks: slightly lighter dark
- No labels, no POIs, no business names
- Route line: `#5C9A4A` leaf green, **6dp thick**, sharp corners (not rounded — this is a deliberate stylistic choice)
- Start point: solid green dot with soft glow
- End point: solid green dot with larger soft glow halo

### Run Detail (Screen 08) — light
- Background: `#F5EFD8` warm cream
- Roads: `#FFFFFF` white
- Water: very pale blue `#D6E5EC`
- Parks: very pale green `#E5EDD8`
- Route line: deep green `#0F3D2E`, **6dp thick**
- Generic city only — no real landmark labels

---

## Screen-to-screen navigation map

```
Splash (1.5s) → Welcome → Onboarding → Home
                                          │
                ┌─────────────────────────┼──────────────────────┐
                ↓                         ↓                      ↓
          Recent run card          "Ready to run?"          Bottom nav
                ↓                    Active Run                  │
          Run Detail (8)              ↓     ↓                    ├─ Stats → Analytics (7)
                                    Pause   Stop                 ├─ Run → Active Run (5)
                                      ↓     ↓                    ├─ Activity → Run Detail (8)
                                    (resume) Summary (6)          └─ Profile (12)
                                              │                            │
                                       ┌──────┴──────┐                 ┌───┴────┐
                                       ↓             ↓                 ↓        ↓
                                    Save Run    View Details      Settings (10)  Edit
                                       ↓             ↓                 │
                                     Home    Run Detail (8)            ├─ Appearance (13)
                                                                       └─ Voice Coach (11)
```
