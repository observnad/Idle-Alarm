# Idle Alarm

A RuneLite plugin that alerts you when your player character becomes idle. Unlike standard one-time notifications, Idle Alarm can play repeating audio alerts and flash visual overlays until you resume interacting with the game.

## Features

- **Continuous Sound Alarms**: Repeating sound effects that continue playing until you are no longer idle.
- **Visual Flicker**: Optional whole-screen or UI flashing overlay to catch your attention visually.
- **Activity Detection**: Automatically reset the idle alarm delay on mouse movement, clicks, or keystrokes.
- **Custom Sound Effects**: Choose from built-in sound presets or provide any in-game sound effect ID.

## Configuration

### General Settings

- **Alarm delay**: Number of milliseconds you can remain idle before the alarm starts (default: `600ms` / 1 game tick).
- **Alarm speed**: Interval in milliseconds between repeated alarm sounds (default: `300ms`).
- **Max duration**: Maximum time in milliseconds the alarm will run. Set to `0` to keep alarming indefinitely.

### Activity Detection

- **Mouse movement**: Resets the idle timer whenever the mouse cursor moves across the game window.
- **Clicks & keystrokes**: Resets the idle timer on any mouse click or keyboard input.

### Sound

- **Enable sound**: Toggle the audio alarm on or off.
- **Alarm volume**: Adjust the volume level of the alarm sound.
- **Alarm sound**: Select from a list of preset sounds (Plop, Bloop, UI Boop, Potion, Woodcut, Mining, Low Alch, etc.) or choose *Custom*.
- **Custom sound ID**: Specify any valid sound effect ID to play when *Custom* is selected.

### Visual

- **Flicker effect**:
  - `Off`: Disabled.
  - `Whole screen`: Flashes an overlay across the entire client canvas.
  - `UI`: Flashes only around core UI elements (minimap, chatbox, and inventory/side panel).
- **Flicker color**: Choose the color and opacity (alpha) of the visual flash.
