# Jalali Persian Calendar

Jalali Persian Calendar is an Android app that displays Gregorian and Persian (Jalali) dates, stores calendar events on your phone, and converts dates between the two calendars. It is free software, works without an account or a network connection, and is built with Jetpack Compose.

## Download

<!-- cocode-apps:install:start -->
- Coming to F-Droid
- [Download the Android installation file (APK) from GitHub](https://github.com/cocodedk/persian-calendar/releases/latest/download/persian-calendar.apk)
- [Add the app to Obtainium, an app that keeps it up to date](https://apps.obtainium.imranr.dev/redirect?r=obtainium://add/https://github.com/cocodedk/persian-calendar)
<!-- cocode-apps:install:end -->

## Features

### 🌍 Two calendars
- **Gregorian calendar**: the calendar used around the world
- **Persian (Jalali) calendar**: Jalali day numbers on the grid, and the Jalali year and month names (in Persian script) in the header
- Tap "Show Jalali dates" or "Show Gregorian dates" to switch
- The month grid always covers a Gregorian month; Jalali mode changes the day numbers

### 📅 Calendar
- Swipe left or right to change months
- Today's date is highlighted; the "Today" button returns to it
- "Select Month" and "Select Year" list the Jalali or the Gregorian months and years, matching the calendar on screen
- Week day headers, Sunday first, with weekend days in a different colour

### 📝 Events
- Tap a day to add an event or see that day's events
- An event can repeat every year on the same Gregorian date, with an optional last year
- Events are stored in a database on your phone

### 🔄 Date Converter
- Convert from Jalali to Gregorian dates, or the other way round
- The result updates as you type
- Numeric date fields that reject a date that does not exist, Gregorian or Jalali (for example 30 February, or day 30 of Esfand in a year that is not a leap year)
- Shows the time until or since the date

### 🎨 Look
- Material Design 3 components with a green colour scheme
- A six-row calendar grid with month and year selectors

### ⏰ Time Display
- Shows the current time in Iran, updated every second

### ℹ️ About screen
- Opens from the "About this app" button at the bottom of the calendar screen
- Shows the version, a privacy summary, links to the website, the source code and the issue tracker, and the credits and licenses

## Privacy

The app does not collect, transmit or share any personal data. It requests no internet permission and no runtime permissions, and it uses no analytics, crash reporting or advertising. The events you add are stored in a private database on your own device and are not sent anywhere. Deleting the app or clearing its data removes that copy. If you have enabled Android Auto Backup or Google account backup, the operating system may include this data in your own personal Google backup, which can be restored later. That is controlled entirely by you and Google, and we have no access to it. The About screen has buttons that open web pages (the website, the source code, the privacy policy); the app hands the address to your browser only when you tap one, and makes no network connection itself.

Read the full [privacy policy](https://calendar.cocode.dk/privacy/).

## Technical Architecture

### Built With
- **Jetpack Compose**: Modern declarative UI toolkit
- **Kotlin**: Primary programming language
- **Material Design 3**: Latest Material Design components
- **MVVM Architecture**: Model-View-ViewModel pattern
- **LiveData**: Reactive data streams
- **Coroutines**: Asynchronous programming

### Project Structure
```
app/src/main/
├── java/
│   ├── com/cocode/calendar/
│   │   ├── MainActivity.kt          # Applies the theme and opens CalendarApp
│   │   ├── CalColors.kt            # Color scheme definitions
│   │   └── ui/theme/               # Theme and styling
│   ├── CalendarConverter.kt        # Date conversion utilities
│   └── utils/                      # Utility functions
├── res/                           # Resources (drawables, strings, etc.)
└── AndroidManifest.xml           # App configuration
```

### Key Components

#### MainActivity.kt
- Main entry point of the application
- Applies the theme and opens `CalendarApp`
- Screen components in `screens/` and `components/` implement the interface (calendar grid, navigation, dialogs, About screen)
- `CalendarViewModel` coordinates state

#### CalendarConverter.kt
- Core date conversion logic
- Gregorian to Jalali conversion
- Jalali to Gregorian conversion
- Persian month name handling
- Week number calculations

#### CalendarViewModel
- Manages application state
- Handles calendar mode switching
- Controls date navigation
- Manages converter visibility

### Color Scheme
The app uses a custom green-based color scheme:
- **Background**: Dark green (#025842)
- **Primary**: Green (#019A64)
- **Accent**: Blue (#43C7F9)
- **Text**: White (#FFFFFF)
- **Weekend**: Red (#F05066)

## Requirements

- **Minimum SDK**: API 26 (Android 8.0)
- **Target SDK**: API 36 (Android 16)
- **Kotlin**: 1.9.0
- **Jetpack Compose**: 1.9.0 (Compose BOM 2025.08.00)

## Usage

### Viewing Calendar
1. Launch the app to see the current month with Gregorian day numbers
2. Swipe left for the next month or right for the previous month, or tap "Select Month" to choose a month
3. Tap the "Today" button to return to the current date

### Switching Calendar Systems
1. Tap "Show Jalali dates" (or "Show Gregorian dates")
2. The day numbers and the header change to the other calendar
3. "Select Month" and "Select Year" now list the months and years of that calendar

### Using the Date Converter
1. Tap "Converter" to open the date converter
2. Tap the ⇄ button to choose the direction (Jalali to Gregorian or the other way)
3. Enter the date in the Year, Month and Day fields
4. The converted date appears as you type

### Adding an Event
1. Tap a day
2. Fill in the title, and optionally a description and a yearly repeat
3. Tap "Create"

## Build

### For Developers

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the application


### Installing the Release Build

To install the release version of the app on an Android device from the generated `app-release.aab` file, you will need to use `bundletool`.

**Prerequisites**

1.  **Android SDK:** Make sure you have the Android SDK installed and the `adb` command-line tool is in your system's PATH.
2.  **bundletool:** Download `bundletool` from the official [Android Developer website](https://developer.android.com/studio/command-line/bundletool).

**Steps**

1.  **Generate a universal APK from the AAB:**

    Use `bundletool` to generate a set of APKs from the `.aab` file. You will need the release keystore to sign the APKs. Create it with:

    ```sh
    ./keystore/create-release-keystore.sh
    ```

    The script defaults to `keystore/1-release-key.jks` and will prompt for passwords.

    ```sh
    java -jar /path/to/bundletool.jar build-apks \
      --bundle=release/app-release.aab \
      --output=release/app.apks \
      --mode=universal \
      --ks=keystore/1-release-key.jks \
      --ks-pass=pass:your_keystore_password \
      --ks-key-alias=your_key_alias \
      --key-pass=pass:your_key_password
    ```

    **Note:** Replace `/path/to/bundletool.jar` with the actual path to your `bundletool.jar` file. You will also need to provide the correct passwords for the keystore.

2.  **Install the APKs on your device:**

    With a device connected via `adb`, use `bundletool` to install the generated `.apks` file.

    ```sh
    java -jar /path/to/bundletool.jar install-apks \
      --apks=release/app.apks
    ```

    The application will then be installed on your device.

### Building for Release
The app includes release configuration with:
- ProGuard optimization
- Release signing configuration
- Optimized APK/AAB generation

### Testing
- Unit tests for core functionality, including date conversion logic
- An instrumented test checks the application package name; UI components are not covered by instrumentation tests

### Release APK (CI)

Run the "Release APK" GitHub Actions workflow manually to build and publish a signed release APK.
It needs these four repository secrets:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Generate the base64 value from your keystore:

```sh
base64 -w 0 keystore/1-release-key.jks
```

The release workflow fails if any of the four signing secrets is missing.

## Contributing

Local setup, git hooks and the build and test commands are in [CONTRIBUTING.md](CONTRIBUTING.md).

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

### Pre-commit Hooks (Optional)

This repo includes pre-commit hooks to run `./gradlew test` and `./gradlew lint` before committing.

```sh
pre-commit install
```

To skip a hook once, use `SKIP=gradle-test,gradle-lint git commit`.


## Documentation

The GitHub Pages site is deployed from the `docs/` directory on the `main` branch:
https://calendar.cocode.dk/

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Persian calendar conversion algorithms
- Material Design 3 guidelines
- Jetpack Compose documentation
- Android development community

---

**Note**: The app uses your device's clock and reads the events you save in its local database. It makes no network connections of its own, and Android may include its data in your device backup.
