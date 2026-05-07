# Screen 07 — Analytics

**Reference image**: `reference.png`
**Master design system**: `../DESIGN_SYSTEM.md`

## Purpose
Performance trends across week, month, and year. Aggregated charts of distance and pace.

## Layout

Cream `#F5EFD8` background.

**Top — heading:**
- 80dp top safe area
- "Analytics" left-aligned, 24dp side padding, 700 Bold deep green `#0F3D2E`, ~44sp

**Period selector (segmented control):**
- 24dp top gap from heading
- Pill-shaped container with 1.5dp leaf green border, ~52dp tall, full width minus 24dp side margins
- Three equal segments: **Week** | **Month** | **Year**
- Active segment ("Week" by default): filled deep green `#0F3D2E`, white text, 600 SemiBold 18sp
- Inactive segments: transparent, leaf green text `#5C9A4A`

**"This Week" card:**
- 24dp top gap from selector
- White `#FFFFFF` card, full width minus 24dp side margins, 20dp corner radius, soft shadow
- Internal padding: 24dp
- Top: small label "This Week" in deep green at 70%, 14sp Medium
- Below: total "21.4 km" in deep green, 700 Bold, ~36sp
- 24dp gap, then a vertical bar chart:
  - 7 vertical bars, one per day of the week
  - Bar fill: leaf green `#5C9A4A`
  - Rounded tops on bars (4dp radius)
  - Heights vary based on each day's distance
  - Subtle highlight: tallest bar may be slightly darker
- Below bars: day labels "M T W T F S S" centered under each bar, 14sp Regular muted

**"Avg Pace" card:**
- 16dp gap below the previous card
- Same card style
- Top: small label "Avg Pace" in deep green at 70%, 14sp Medium
- Below: "6'24" /km" in deep green, 700 Bold, ~36sp
- Below the number: smooth line chart showing pace trend over the week
  - Curve in leaf green `#5C9A4A`, 2.5dp stroke, smooth/curved
  - Subtle area fill underneath at 25% opacity (gradient fading to transparent)
  - No axis labels
- ~120dp tall chart area

**Bottom navigation (same as Home):**
- Stats icon is the active one for this screen (chart-bar in green pill)

## Behavior

### Period selector
- Tap a segment to switch period
- Reload chart data based on selected period:
  - Week: last 7 days (Mon-Sun this week)
  - Month: last 30 days, bars grouped weekly (4 bars)
  - Year: last 12 months, bars per month

### Empty state
- If no runs in selected period: show illustration + "No runs yet for this period — go for a run!"

### Data source
Aggregate from Room database:
- Bar chart: SUM(distance_meters) GROUP BY day
- Line chart: AVG(avg_pace_sec_per_km) per day, showing trend

### Chart library recommendation
- **Vico** (best for Compose): https://github.com/patrykandpatrick/vico
- Or **MPAndroidChart** if simpler integration needed

## Typography
- "Analytics" heading: 44sp Bold deep green
- Period selector text: 18sp SemiBold
- Card label: 14sp Medium deep green at 70%
- Card hero number: 36sp Bold deep green
- Day letters: 14sp Regular muted

## Components used
- Segmented control
- White card
- Bar chart (custom or library)
- Line chart with area fill (custom or library)
- Bottom navigation
