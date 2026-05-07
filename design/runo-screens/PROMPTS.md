# Runo — Claude Code Build Prompts

Copy each prompt and paste it to Claude Code in VS Code, **one at a time**. Wait for each to complete, test on the emulator/phone, and commit before moving to the next.

---

## FOUNDATION (run these first, in order)

### F1 — Design system foundation

```
I have the complete Runo design ready in design/screens/. Please read these 
files first:

- design/screens/README.md
- design/screens/DESIGN_SYSTEM.md

Then update the project foundation to match exactly:

1. Update Color.kt with the exact hex values from DESIGN_SYSTEM.md for both 
   light and dark mode. Replace any existing Material 3 default colors.

2. Update Theme.kt — Material 3 ColorScheme for both modes. Map the brand 
   palette correctly:
   - primary = brand deep green (#0F3D2E)
   - secondary = leaf green accent (#5C9A4A)
   - background = cream (#F5EFD8) in light, near-black in dark
   - surface = card surface
   - error = danger red

3. Update Type.kt — set up Satoshi font as the primary (use Inter as 
   fallback). Define text styles for Display, Headline, Title, Body, Label 
   matching the weights and sizes in DESIGN_SYSTEM.md.

4. Resolve any Material vs Material3 import conflicts — we use Material 3 
   ONLY across the entire project.

After this, do not build any screens yet. Tell me when foundation is done.
```

### F2 — Reusable components

```
Now create reusable Composables in app/src/main/java/com/dmitrivenger/runo/ui/components/.

Reference design/screens/DESIGN_SYSTEM.md for all specifications.

Create these components:

1. RunoPrimaryButton — filled deep green pill, optional trailing icon, soft 
   tinted shadow, press scale animation (scale to 0.97 on press)

2. RunoSecondaryButton — outlined version of primary (same shape, 1.5dp 
   border, transparent fill)

3. RunoDangerButton — outlined red variant for destructive actions

4. RunoCard — white surface card with rounded 20dp corners and soft shadow

5. RunoSettingsRow — white pill row used on Settings/Voice Coach/Profile 
   screens. Layout: leading icon → label → optional value text → chevron

6. RunoInputField — onboarding-style input field card. Layout: leading icon 
   in a 32dp box, then a vertical stack with small label above and larger 
   editable value below

7. RunoBottomNav — pill-shaped bottom navigation with 5 items. Center 
   "Run" item should be a raised white circle with the runner icon. Active 
   item shows a deep green pill highlight.

8. RunoToggle — iOS-style toggle, leaf green when on

9. RunoSegmentedControl — pill container with N equal segments. Active 
   segment filled deep green, inactive transparent with leaf green text.

10. RunoTopBar — back arrow + optional title + optional trailing icon

Each component must work in both light and dark mode using only theme colors 
from MaterialTheme.colorScheme. NEVER hardcode hex values inside the components.

Tell me when components are done.
```

### F3 — Edge-to-edge and system insets

```
Set up edge-to-edge display so no screen has content hidden behind the system 
status bar or navigation bar.

1. Call enableEdgeToEdge() in MainActivity.onCreate()

2. In the root Composable of every screen, apply:
   .windowInsetsPadding(WindowInsets.safeDrawing)

3. For the Active Run screen specifically, ensure status bar icons are LIGHT 
   (white) on the dark background

4. For light-mode screens, ensure status bar icons are DARK (deep green or 
   black) on the cream background

5. Test that the bottom of every screen leaves at least 24dp clearance above 
   the system nav bar

Tell me when done.
```

---

## SCREENS (build in this order)

### Screen 01 — Splash

```
Build screen 01 Splash.

References:
- Visual: design/screens/01_splash/reference.png
- Spec: design/screens/01_splash/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

Match the reference exactly. The splash should auto-advance to Welcome after 
1.5 seconds (or to Home if user.onboardedAt exists in DataStore).

Use a LaunchedEffect with delay() in the SplashScreen Composable. Pop the 
splash off the back stack so user cannot return.

Tell me what to test on the phone.
```

### Screen 02 — Welcome

```
Build screen 02 Welcome.

References:
- Visual: design/screens/02_welcome/reference.png
- Spec: design/screens/02_welcome/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

The painterly mountain illustration is part of the reference image. For the 
implementation, save the bottom-half illustration as 
app/src/main/res/drawable-nodpi/welcome_background.png and use it as a 
full-bleed background image with the text and CTA overlaid.

If you can't extract the illustration from reference.png cleanly, use a 
solid cream background as a placeholder and tell me what filename to drop 
the illustration in as later.

Tap "Get Started" navigates to Onboarding.

Tell me what to test.
```

### Screen 03 — Onboarding

```
Build screen 03 Onboarding.

References:
- Visual: design/screens/03_onboarding/reference.png
- Spec: design/screens/03_onboarding/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

Set up DataStore (preferences) for user profile storage. Save name, age, 
height_cm, weight_kg, and onboardedAt (epoch millis) on Continue.

Validate as specified in SPEC.md. Show inline error text below a field if 
invalid.

Continue → navigate to Home, popUpTo("welcome") inclusive so user cannot 
return to onboarding.

On subsequent app launches, if onboardedAt exists in DataStore, Splash 
should skip to Home instead of Welcome.

Tell me what to test.
```

### Screen 04 — Home

```
Build screen 04 Home.

References:
- Visual: design/screens/04_home/reference.png
- Spec: design/screens/04_home/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

Important:
- Greeting changes by time of day (Good morning / afternoon / evening)
- Read user name from DataStore
- Mock 3 recent runs hardcoded in the ViewModel for now (no database yet)
- Use the RunoBottomNav component built in F2
- Hero "Ready to run?" card → tap navigates to Active Run with countdown

Tell me what to test.
```

### Screen 12 — Profile

```
Build screen 12 Profile.

References:
- Visual: design/screens/12_profile/reference.png
- Spec: design/screens/12_profile/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

Important:
- Read user data from DataStore (name, age, height, weight)
- Mock the avatar with a placeholder circle showing the user's initial for 
  now (we'll add photo picker later)
- Mock total runs and total distance values for now (we'll wire to DB later)
- "Edit Profile" button toggles fields between read-only and editable
- Reachable from bottom nav (Profile icon) and from Home avatar tap

Tell me what to test.
```

### Screen 10 — Settings

```
Build screen 10 Settings.

References:
- Visual: design/screens/10_settings/reference.png
- Spec: design/screens/10_settings/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

Reachable from Profile screen (or from a future menu somewhere).

For now, only these rows have functional destinations:
- Appearance → Screen 13 (placeholder OK if we haven't built it)
- Voice Coach → Screen 11 (placeholder OK if we haven't built it)
- Units → opens a Metric/Imperial bottom sheet, save to DataStore

Other rows (Notifications, Data & Storage, About Runo) navigate to a 
placeholder "Coming soon" screen.

Log Out shows a confirmation dialog and a toast for now.

Tell me what to test.
```

### Screen 13 — Appearance

```
Build screen 13 Appearance.

References:
- Visual: design/screens/13_appearance/reference.png
- Spec: design/screens/13_appearance/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

Use the LEFT layout from the reference image (segmented pill with 
sun/moon/half icons). Ignore the right-side phone-tile mockup — that was 
an alternate the AI generated.

Important:
- Theme selection applies immediately across the app (no restart needed)
- Save selection to DataStore: theme_mode = "light" | "system" | "dark"
- Read theme_mode at app launch and apply via MaterialTheme
- For accent colors: only green is fully wired; others show "Coming soon" 
  toast
- For map style: only "Outdoor" is functional for now

Tell me what to test, especially that switching themes actually rebrands 
the entire app live.
```

### Screen 11 — Voice Coach

```
Build screen 11 Voice Coach.

References:
- Visual: design/screens/11_voice_coach/reference.png
- Spec: design/screens/11_voice_coach/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

Important:
- Use Android's built-in TextToSpeech API
- All settings persist to DataStore (see SPEC.md for keys)
- "Test Voice" speaks a sample announcement based on the current Coaching 
  Style and Voice Speed
- The Active Run integration with these settings comes in a later prompt — 
  for now, just build the settings UI and persistence

Note: The reference shows two rows that are functionally similar 
("Coaching Style" with chevron and "Motivational" with chevron). Consolidate 
into ONE row labeled "Coaching Style" with "Motivational" as the value text 
on the right + chevron. Use your judgment.

Tell me what to test.
```

### Screen 05 — Active Run (UI shell only)

```
Build screen 05 Active Run — UI shell with mock tracking, no real GPS yet.

References:
- Visual: design/screens/05_active_run/reference.png
- Spec: design/screens/05_active_run/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

Implement in TWO parts:

PART A — UI shell:
- Layout matching the reference: GPS pill top-left, hero distance number, 
  map placeholder, stats row, three action buttons
- Force dark mode for THIS screen only (use a manually-applied dark 
  ColorScheme, ignore system theme)
- Use a dark gray rectangle as a placeholder where the map will go
- All text and icons in white per the spec
- Keep screen on while on this screen

PART B — Mock tracking:
- Tapping play (after a 3-2-1 countdown overlay with voice "Three… Two… 
  One… Go!") starts a fake timer
- Mock values increment realistically: distance grows at ~6:24/km pace, 
  duration counts seconds, calories grow proportionally
- Tap pause → show the Pause overlay (Screen 09 — placeholder OK for now)
- Tap stop → show "End run?" confirmation → on Yes, navigate to Summary 
  (Screen 06 — placeholder OK for now)

PART C — Real GPS (LATER prompt, NOT now):
- Real GPS, real Google Maps, real route drawing — separate prompt

For now, only PART A and PART B. Apply WindowInsets.safeDrawing.

Tell me what to test.
```

### Screen 09 — Pause overlay

```
Build screen 09 Pause as an overlay on top of Active Run.

References:
- Visual: design/screens/09_pause/reference.png
- Spec: design/screens/09_pause/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

This is NOT a separate navigation destination. It's a Composable shown 
conditionally inside the Active Run screen when the user pauses.

The overlay should dim the underlying Active Run map with a deep green 
overlay at 70% opacity.

Wire up:
- Resume → dismiss overlay, resume mock tracking
- End Run → confirmation → save mock run to a temp ViewModel, navigate to 
  Summary (Screen 06)
- Save and Exit → save run, navigate to Home

Tell me what to test.
```

### Screen 06 — Summary (with Room DB setup)

```
Build screen 06 Summary. This is also where we set up Room database for 
the first time.

References:
- Visual: design/screens/06_summary/reference.png
- Spec: design/screens/06_summary/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

Database setup:
1. Add Room dependencies (room-runtime, room-ktx, room-compiler via KSP)
2. Create the Run entity per SPEC.md schema
3. Create RunDao with insert, getById, getAll (Flow), delete
4. Create RunoDatabase with @Database annotation
5. Provide via Hilt DI (or a simple singleton if Hilt isn't set up yet)

Screen behavior:
- Receive run data via shared ViewModel from Active Run
- Save Run → insert into DB, toast confirmation, navigate to Home
- View Details → save first, then navigate to Run Detail with the new ID
- Auto-save after 30 seconds of inactivity

Tell me what to test, including verifying that the run actually persists 
across app restarts.
```

### Screen 08 — Run Detail

```
Build screen 08 Run Detail.

References:
- Visual: design/screens/08_run_detail/reference.png
- Spec: design/screens/08_run_detail/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

Important:
- Receive the run ID via navigation argument
- Load the run from Room database
- For map: use a placeholder light cream rectangle for now (real map comes 
  in the GPS prompt later) with a static green wavy line drawn on it
- For splits: if the run doesn't have stored splits yet, generate fake 
  splits per kilometer based on total distance and duration (vary pace 
  ±10s for realism)
- Inline rename: tap the pencil → opens a TextField → save on done
- Share: Android system share sheet with a message like "I just ran 5.21 
  km in 32:18 with Runo!"

This screen is also reachable from Home (tap a recent run card).

Tell me what to test.
```

### Screen 07 — Analytics

```
Build screen 07 Analytics.

References:
- Visual: design/screens/07_analytics/reference.png
- Spec: design/screens/07_analytics/SPEC.md
- System: design/screens/DESIGN_SYSTEM.md

Add Vico chart library: 
implementation("com.patrykandpatrick.vico:compose:2.0.0-alpha.28")
implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.28")

Aggregate run data from Room database:
- Bar chart (This Week): SUM(distance) GROUP BY day, last 7 days
- Line chart (Avg Pace): AVG(avg_pace) per day for the period

Empty state: if no runs yet, show "No runs yet — go for your first run!" 
with a button to navigate to Home.

Charts respect dark and light mode.

Bottom navigation (chart icon active for this screen).

Tell me what to test.
```

---

## FINAL POLISH (after all screens work)

### Real GPS for Active Run

```
Now upgrade Active Run with real GPS tracking, replacing the mock timer.

1. Add Google Maps Compose: 
   implementation("com.google.maps.android:maps-compose:6.1.0")
   implementation("com.google.android.gms:play-services-maps:19.0.0")
   implementation("com.google.android.gms:play-services-location:21.3.0")

2. Apply for a Google Maps API key. Tell me the steps to do this and where 
   to add it (probably local.properties + AndroidManifest meta-data). Make 
   sure local.properties is in .gitignore.

3. Replace the dark gray placeholder map with a real GoogleMap composable

4. Apply a custom dark map style. Save the JSON to res/raw/map_style.json. 
   Style: very dark gray base, subtle road network, no labels, no POIs. 
   Match the reference image style.

5. Use FusedLocationProviderClient to track location updates every 1 second

6. Draw the route as a Polyline in leaf green #5C9A4A, 6dp thick, sharp 
   corners

7. Update distance, pace, duration based on real GPS data (replace mock 
   timer in Active Run ViewModel)

8. Convert RunTrackingService to a real ForegroundService:
   - Persistent notification with current distance + pace + Stop action
   - Wake lock to prevent doze
   - Survives app being backgrounded

9. Permissions: ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, 
   FOREGROUND_SERVICE, FOREGROUND_SERVICE_LOCATION, POST_NOTIFICATIONS. 
   Request these at runtime before the run starts.

Save real route polyline to the Run entity in Room as JSON.

Tell me what to test, including verifying tracking works when the screen 
is locked.
```

### Voice feedback wired up

```
Wire up Voice Coach into Active Run.

During a run:
- Every X km (X = user's announcement_frequency from DataStore), trigger 
  TTS announcement
- Format depends on Coaching Style:
  - Motivational: "1 kilometer down. Pace 6 minutes 24 seconds. Keep 
    pushing!"
  - Calm: "1 kilometer completed. Pace 6 minutes 24 seconds."
  - Drill Sergeant: "One kilometer! Pace 6:24!"
  - Minimal: "1 kilometer."
- Apply user's voice_speed via TextToSpeech.setSpeechRate()
- If voice_coach_enabled is false, do nothing
- Lower music volume during announcement using AudioFocus
- "Let's run!" announcement after the 3-2-1 countdown

Test: start a run with voice on, verify announcements happen at the right 
distance and at the right speech rate.
```

### Launcher icon

```
Set the app launcher icon using design/Icon/runo-icon.png (a 1024×1024 PNG 
with the white R logo on a dark green background).

1. Use it as the foreground for an adaptive icon
2. Background color: solid #0F3D2E (dark green)
3. Update mipmap-anydpi-v26/ic_launcher.xml and ic_launcher_round.xml
4. Generate all density variants (mdpi → xxxhdpi)
5. Replace the default Android robot icon completely
6. Foreground sized at ~80% so it doesn't get cropped on round/squircle 
   phones

After this, I'll uninstall the old version of the app from my phone first, 
then reinstall to see the new icon.
```

---

## Workflow recap

For each prompt:
1. Copy → paste to Claude Code in VS Code
2. Wait for it to finish
3. ▶️ Run on emulator or phone
4. Compare against reference.png in the screen folder
5. If mismatch, screenshot the issue, paste with what's wrong
6. Once it matches, commit + push (just ask Claude: "commit and push these changes")
7. Move to next prompt

Don't let prompts pile up before testing — debugging cascading issues is brutal.
