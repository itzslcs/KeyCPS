# KeyCPS

A clean keystrokes and CPS overlay for Minecraft (Fabric / Quilt, client-side).

KeyCPS shows your movement keys, jump, attack and use — lit up as you press them — with a live
clicks-per-second count, so you can watch your click speed while you play instead of guessing.

[![Modrinth downloads](https://img.shields.io/modrinth/dt/keycps?logo=modrinth&label=Modrinth&color=1bd96a)](https://modrinth.com/mod/keycps)
[![Latest version](https://img.shields.io/modrinth/v/keycps?label=latest)](https://modrinth.com/mod/keycps/versions)

## Features

- **Keystrokes HUD** — W, A, S, D, jump, attack and use (plus optional sneak / sprint), with a
  smooth press animation and rounded keys.
- **CPS counter** — clicks per second for attack and use. Every click is counted from input
  events, so even clicks shorter than one frame are caught.
- **Key repeat counting** — bind attack or use to a keyboard key and hold it: KeyCPS counts the
  keyboard repeat rate (aaaaaaa…). Turn on *Rate on Every Key* to see it for W/A/S/D too.
- **Follows your bindings** — keys show what they're actually bound to.
- **Settings screen** — press **Right Shift** in game, or use **Mod Menu**. Size, colours,
  background, opacity, rounded corners, shadow, rainbow text, CPS warning and more.
- **Movable HUD** — starts in the top-left corner; press *Move HUD* and drag it anywhere
  (snaps to edges and centre).
- **14 languages.**

## Download

Get it from **[Modrinth](https://modrinth.com/mod/keycps)**. Works on **Fabric** and **Quilt**; requires Fabric API.
Mod Menu is optional.

| Minecraft | Side |
|---|---|
| 1.21 – 1.21.11, 26.1 – 26.3 | Client only |

## Building from source

Gradle runs on Java 21+; building for Minecraft 26.x needs Java 25+. Every Minecraft version is
built from this one project:

```bash
./gradlew build                 # builds the default version (see gradle.properties)
./gradlew build -Pmc=1.21.5     # builds for a specific version
./gradlew runClient -Pmc=26.1   # launches Minecraft with the mod (and Mod Menu) for testing
```

On Windows use `gradlew.bat` instead of `./gradlew`. The jar ends up in `build/libs/`.

GitHub also builds **all versions automatically** on every push — open the **Actions** tab,
click the latest run, and download the jars from the bottom of the page.

### How the project is laid out

```
src/main/               everything shared: HUD, tracker, settings screen, config, translations
src/platform-*/         key registration, HUD hook and chat for a range of versions
src/input-*/            mouse/keyboard event hooks and the base screen for a range of versions
src/gg-posestack/       drawing adapter for 1.21 – 1.21.5
src/gg-matrix/          drawing adapter for 1.21.6 – 1.21.11
src/mc26.1/             drawing, input hooks and base screen for 26.x (unobfuscated, renamed rendering API)
src/platform-26.*/      key registration, HUD hook, screens and chat for 26.1.x and 26.2+
versions/               one small settings file per Minecraft version
tools/gen_lang.py       generates the translation files
```

Each `versions/<mc>.properties` lists which `src/` folders that version uses. Minecraft renames
a few methods between versions, so only those thin adapters are duplicated; everything else is shared.

**Adding a new Minecraft version:** add `versions/<version>.properties` with the matching Fabric
API and Mod Menu versions, point `variant` at the closest folders, and build. If it doesn't
compile, copy the failing folder to a new one and fix the errors there.

**Translations:** edit the tables in `tools/gen_lang.py` and run `python3 tools/gen_lang.py`.

## Reporting bugs

Open an [issue](../../issues) with your Minecraft version, KeyCPS version, other mods installed,
and a crash report or `latest.log` if there is one.
