# CLAUDE.md — persian-calendar

## Project Overview

Modern Android calendar application built with Jetpack Compose that supports both Gregorian and Persian (Jalali) calendar systems. Provides a clean interface for viewing dates and converting between calendar formats, with Material Design 3 and bilingual (English/Persian) support.

- **Language / Runtime**: Kotlin 2.x, Java 17
- **Framework**: Jetpack Compose, Android SDK
- **Architecture**: Clean Architecture + MVVM
- **Package / Namespace**: `com.cocode.calendar`

---

## Required Skills — ALWAYS Invoke These

These skills **must** be invoked when the relevant situation arises. Never skip them.

| Situation | Skill |
|-----------|-------|
| Before any new feature or screen | `superpowers:brainstorming` |
| Planning multi-step changes | `superpowers:writing-plans` |
| Writing or fixing core logic | `superpowers:test-driven-development` |
| First sign of a bug or failure | `superpowers:systematic-debugging` |
| Before completing a feature branch | `superpowers:requesting-code-review` |
| Before claiming any task done | `superpowers:verification-before-completion` |
| Working on UI / frontend | `frontend-design:frontend-design` |
| After implementing — reviewing quality | `simplify` |

---

## Architecture

```
Calendar/
├── app/
│   ├── src/main/java/    ← Application source (Compose UI, ViewModels, Domain)
│   ├── src/test/         ← Unit tests
│   └── src/androidTest/  ← Instrumentation tests
├── gradle/               ← Version catalog (libs.versions.toml)
├── build.gradle.kts      ← Root build config
├── settings.gradle.kts   ← Module/repo settings
└── version.txt           ← Semantic version (MAJOR.MINOR.PATCH)
```

### Layer Rules
- UI layer (Compose screens) must not contain business logic
- ViewModels mediate between UI and domain/data layers
- Domain layer must be pure Kotlin — no Android imports
- No hardcoded strings — use Android string resources

---

## Coding Conventions

- [ ] All models are **immutable** — use `data class` with `copy()` for mutations
- [ ] Functions are **pure** where possible — no hidden side effects
- [ ] State managed through `ViewModel` + `StateFlow`/`LiveData`
- [ ] No hardcoded strings — use `strings.xml` resources
- [ ] Strict typing everywhere — Kotlin DSL
- [ ] **Max 200 lines per file** — extract composables or classes when approaching the limit

---

## Engineering Principles

### File Size
- **200-line maximum per file** — extract a composable, class, or module when approaching the limit

### DRY · SOLID · KISS · YAGNI
- Extract shared logic into named utilities; never copy-paste
- Single Responsibility: one composable/class does one thing
- Don't add features not yet needed
- Delete dead code immediately

### TDD
- Write the failing test first, make it pass, then refactor
- Test names describe behaviour: `"should convert Gregorian to Jalali date"`
- One assertion per test — keep tests focused and readable

### Commit hygiene
- Follow Conventional Commits: `feat: ...` / `fix: ...` / `chore: ...`
- The `commit-msg` hook enforces this automatically

---

## Build Commands

```bash
./gradlew assembleDebug --no-daemon          # Build debug APK
./gradlew testDebugUnitTest --no-daemon      # Run unit tests
./gradlew lintDebug --no-daemon              # Lint
./gradlew buildSmoke --no-daemon             # Full smoke check (build + test + lint)
```

---

## Key Files

| File | Purpose |
|------|---------|
| `CLAUDE.md` | This file — project conventions and session startup |
| `version.txt` | Semantic version (MAJOR.MINOR.PATCH) |
| `.github/workflows/ci.yml` | CI on PRs and non-main branches |
| `.github/workflows/release-apk.yml` | Signed release APK build and GitHub Release |
| `.github/workflows/pages.yml` | GitHub Pages deployment |
| `.githooks/pre-commit` | Smoke check on commit |
| `.githooks/commit-msg` | Conventional Commits enforcement |
| `scripts/install-hooks.sh` | One-time hook installer |

---

## Starting a New Session

1. Read this file
2. Run `./gradlew buildSmoke --no-daemon` to confirm everything passes
3. Invoke `superpowers:brainstorming` before touching any feature
4. Follow the Required Skills table — every skill is mandatory, not optional
