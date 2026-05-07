# Runo — Screen Specs

This folder contains the design references and implementation specs for all 13 screens of Runo.

## Folder structure

```
design/screens/
├── README.md                    ← (this file)
├── DESIGN_SYSTEM.md             ← Master color, typography, component reference
├── PROMPTS.md                   ← Ready-to-paste prompts for Claude Code in build order
│
├── 01_splash/
│   ├── reference.png            ← AI-generated visual reference
│   └── SPEC.md                  ← Implementation guide
├── 02_welcome/
│   ├── reference.png
│   └── SPEC.md
├── ... (13 screen folders)
```

## How to use these with Claude Code

**Step 1**: Drop this entire folder into your project at `Runo/design/screens/`

**Step 2**: Send Claude Code the foundation prompts (in `PROMPTS.md` — F1, F2, F3) BEFORE any screen prompts. These set up theme, components, and edge-to-edge insets correctly.

**Step 3**: For each screen, send the matching prompt from `PROMPTS.md`. Each prompt points Claude Code at:
- The visual reference (`reference.png`)
- The implementation spec (`SPEC.md`)
- The master design system (`DESIGN_SYSTEM.md`)

**Step 4**: After Claude finishes a screen, test it on your phone or emulator. Compare with the reference image. If something's off, screenshot and ask Claude to fix it.

**Step 5**: Commit each working screen to git before moving to the next.

## Build order

The recommended order (also in PROMPTS.md):

1. **Foundation** (theme, components, insets)
2. **Splash** — simplest screen, validates the foundation
3. **Welcome** — establishes hero illustration handling
4. **Onboarding** — introduces input fields and DataStore
5. **Home** — most-used screen, mock data initially
6. **Profile** + **Settings** + **Appearance** — simple list screens, build skill
7. **Voice Coach** — adds TTS integration
8. **Active Run** — most complex; build UI shell first, GPS later
9. **Pause** — overlay on Active Run
10. **Summary** — first screen that writes to Room database
11. **Run Detail** — first screen that reads from Room database
12. **Analytics** — needs chart library and aggregations
13. **(Final pass)** — wire up real GPS, real voice feedback, polish

## Why this order?

- Each screen builds on the foundation laid by the previous one
- Database setup happens midway when we actually need it (not upfront)
- The most complex screens (Active Run, Analytics) come last when you've got momentum
- Voice Coach and Active Run integration are split: configure first, then wire up

## What's NOT in this folder

- Real backend integration (Supabase auth, cloud sync) — comes later
- App icon — uses the existing `design/Icon/runo-icon.png`
- Marketing assets (App Store screenshots, etc.)

## Maintenance

If you regenerate any screen reference:
1. Replace the `reference.png` in that screen's folder
2. Re-read its `SPEC.md` and update if the layout changed
3. Send Claude Code a "rebuild this screen" prompt with the updated reference
