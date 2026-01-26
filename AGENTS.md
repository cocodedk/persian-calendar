# Repository Guidelines

## Project Structure & Module Organization
- `app/` is the Android application module.
- Kotlin source lives in `app/src/main/java/com/cocode/calendar/` (UI in `components/` and `screens/`, state in `viewmodel/`, data in `models/`, utilities in `utils/` and `converter/`).
- Compose theme files are under `app/src/main/java/com/cocode/calendar/ui/theme/`.
- Android resources are in `app/src/main/res/` (values, drawables, mipmaps, XML rules).
- Unit tests: `app/src/test/` (local JVM). Instrumentation tests: `app/src/androidTest/` (device/emulator).
- `0-docs/refactoring/` holds refactoring notes and design references.
- Gradle configuration is at the repo root (`build.gradle.kts`, `settings.gradle.kts`, `gradle/libs.versions.toml`).

## Build, Test, and Development Commands
- `./gradlew assembleDebug` builds a debug APK.
- `./gradlew installDebug` installs the debug build on a connected device/emulator.
- `./gradlew test` runs local unit tests.
- `./gradlew connectedAndroidTest` runs instrumentation tests (requires a device/emulator).
- `./gradlew lint` runs Android lint checks.
- `./gradlew clean` removes build outputs.
- `./gradlew bundleRelease` builds the release AAB.
Use Android Studio for project sync, running, and device management when developing locally.
Optional: install pre-commit hooks to run `./gradlew test` and `./gradlew lint` on commit with `pre-commit install`.

## Coding Style & Naming Conventions
- Language: Kotlin with Jetpack Compose and Material 3.
- Indentation: 4 spaces; follow Android Studio’s default Kotlin formatter.
- Naming: classes/objects and `@Composable` functions in `PascalCase`, functions/variables in `camelCase`, constants in `UPPER_SNAKE_CASE`.
- Resources use `snake_case` (e.g., `ic_launcher_background.xml`, `themes.xml`).
- Keep new code within the `com.cocode.calendar` package structure.

## Testing Guidelines
- Frameworks: JUnit4 for unit tests, AndroidX test runner + Espresso for instrumentation.
- Test class naming: `*Test` matching the target class or feature.
- Put pure logic tests in `app/src/test` and UI/device-dependent tests in `app/src/androidTest`.

## Commit & Pull Request Guidelines
- Commit messages follow a Conventional Commits style like `refactor: simplify calendar components`.
- PRs should include a clear description, testing notes (commands run), and linked issues if applicable.
- For UI changes, include screenshots or short recordings.

## Security & Configuration Tips
- Keep local SDK paths in `local.properties`; do not commit it.
- The release keystore lives in `keystore/`; never commit or share passwords.
- Avoid hardcoding secrets; use Gradle properties or environment variables.
