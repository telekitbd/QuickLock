# Quick Lock

A lightweight Android app with a one-tap screen lock and an Android Quick Settings tile.

## IMPORTANT: Upload files to the repository root

When extracting this ZIP, open the extracted folder and upload its **contents** directly into the root of your GitHub repository.
Do not upload the outer `QuickLock` folder as a subfolder.

The repository root must contain these items:
- `.github/workflows/build-apk.yml`
- `settings.gradle.kts`
- `build.gradle.kts`
- `gradle.properties`
- `app/`

## Build the APK with GitHub Actions

1. Create a GitHub repository named `QuickLock`.
2. Extract this ZIP on your computer.
3. Open the extracted folder. Select all its contents, including the hidden `.github` folder, and upload them to the repository root.
4. Commit the files to the `main` branch.
5. Open the repository's **Actions** tab.
6. Select **Build Quick Lock APK** and click **Run workflow** if it has not started automatically.
7. Open the completed run and download the `QuickLock-debug-apk` artifact.
8. Extract the artifact ZIP and install `app-debug.apk` on your Android phone.

## First use

1. Open Quick Lock.
2. Tap **Enable Device Admin** and approve the Android system prompt.
3. For app-free access, swipe down twice to open Android Quick Settings, tap Edit/Pencil, find **Quick Lock**, and drag it into the active tiles.
4. Tap the Quick Lock tile whenever you want to lock the phone.

Android requires Device Admin permission for the screen-lock action. To uninstall the app later, first deactivate Quick Lock under Android Settings > Security > Device admin apps.

The app requests no internet permission and includes no ads or analytics.
