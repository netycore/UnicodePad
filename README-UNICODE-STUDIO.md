# Unicode Studio

A modern Android Unicode character explorer built on the existing UnicodePad Android foundation.

## Current implementation

- Premium dark Material 3 inspired interface
- Unicode block browser powered by the existing UnicodePad block catalogue
- Character grid with Unicode code point labels
- Character details: Unicode name, code point, decimal value and block
- One-tap clipboard copy
- Favorites and recently viewed characters stored locally
- Search by character name, glyph or U+ hexadecimal code
- Paginated browsing for large Unicode blocks

## Project foundations

This project combines product ideas from two independent open-source projects:

- [Ryosuke839/UnicodePad](https://github.com/Ryosuke839/UnicodePad) — Android character input, Unicode block resources and native Android integration. Original project is Apache-2.0 licensed.
- [unicode-org/gsoc-unicode-app](https://github.com/unicode-org/gsoc-unicode-app) — Unicode Explorer concepts including character metadata, search, favorites, recents and multilingual exploration.

This branch keeps the Android/Kotlin project structure and implements a new Unicode Studio launcher experience. It does not copy the Flutter application's source files.

Please retain upstream notices and review each dependency/data-file license before redistributing a release.

## Build APK

Requirements: JDK 17 and Android SDK (API 36).

```bash
git clone https://github.com/netycore/UnicodePad.git
cd UnicodePad
git checkout feature/unicode-studio-premium
./gradlew assemblePlayDebug
```

APK output:

```
app/build/outputs/apk/play/debug/app-play-debug.apk
```

A GitHub Actions workflow builds this APK and uploads it as an artifact on pushes to this branch and on manual runs.

## Package

`com.netycore.unicodestudio`

## Status

Early implementation. Validate rendering, Unicode name coverage, search performance on large blocks, and device/API compatibility before a production release.
