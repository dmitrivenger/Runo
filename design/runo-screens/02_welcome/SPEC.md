# Screen 02 — Welcome

**Reference image**: `reference.png`
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
First interactive screen after splash. Introduces the brand and offers a single Get Started action.

## Layout

Cream `#F5EFD8` background with painterly mountain illustration filling the lower 60% of the screen.

**Top half — text content:**
- 80dp top safe area
- Two-line headline, centered:
  - Line 1: "Welcome to" — H1, deep green `#0F3D2E`, 700 Bold, ~44sp
  - Line 2: "Runo" — same style but slightly larger emphasis, ~56sp
- Tagline beneath, three short lines, centered, ~24sp deep green:
  - "Track your runs."
  - "Improve every day."
  - "Be your best."

**Bottom half — illustration:**
- Painterly oil-paint mountain landscape with a green winding path leading toward distant peaks
- Earth tones (browns, sage greens) with vibrant green grass in the foreground
- The illustration extends to the bottom edge of the screen

**Anchored at bottom:**
- Primary button "Get Started" with a right-arrow icon
- Full width minus 24dp side margins, 56dp tall
- Filled deep green pill `#0F3D2E`, white text, soft shadow
- Sits on top of the illustration with 24dp bottom margin

## Behavior
- Tap "Get Started" → navigate to Onboarding (Screen 03) with horizontal slide transition
- Hardware back button: do nothing (this is a top-level start screen)

## Implementation note
The painterly illustration should be saved as a single image asset (`welcome_background.png`) at `app/src/main/res/drawable-nodpi/welcome_background.png` and used as a full-bleed background. The text and button overlay it.

## Typography
- "Welcome to / Runo": 44sp / 56sp Bold deep green, tight letter-spacing
- Tagline lines: 24sp Regular deep green
- Button text: 18sp SemiBold white
