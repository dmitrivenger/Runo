# Runo Design System — Ground Truth

Source of truth: `design/runo-design-system.png`  
Last analysed: 2026-05-07 (full rewrite — previous version was inaccurate)

---

## Critical corrections vs. previous version

| What was wrong | What the design actually shows |
|---|---|
| `Primary = #22C55E` (lime green) for buttons/FAB | Buttons/FAB/active states use **dark forest green ≈ `#166534`** |
| `onPrimary = #000000` (black on green) in dark mode | Text on dark-green buttons is **white `#FFFFFF`** in both modes |
| Map = default OSM | Map is a clean, almost monochrome topographic style, off-white base |
| `#22C55E` on buttons everywhere | `#22C55E` is **only** used for the route polyline and chart bars |

---

## Color Palette

### Dark Mode (reading the left palette strip in the image)

| Token | Hex | Notes |
|---|---|---|
| `Dark_Background` | `#0D0D0D` | Screen background — near black, cool |
| `Dark_Surface` | `#161616` | Cards, bottom sheet, nav bar |
| `Dark_SurfaceVariant` | `#222222` | Input fields, secondary cards, chip unselected bg |
| `Dark_OnBackground` | `#EBEBEB` | Primary text on dark bg |
| `Dark_OnSurface` | `#EBEBEB` | Primary text on cards |
| `Dark_OnSurfaceMuted` | `#666666` | Secondary text, labels, inactive icons |
| `Dark_Border` | `#2E2E2E` | Subtle card/input border in dark mode |

### Light Mode (reading the right palette strip)

| Token | Hex | Notes |
|---|---|---|
| `Light_Background` | `#F5F5F0` | Slightly warm off-white, NOT pure gray |
| `Light_Surface` | `#FFFFFF` | Cards, bottom sheet |
| `Light_SurfaceVariant` | `#EBEBEB` | Input fields, chip unselected bg |
| `Light_OnBackground` | `#111111` | Primary text |
| `Light_OnSurface` | `#111111` | Text on cards |
| `Light_OnSurfaceMuted` | `#6B7280` | Secondary text, placeholders |
| `Light_Border` | `#E0E0E0` | Subtle card border in light mode |

### Shared Brand Colours (reading the accent chips)

| Token | Hex | Usage |
|---|---|---|
| `Brand_Forest` | `#166534` | **Primary UI green**: buttons, FAB, active nav tab, active toggle, chips selected — NEVER on text-heavy areas |
| `Brand_ForestVariant` | `#14532D` | Pressed / ripple state on primary green elements |
| `Brand_Lime` | `#22C55E` | **Data-viz accent only**: route polyline on map, bar chart fill, sparkline — NOT for buttons |
| `Brand_Blue` | `#3B82F6` | Secondary data (pace trend line, secondary chart) |
| `Brand_Red` | `#EF4444` | Destructive / end run / error states |
| `Brand_OnForest` | `#FFFFFF` | Text on `Brand_Forest` buttons — BOTH modes (white on dark green) |

### Colour mapping to Material 3 roles

| M3 role | Dark | Light |
|---|---|---|
| `primary` | `Brand_Forest` | `Brand_Forest` |
| `onPrimary` | `Brand_OnForest (#FFFFFF)` | `Brand_OnForest (#FFFFFF)` |
| `primaryContainer` | `Brand_ForestVariant` | `Brand_ForestVariant` |
| `secondary` | `Brand_Blue` | `Brand_Blue` |
| `tertiary` | `Brand_Red` | `Brand_Red` |
| `background` | `Dark_Background` | `Light_Background` |
| `surface` | `Dark_Surface` | `Light_Surface` |
| `surfaceVariant` | `Dark_SurfaceVariant` | `Light_SurfaceVariant` |
| `onBackground` | `Dark_OnBackground` | `Light_OnBackground` |
| `onSurface` | `Dark_OnSurface` | `Light_OnSurface` |
| `onSurfaceVariant` | `Dark_OnSurfaceMuted` | `Light_OnSurfaceMuted` |
| `outline` | `Dark_Border` | `Light_Border` |
| `error` | `Brand_Red` | `Brand_Red` |

---

## Typography

**Font**: Satoshi. Fallback: **Inter** (already installed — acceptable substitute; same weight range, similar proportions).  
**Do NOT use system default fonts.**

The font is a clean humanist sans-serif. Headings and display sizes use tight negative letter-spacing. All-caps labels (DISTANCE, PACE, TIME) use positive tracking (+0.06–0.1 em).

| M3 role | Weight | Size | Line height | Letter spacing | Usage |
|---|---|---|---|---|---|
| `displayLarge` | Bold 700 | 72 sp | 78 sp | -2 sp | Countdown number |
| `displayMedium` | Bold 700 | 48 sp | 54 sp | -1.5 sp | Hero metric (large distance/time) |
| `displaySmall` | Bold 700 | 36 sp | 42 sp | -0.5 sp | Secondary hero metric |
| `headlineLarge` | SemiBold 600 | 28 sp | 34 sp | 0 sp | Screen titles ("Analytics", "Settings") |
| `headlineMedium` | SemiBold 600 | 22 sp | 28 sp | 0 sp | Card headings, sub-screen titles |
| `headlineSmall` | SemiBold 600 | 18 sp | 24 sp | 0 sp | (spare) |
| `titleLarge` | SemiBold 600 | 18 sp | 24 sp | 0 sp | Section headers, run tile distance |
| `titleMedium` | Medium 500 | 15 sp | 22 sp | 0.1 sp | Button labels, row titles |
| `titleSmall` | Medium 500 | 13 sp | 18 sp | 0.1 sp | (spare) |
| `bodyLarge` | Regular 400 | 16 sp | 24 sp | 0 sp | Onboarding body copy, descriptions |
| `bodyMedium` | Regular 400 | 14 sp | 20 sp | 0 sp | List items, secondary run tile info |
| `labelLarge` | Medium 500 | 12 sp | 16 sp | 1.0 sp | ALL-CAPS metric labels (DISTANCE, PACE) |
| `labelMedium` | Medium 500 | 11 sp | 14 sp | 0.8 sp | Tags, units (km, /km, kcal), captions |
| `labelSmall` | Regular 400 | 10 sp | 12 sp | 0.6 sp | Sub-captions, chart axis labels |

**Rule**: Metric labels (`DISTANCE`, `PACE`, `TIME`) on the active run and summary screens are rendered in `labelLarge` — **uppercase in the string itself**, NOT via `fontVariantCaps`. They use positive letter-spacing to breathe.

---

## Components

### RunoPrimaryButton

```
Background:     Brand_Forest (#166634)
Text:           Brand_OnForest (#FFFFFF), titleMedium, NOT bold
Height:         56 dp
Corner radius:  16 dp
Width:          fillMaxWidth by default
Pressed state:  scale 0.97f, background Brand_ForestVariant
Disabled:       50% alpha on both background and text
Padding H:      24 dp (icon + text layout when icon present)
Icon gap:       8 dp between icon and text
Shadow:         none in dark mode; elevation 2dp in light mode
```

### RunoSecondaryButton (outlined)

```
Background:     Transparent
Border:         1.5 dp, Brand_Forest
Text:           Brand_Forest, titleMedium
Height:         56 dp
Corner radius:  16 dp
Pressed:        Brand_Forest bg at 10% alpha fill
```

### RunoDestructiveButton (End Run)

```
Background:     Transparent
Border:         1.5 dp, Brand_Red
Text:           Brand_Red, titleMedium
Height:         56 dp
Corner radius:  16 dp
```

### RunoCard

```
Dark mode:
  Background:     Dark_Surface (#161616)
  Border:         1 dp, Dark_Border (#2E2E2E)
  Elevation:      0 dp
Light mode:
  Background:     Light_Surface (#FFFFFF)
  Border:         none
  Elevation:      1 dp (very subtle shadow)
Corner radius:  16 dp (standard), 20 dp (hero stats card on Home)
Padding:        16 dp all sides
```

### RunoTextField

```
Background:     surfaceVariant (Dark #222222 / Light #EBEBEB)
Border:         1 dp, outline color (muted) by default
                1.5 dp, Brand_Forest when focused
Corner radius:  12 dp
Height:         56 dp
Label style:    bodyMedium, onSurfaceVariant — floats to top on focus
Text style:     bodyLarge, onSurface
No filled Material3 indicator line — use OutlinedTextField styling
```

### RunoBottomNav

```
Background:     surface color
Height:         64 dp (content) + navigation bar inset
Top divider:    1 dp, outline color
3 tabs:         Home (house icon), Analytics (bar chart), Profile (person)
Active:         Brand_Forest icon (24 dp) + titleSmall label below
Inactive:       onSurfaceVariant icon (24 dp), NO label
Transition:     crossfade icons, no slide
Icon style:     Rounded filled (Material Icons Rounded)
```

### RunoTopBar (used on Settings, RunDetail, etc.)

```
Background:     background color (transparent — blends with screen)
Height:         56 dp
Back icon:      ArrowBack, 24 dp, onBackground color
Title:          headlineLarge, onBackground — shown on most screens as
                a separate heading below the bar, not inside it
Elevation:      0 dp always
```

### RunoMetricBlock (big number + label pattern)

```
Label row:      labelLarge, onSurfaceVariant, uppercase, letter-spaced
                above the number (e.g. "DISTANCE")
Value row:      displayMedium or displaySmall, onSurface or primary,
                Bold 700
Unit row:       labelMedium, onSurfaceVariant, below value
Vertical gap:   4 dp between label→value, 2 dp between value→unit
```

### RunoToggle

```
Track ON:       Brand_Forest
Track OFF:      surfaceVariant
Thumb:          White #FFFFFF
Sizing:         Standard Material 3 Switch
```

### RunoChip (period selector, unit selector)

```
Selected:
  Background:   Brand_Forest at 12% alpha
  Border:       1.5 dp, Brand_Forest
  Text:         Brand_Forest, labelLarge

Unselected:
  Background:   surfaceVariant
  Border:       none
  Text:         onSurfaceVariant, labelLarge

Corner radius:  50 dp (full pill)
Padding:        horizontal 18 dp, vertical 8 dp
```

---

## Layout Tokens

| Token | Value |
|---|---|
| Screen horizontal padding | 20 dp |
| Section vertical gap | 24 dp |
| Card internal padding | 16 dp |
| Item vertical gap (within a section) | 12 dp |
| Tight gap (label → value) | 4 dp |
| Corner radius — card / button | 16 dp |
| Corner radius — hero stat card | 20 dp |
| Corner radius — input field | 12 dp |
| Corner radius — chip / pill | 50 dp |
| Corner radius — badge / tag | 8 dp |
| FAB size | 72 dp |
| Bottom nav height (excl. inset) | 64 dp |
| Min touch target | 48 dp |

---

## Map Style

The design shows a **clean, near-monochrome topographic map**, NOT the default colourful OpenStreetMap raster.

### Visual characteristics
- Base land: very slightly warm off-white `#F5F5EE`
- Roads (major): thin light gray `#DEDED6`
- Roads (minor): near-invisible `#E8E8E2`
- Water: extremely subtle pale blue-gray `#D4E8EF`
- Terrain: no fill, no exaggerated shading — just faint contour lines if any
- Zero POI markers, zero business logos, zero transit icons, zero labels
- Route polyline: `#22C55E` (Brand_Lime), 10 dp width, round cap/join

### Dark mode map
- Base: dark cool gray `#1A1D21`
- Roads: slightly lighter `#272B32`
- Water: very dark navy `#182028`
- Route: same `#22C55E` — stands out strongly

### Implementation
- Use MapLibre with a custom style JSON (not default tile URL)
- Light style: `res/raw/map_style_light.json`
- Dark style: `res/raw/map_style_dark.json`
- Load the correct style based on `darkTheme` flag passed to the map
- Camera follows runner at zoom 16 during active run
- Post-run static view: fit bounds with 64 dp padding

---

## Screen-by-screen notes

### Home (dark)
- Greeting row: `labelLarge` muted time-of-day text ("Good morning") above `headlineLarge` name
- Avatar circle: 44 dp, primary at 15% alpha fill, initial letter
- Hero stats card: 20 dp corner radius, `surfaceVariant` bg (not `surface`)
  - "Total distance" → `labelLarge` muted uppercase
  - Big km number → `displayMedium` primary colour (Brand_Forest)
  - Sub-stat row: two mini cards with `headlineMedium` value + `labelMedium` label
- Section heading "Recent runs" → `titleLarge`
- Run tiles: 16 dp radius, `surface` bg, 12 dp vertical gap
- FAB: 72 dp circle, Brand_Forest bg, white + icon

### Active Run (dark)
- Map occupies full screen edge-to-edge including behind system bars
- Bottom sheet: `topStart=28 topEnd=28` rounded, `surface` bg at 97% alpha
  - Drag handle pill: 36×4 dp, `onSurfaceVariant` at 30% alpha
  - Metric row: 3 columns separated by 1 dp × 48 dp dividers
    - Each column: `labelLarge` uppercase muted label / `displaySmall` value / `labelMedium` unit
  - Running state: stop icon-button (56 dp, tertiary tinted circle) + pause FAB (72 dp, primary)
  - Paused state: Resume primary button + End Run destructive outlined button, stacked with 12 dp gap
- PAUSED badge: pill, `#EF4444` at 92% alpha, white `labelLarge` Bold text

### Analytics (used as tab — inside Scaffold, already safe-drawn)
- First item: `Spacer(28dp)` → "Analytics" `headlineLarge` → `PeriodChips`
- Hero card: `surfaceVariant` bg, 20 dp radius
  - Period label `labelLarge` muted
  - Big km → `displayMedium` primary
  - Two sub-stat mini cards inside
- Distance bar chart: bars filled Brand_Lime (`#22C55E`), NOT Brand_Forest
- Pace trend line: Brand_Blue

### Summary / RunDetail (light mode in design)
- Background slightly warm `Light_Background`
- "Run Complete" / date heading at top
- Same metric grid pattern as above
- Done button: full-width RunoPrimaryButton at bottom

### Settings / Voice Coach (light mode)
- Each section: `titleLarge` section header outside the card, `RunoCard` for grouped rows
- Toggle rows: icon + text column (weight 1f) + RunoToggle
- Chip rows: `RunoChip` for unit/interval selection

### Welcome (dark)
- Full bleed atmospheric canvas hero (top 58%)
- Logo mark Box: 64 dp, 16 dp radius, Brand_Forest bg, "R" in `displaySmall`
- Bottom card: NOT a card — just a dark bottom overlay with text + button
  - "Welcome to Runo" → `headlineLarge`
  - Tagline → `bodyLarge` muted
  - `RunoPrimaryButton` at bottom

### Onboarding (dark)
- Progress dots: active dot = 24 dp wide pill Brand_Forest; inactive = 8 dp circle surfaceVariant
- Step title: `headlineLarge`
- Step subtitle: `bodyLarge` muted
- `RunoTextField` for all inputs
- `RunoPrimaryButton` pinned at bottom above 36 dp safe-area spacer

---

## What must change in code (priority order)

1. **`Color.kt`**: Change `Brand_Green` → `Brand_Forest = #166534`; add `Brand_Lime = #22C55E` for route/charts; fix `Brand_OnGreenDark` → `#FFFFFF`
2. **`Theme.kt`**: Map `primary = Brand_Forest` and `onPrimary = #FFFFFF` for BOTH schemes
3. **`RunoPrimaryButton`**: Uses `colorScheme.primary` (now forest green) — text `colorScheme.onPrimary` (now white) — should automatically fix
4. **Active run bottom sheet controls**: Any hardcoded `Brand_Green` / `Color(0xFF22C55E)` references for buttons must use `colorScheme.primary`; route line colour stays as `#22C55E` literal
5. **Map style JSONs**: Create light + dark custom style files and load conditionally
6. **Typography `labelLarge`**: Increase letter-spacing to `1.0.sp` (currently `0.5.sp`)
7. **`Dark_Surface`**: Change from `#1A1A1A` to `#161616` (slightly darker, matches swatches)
