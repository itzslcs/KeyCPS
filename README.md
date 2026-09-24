# KeyCPS

A clean keystrokes and CPS overlay for Minecraft (Fabric, client-side).

KeyCPS shows your movement keys (WASD, jump), your attack and use buttons, and a live
clicks-per-second count for each — so you can watch your click speed while you play
instead of guessing at it.

[![Modrinth downloads](https://img.shields.io/modrinth/dt/keycps?logo=modrinth&label=Modrinth&color=1bd96a)](https://modrinth.com/mod/keycps)
[![Latest version](https://img.shields.io/modrinth/v/keycps?label=latest)](https://modrinth.com/mod/keycps/versions)

## Features

- **Keystrokes HUD** — W, A, S, D, jump, attack and use, lit up as you press them.
- **CPS counter** — clicks per second for attack and use.
- **Follows your bindings** — if attack or use is bound to a keyboard key, KeyCPS tracks
  *that* key, not just left/right mouse.
- **Movable HUD** — bind *Move HUD* under *Controls → KeyCPS*, press it, drag the HUD, Esc to save.
- **Configurable** — scale, layout, colours, background, compact mode — in `config/keycps.json`.

## Download

Get it from **[Modrinth](https://modrinth.com/mod/keycps)**. Requires Fabric Loader and Fabric API.

| Minecraft | Side |
|---|---|
| 1.21 – 1.21.11 | Client only |

## Building from source

You need Java 21. Every Minecraft version is built from this one project:

```bash
./gradlew build                 # builds the default version (1.21.11)
./gradlew build -Pmc=1.21.5     # builds for a specific version
./gradlew runClient -Pmc=1.21.5 # launches Minecraft with the mod for testing
```

On Windows use `gradlew.bat` instead of `./gradlew`. The jar ends up in `build/libs/`.

GitHub also builds **all versions automatically** on every push — open the **Actions** tab,
click the latest run, and download the jars from the bottom of the page.

### How the project is laid out

```
src/main/        code shared by every Minecraft version (HUD, config, resources)
src/mc1.21/      code for 1.21 – 1.21.8
src/mc1.21.9/    code for 1.21.9 – 1.21.10
src/mc1.21.11/   code for 1.21.11
versions/        one small settings file per Minecraft version
```

Minecraft changes a few of its methods between versions, so the handful of files that
touch those methods have a copy per group of versions. Everything else is shared.

**Adding a new Minecraft version:** add `versions/<version>.properties` with the matching
Fabric API version (from [fabricmc.net/develop](https://fabricmc.net/develop/)), point
`variant` at the closest `src/` folder, and build. If it doesn't compile, copy that folder
to a new one and fix the errors there.

## Reporting bugs

Open an [issue](../../issues) with your Minecraft version, KeyCPS version, other mods
installed, and a crash report or `latest.log` if there is one.
