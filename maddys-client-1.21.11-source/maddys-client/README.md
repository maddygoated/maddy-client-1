# Maddy's Client (Fabric 1.21.11)

A PvP / HUD client mod for **Minecraft 1.21.11** (Fabric).
Open the mod menu in game with **Right Shift**.

## Build the .jar (Windows)

1. Install **Java 21 JDK** (e.g. Eclipse Temurin 21).
2. Open this folder in a terminal and run:

   ```
   gradlew.bat build
   ```

   (macOS / Linux: `./gradlew build`)
3. The mod is created at `build/libs/maddys-client-1.0.0.jar`.

The first build downloads Minecraft and Fabric, so it needs internet and takes a few minutes.

## Install

1. Install **Fabric Loader** for 1.21.11 (fabricmc.net/use).
2. Put `maddys-client-1.0.0.jar` **and** **Fabric API** for 1.21.11 into `.minecraft/mods`.
3. Start the "fabric-loader-1.21.11" profile.

## Controls

| Key | Action |
|---|---|
| Right Shift | Mod menu |
| C (hold) | Zoom |
| (unbound) | HUD editor, Fullbright toggle, Stopwatch, AutoText |

All keys can be changed in Options → Controls → "Maddy's Client".

## Mod menu

- Left-click a card to turn a mod on / off.
- Right-click a card (or the ••• dots) to open its settings.
- "Edit HUD Layout" opens the drag-and-drop editor (drag, scroll to resize, right-click to hide, snapping guides).
- Everything saves to `config/maddys-client.json`.

## Mods (32)

**HUD (23):** Watermark, FPS, CPS, Ping, Coordinates (+facing, nether coords), Direction compass,
Speed, Keystrokes (WASD, mouse, CPS, space), Armor Status, Potion Effects, Target HUD,
Combo Display, Reach Display, Item Counter, Clock, Playtime, Stopwatch, Server Address,
Pack Display, Memory, Saturation, Day Counter, Mod List.

Every HUD mod has: scale, background on/off + colour, rounded corners, accent border,
text colour, accent colour, RGB (chroma) mode, text shadow.

**Render:** Fullbright, Custom Crosshair (6 shapes, size, gap, thickness, colour, outline,
red-on-target), Hit Indicator, Hide Scoreboard, Hide Boss Bar.

**Player:** Toggle Sprint, Toggle Sneak, AutoText.

**Utility:** Zoom (factor + smooth camera).

Also: branded window title, "Maddy's Client" button and logo on the title screen.
