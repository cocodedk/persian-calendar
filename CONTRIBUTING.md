# Contributing to persian-calendar

## Local Setup

1. Install Android Studio Hedgehog or later
2. Install JDK 17 (temurin distribution recommended)
3. Clone the repository: `git clone https://github.com/cocodedk/persian-calendar.git`
4. Open in Android Studio and sync Gradle

## Install Git Hooks

```
./scripts/install-hooks.sh
```

## Local Git Setup

Run these once after cloning:

```bash
git config pull.rebase true
git config core.autocrlf input
git config push.autoSetupRemote true
git config init.defaultBranch main
```

## Build and Test Commands

```bash
./gradlew assembleDebug --no-daemon           # Build debug APK
./gradlew testDebugUnitTest --no-daemon       # Run unit tests
./gradlew lintDebug --no-daemon               # Lint
./gradlew assembleDebug testDebugUnitTest --no-daemon  # Smoke check
```

## Coding Style

- Kotlin DSL for Gradle, no Groovy
- Jetpack Compose for all UI — no XML layouts
- Max 200 lines per file
- Use string resources — no hardcoded text

## Branch Naming

| Prefix | Use for |
|--------|---------|
| `feature/` | New features (`feat:` commits) |
| `fix/` | Bug fixes (`fix:` commits) |
| `chore/` | Maintenance (`chore:` commits) |
| `docs/` | Documentation (`docs:` commits) |
| `refactor/` | Code cleanup (`refactor:` commits) |
| `ci/` | CI changes (`ci:` commits) |

## PR Checklist

- [ ] Smoke check passes: `./gradlew assembleDebug testDebugUnitTest --no-daemon`
- [ ] Manual test completed on device or emulator
- [ ] Updated docs if behavior changed
- [ ] Commit messages follow Conventional Commits format
