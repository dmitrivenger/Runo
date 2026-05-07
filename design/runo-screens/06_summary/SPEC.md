# Screen 06 — Summary

**Reference image**: `reference.png`
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
Shown immediately after a run ends. Celebrates the user, displays the headline stats, and offers Save Run + View Details.

## Layout

Cream `#F5EFD8` background.

**Top — celebration:**
- Centered confetti icon at the top, ~32dp size, multicolor (small green/yellow/orange shapes)
- 80dp top safe area above the icon

**Headline:**
- "Great work!" centered, 700 Bold deep green `#0F3D2E`, ~44sp
- Below, subtitle "You've completed your run." centered, 16sp Regular deep green at 70%

**Hero distance:**
- Massive distance "5.21 km" centered, 700 Bold leaf green `#5C9A4A`, ~80sp
- "km" same style, slightly smaller (~48sp)

**Stats row — three columns with dividers:**
- 32dp gap below distance
- Three equal columns separated by thin vertical dividers
- Each column: label on top, value below
  - **Time** / "32:18"
  - **Avg Pace** / "6'24""
  - **Calories** / "412"
- Labels: 14sp Regular muted gray
- Values: 28sp Bold deep green

**Mini map preview:**
- 32dp gap below stats
- Centered card, ~200dp wide × 160dp tall, 16dp corner radius
- Light cream background
- Inside: a small line graph (or simple route line) in leaf green showing the route shape — generic abstract shape, not a real map for this preview
- Two green dots at start and end of the line

**Buttons — stacked:**
- 32dp gap below the map preview
- "Save Run" primary button: full-width pill, deep green fill, white text, 56dp tall
- 12dp gap
- "View Details" secondary button: full-width pill, transparent fill, deep green border, deep green text, 56dp tall
- 24dp bottom safe area

## Behavior

### Entry
- Receive run data via shared ViewModel or navigation arguments:
  - distance (m), duration (s), avgPace (s/km), calories, route polyline JSON

### Save Run
- Insert the run into the Room database
- Show a brief confirmation toast "Run saved"
- Navigate to Home (Screen 04), pop back stack so the user can't return to Summary

### View Details
- Save the run first (auto-save), then navigate to Run Detail (Screen 08) with the new run's ID

### Auto-save
- If user does nothing for 30 seconds, auto-save the run silently. Do not navigate.
- This protects against the user closing the app and losing the run.

### Hardware back
- Show "Discard run?" dialog with options [Discard, Cancel]
- Discard → navigate to Home without saving

## Database integration

This is the first screen that writes to Room. Set up:

```kotlin
@Entity(tableName = "runs")
data class Run(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String = "Run",  // user can rename later
    val startTime: Long,
    val endTime: Long,
    val durationSeconds: Int,
    val distanceMeters: Float,
    val averagePaceSecPerKm: Float,
    val caloriesKcal: Float,
    val routePolylineJson: String  // encoded list of LatLng points
)
```

DAO with `insert(run: Run): Long`, `getById(id: Long): Run`, `getAll(): Flow<List<Run>>`, `delete(id: Long)`.

## Typography
- "Great work!": 44sp Bold deep green
- Subtitle: 16sp Regular deep green at 70%
- Hero distance: 80sp Bold leaf green
- Stat labels: 14sp Regular muted
- Stat values: 28sp Bold deep green
- Buttons: 18sp SemiBold

## Components used
- Primary button (filled deep green pill)
- Secondary button (outlined deep green pill)
