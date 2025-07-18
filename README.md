# Calendar App

A modern Android calendar application built with Jetpack Compose that supports both Gregorian and Persian (Jalali) calendar systems. The app provides a clean, intuitive interface for viewing dates and converting between different calendar formats.

## Features

### 🌍 Dual Calendar Support
- **Gregorian Calendar**: Standard international calendar system
- **Persian (Jalali) Calendar**: Traditional Persian calendar system with Persian month names
- Seamless switching between calendar modes
- Real-time conversion between Gregorian and Jalali dates

### 📅 Calendar Functionality
- Monthly calendar view with intuitive navigation
- Current date highlighting
- Previous/next month navigation
- Today's date quick access
- Week day headers in both calendar systems
- Visual distinction between weekdays and weekends

### 🔄 Date Converter
- Built-in date converter tool
- Convert from Jalali to Gregorian dates
- Convert from Gregorian to Jalali dates
- Real-time conversion as you type
- User-friendly input fields with validation

### 🎨 Modern UI/UX
- Material Design 3 components
- Custom color scheme with green theme
- Responsive layout that adapts to different screen sizes
- Smooth animations and transitions
- Intuitive touch interactions

### ⏰ Time Display
- Real-time display of current time in Iran
- Updates automatically

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
│   │   ├── MainActivity.kt          # Main activity and UI components
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
- Contains the primary UI composables
- Implements the calendar grid and navigation
- Handles user interactions and state management

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
- **Target SDK**: API 34 (Android 14)
- **Kotlin**: 1.8+
- **Jetpack Compose**: 1.5.1+

## Installation

1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build and run the application

## Usage

### Viewing Calendar
1. Launch the app to see the current month in Gregorian calendar
2. Use the arrow buttons to navigate between months
3. Tap the "Today" button to return to the current date

### Switching Calendar Systems
1. Tap the calendar mode toggle button
2. The view will switch between Gregorian and Jalali calendars
3. All dates and month names will update accordingly

### Using the Date Converter
1. Tap the converter button to open the date converter
2. Choose conversion direction (Jalali to Gregorian or vice versa)
3. Enter the date in the input fields
4. View the converted date in real-time

## Development

### Building for Release
The app includes release configuration with:
- ProGuard optimization
- Release signing configuration
- Optimized APK/AAB generation

### Testing
- Unit tests for core functionality
- Instrumented tests for UI components
- Test coverage for date conversion logic

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Persian calendar conversion algorithms
- Material Design 3 guidelines
- Jetpack Compose documentation
- Android development community

---

**Note**: This calendar app is designed to be lightweight, fast, and user-friendly while providing comprehensive calendar functionality for both international and Persian users.
