# Screen 01 — Splash

**Reference image**: `reference.png` in this folder
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
First screen on app launch. Shown for ~1.5 seconds, then auto-advances to Welcome (or Home if user is already onboarded).

## Layout

Cream background `#F5EFD8` filling the entire screen.

Centered vertically:
- A soft warm yellow/cream radial glow behind the logo (subtle, like sunlight)
- The Runo "R" logo mark (deep green, with three small horizontal speed lines to its left)
- Wordmark "RUNO" below the logo, large bold deep green, with wide letter-spacing
- Tagline beneath: "Every step counts toward something bigger" in deep green at ~80% opacity

Below the wordmark area:
- A thin horizontal line with a small dot in the middle (decorative divider)

Bottom third:
- Three small dots horizontally arranged — first one filled deep green, the others muted (loading indicator)

## Behavior

- Display for **1500ms minimum**, **2500ms maximum**
- Auto-advance:
  - If user has completed onboarding → navigate to Home (Screen 04)
  - Otherwise → navigate to Welcome (Screen 02)
- No interactivity on this screen
- Use `popUpTo("splash") { inclusive = true }` so user cannot navigate back

## Colors used
- Background: `#F5EFD8`
- Logo + wordmark + tagline: `#0F3D2E`
- Glow: warm cream/yellow at low opacity
- Loading dots: `#0F3D2E` for active, `#0F3D2E @ 25%` for inactive

## Typography
- Wordmark "RUNO": 56sp, Bold, letter-spacing +6
- Tagline: 16sp, Regular

## Optional animation
- Logo and wordmark fade in over 400ms on screen appear
- Loading dots animate in sequence (cycling fill, 1.2s loop)
