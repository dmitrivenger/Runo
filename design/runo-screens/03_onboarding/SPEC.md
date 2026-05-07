# Screen 03 — Onboarding

**Reference image**: `reference.png`
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
Collects user profile (name, age, height, weight) so the app can personalize calorie calculations and greetings.

## Layout

Cream `#F5EFD8` background with the same painterly mountain illustration from Welcome blurred/faded into the lower portion (continuity).

**Top — heading:**
- 80dp top safe area
- Two-line headline, centered:
  - Line 1: "Let's get to" — H1, deep green `#0F3D2E`, 700 Bold, ~44sp
  - Line 2: "know you" — same style, slight emphasis with leaf green `#5C9A4A`
- Subtitle below, two lines, centered, ~18sp deep green:
  - "This helps us personalize"
  - "your experience."

**Middle — input field stack (4 cards):**
Each card is white `#FFFFFF`, 20dp corner radius, soft shadow, 72dp tall, 12dp gap between.
Each contains: leading icon (32dp, deep green) → label above value vertically:

1. **Person icon** — label "Name" — value "Dmitri"
2. **28-circle icon** — label "Age" — value "28"
3. **Ruler icon** — label "Height" — value "180 cm"
4. **Weight scale icon** — label "Weight" — value "75 kg"

**Bottom — CTA:**
- "Continue" primary button, full-width pill, deep green fill, white text + right-arrow icon
- Below button: three small page-progress dots (1st filled green, 2nd & 3rd muted)
- 24dp bottom safe area

## Behavior

**Tapping a field** opens an inline edit:
- Name: text keyboard
- Age: number keyboard, range 13–100
- Height: number wheel picker (50–250 cm), or number keyboard with cm suffix
- Weight: number wheel picker (20–200 kg), or number keyboard with kg suffix

**Tapping "Continue":**
- Validate all fields are non-empty and within range
- Save to DataStore: `user_name`, `user_age`, `user_height_cm`, `user_weight_kg`, `onboarded_at`
- Navigate to Home (Screen 04), pop the back stack so user can't return

**On subsequent app launches**, if `onboarded_at` exists, skip Welcome + Onboarding and go straight to Home from Splash.

## Validation
- Name: required, 1–30 chars
- Age: required, 13–100
- Height: required, 50–250
- Weight: required, 20–200
- Show inline error message if invalid (small red text below the field)

## Typography
- Headline: 44sp Bold (with "know you" in leaf green `#5C9A4A`)
- Field labels: 14sp Regular muted
- Field values: 20sp SemiBold deep green
- Continue button: 18sp SemiBold white

## Storage schema (DataStore)
```kotlin
data class UserProfile(
    val name: String,
    val age: Int,
    val heightCm: Int,
    val weightKg: Float,
    val onboardedAt: Long  // epoch millis
)
```
