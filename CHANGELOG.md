# Changelog

## 1.6.1

**New**
- **Always LMB / RMB** setting (off by default): the attack and use keys are always labelled LMB and
  RMB, even when they're bound to keyboard keys. The CPS still counts those keyboard presses,
  key repeat included.
- Quilt is now officially supported.

## 1.6.1

**New**
- **Always LMB / RMB** setting (off by default): the attack and use keys are always labelled LMB and
  RMB, even when they're bound to keyboard keys. The CPS still counts those keyboard presses,
  key repeat included.
- Quilt is now officially supported.

## 1.6

**New**
- In-game settings screen — press **Right Shift** (rebindable), or open it from **Mod Menu**.
- Mod Menu integration (optional — KeyCPS still works without it).
- Minecraft **26.1, 26.1.1, 26.1.2, 26.2 and 26.3** support.
- **Key repeat counting on every version**: holding a keyboard-bound attack/use key counts the OS
  key-repeat rate (aaaaaaa…). 1.21.11 was missing this before.
- Optional **rate on every key** — see how often W/A/S/D fire per second, repeats included.
- Optional **sneak / sprint** row.
- **CPS warning**: the counter turns red at a threshold you choose.
- Keys show what they're actually bound to (AZERTY → Z Q S D, attack on F → "F", …).
- New keybinds: *Open Settings*, *Toggle HUD*, *Move HUD*.
- One-time tip in chat on first join telling you how to open the settings.
- Translations: English, German, French, Spanish, Portuguese (BR), Italian, Dutch, Polish,
  Turkish, Russian, Ukrainian, Chinese (Simplified), Japanese, Korean.

**Better looking**
- Rounded keys, smooth press/release fade, CPS shown inside the mouse buttons, the jump key drawn
  as a space bar, text shadow, optional rainbow text. Scaling now scales the text too.
- The HUD starts in the **top-left corner** on first launch.
- Move screen: drag the HUD anywhere; it snaps to the edges and the centre.

**Fixed**
- Very fast clicks (shorter than one frame) were sometimes missed — clicks are now counted from
  input events, not sampled once per frame.
- Typing in chat no longer bumps the counters when attack/use are on keyboard keys.
- Each jar now only loads on the Minecraft version it was built for.
