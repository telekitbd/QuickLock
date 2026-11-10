# Quick Lock 2.0

One-tap screen lock with a Quick Settings tile. **Fingerprint and face unlock keep working.**

## Why v2?
v1 used Device Admin `lockNow()`. Android treats that as a "strict" lock and forces PIN/pattern for the next
unlock, so fingerprint/face were disabled. v2 uses an Accessibility Service with Android's own
`GLOBAL_ACTION_LOCK_SCREEN`, which is identical to pressing the power button.

The service never reads your screen (no events, no window content access). No internet permission, no ads.

## Build the APK with GitHub Actions
Upload the **contents** of this folder (including hidden `.github`) to the root of your repo, then run
**Build Quick Lock APK** in the Actions tab and download `QuickLock-debug-apk`.

## First use
1. Uninstall v1 first (deactivate it under Settings > Security > Device admin apps), then install v2.
2. Open Quick Lock > **Open Accessibility settings** > Quick Lock > turn on.
   - Switch greyed out (Android 13+ sideloaded apps)? Settings > Apps > Quick Lock > ⋮ > **Allow restricted settings**, then try again.
3. Tap **Add to Quick Settings** (Android 13+), or add the tile manually via the pencil/Edit icon.
4. Lock from the big button, the tile, or by long-pressing the app icon > **Lock now**.

Requires Android 9 (API 28) or newer.
