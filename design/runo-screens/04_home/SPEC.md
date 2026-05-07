# Screen 04 — Home

**Reference image**: `reference.png`
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
The central hub of the app. Greets the user, offers the primary "start a run" action, and shows recent run history.

## Layout

Cream `#F5EFD8` background.

**Top — greeting (24dp top padding after status bar):**
- Left side, two lines:
  - "Good morning," in muted gray `#8A8A7E`, regular ~16sp
  - User's first name "Dmitri" in deep green `#0F3D2E`, 700 Bold, ~36sp
- Right side: circular profile avatar, ~56dp diameter
  - Show user's photo if uploaded; otherwise show a default circular silhouette with the user's initial
  - Tap → navigate to Profile (Screen 12)

**Hero card — "Ready to run?" (32dp gap below greeting):**
- Full width minus 24dp side margins, ~140dp tall
- Filled deep green `#0F3D2E`, 24dp corner radius, soft shadow
- Inside, two-column layout:
  - Left: "Ready to run?" headline (white, 700 Bold, ~28sp) above subtitle "Start your next adventure." (white at 80%, 16sp Regular)
  - Right: white circular play button, ~64dp diameter, with deep green play triangle icon centered. Floating slightly outside the card on the right edge.
- Subtle topographic line texture in card background (very low contrast, optional)
- Tap anywhere on the card → navigate to Active Run (Screen 05) with countdown

**Section header — "Recent Runs":**
- Left: "Recent Runs" in deep green `#0F3D2E`, 600 SemiBold, ~24sp
- Right: "View all" in muted gray `#8A8A7E`, regular 14sp
- 32dp top margin from hero card

**Recent Runs list (3 cards):**
Each card: white `#FFFFFF`, 20dp corner radius, soft shadow, ~96dp tall, 12dp gap between.
Three-column layout inside each card:
- Left column: stacked
  - Date "May 14" in muted gray, ~14sp
  - Distance "5.21 km" in deep green, 700 Bold, ~24sp
- Middle column: pace "6'24"/km" in deep green, 500 Medium, ~14sp
- Right column: small line graph thumbnail showing pace variation over the run
  - ~96dp wide × 56dp tall
  - Cream background (`#F5EFD8`)
  - Leaf green wavy line `#5C9A4A`, 2dp stroke
  - Subtle area fill underneath at 30% opacity

Sample data for first build (mock):
- Card 1: May 14 — 5.21 km — 6'24"/km
- Card 2: May 12 — 6.15 km — 6'15"/km
- Card 3: May 10 — 4.8 km — 6'40"/km

Tap a card → navigate to Run Detail (Screen 08) with that run's ID.

**Bottom navigation:**
- Floating pill bar with 5 items
- Cream `#FBF7E8` background, soft shadow
- Items left to right: Home (active, in dark green pill), Stats (chart bars), Run (center, white circle floating up with runner-figure icon), Activity (document), Profile (person)
- Home is currently active for this screen

## Behavior

- **Greeting changes by time of day**:
  - 5am-12pm: "Good morning,"
  - 12pm-6pm: "Good afternoon,"
  - 6pm-5am: "Good evening,"
- **Pull to refresh**: re-load recent runs from local database
- **Empty state** (no runs yet): replace the 3 cards with a single illustration + text "No runs yet — start your first one!"

## Mock data note
For initial build, hardcode the 3 recent runs. We'll wire up the Room database later.

## Typography
- Greeting label: 16sp Regular muted
- User name: 36sp Bold deep green
- "Ready to run?": 28sp Bold white
- "Start your next adventure.": 16sp Regular white at 80%
- Section header: 24sp SemiBold deep green
- Run card distance: 24sp Bold deep green
- Run card date / pace: 14sp muted / Medium

## Components used
- Hero card (custom, deep green)
- Recent run card (white card style)
- Bottom navigation
