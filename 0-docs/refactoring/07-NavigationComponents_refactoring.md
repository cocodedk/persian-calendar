# NavigationComponents.kt Refactoring Plan

## File Overview
- **Current Size**: 329 lines
- **Location**: `app/src/main/java/com/cocode/calendar/components/NavigationComponents.kt`
- **Purpose**: Contains navigation and picker dialog components for calendar navigation

## Current Structure
The file contains 3 main composable functions:
1. `CalendarNavigation()` - Main navigation bar with month/year display and controls
2. `MonthPickerDialog()` - Dialog for selecting months
3. `YearPickerDialog()` - Dialog for selecting years

## Refactoring Strategy

### Phase 1: Extract Navigation Bar Components
**Create new file**: `components/navigation/CalendarNavigationBar.kt`

**Extract functions**:
- `CalendarNavigation()` → Move to new file

**Rationale**: Main navigation bar is a distinct UI component that can be used independently.

### Phase 2: Extract Picker Dialogs
**Create new file**: `components/pickers/CalendarPickerDialogs.kt`

**Extract functions**:
- `MonthPickerDialog()` → Move to new file
- `YearPickerDialog()` → Move to new file

**Rationale**: Picker dialogs are reusable components that handle specific selection logic.

### Phase 3: Create Picker Utilities
**Create new file**: `components/pickers/PickerUtils.kt`

**Purpose**: Extract common picker logic, validation, and utility functions.

## Directory Structure After Refactoring

```
app/src/main/java/com/cocode/calendar/components/
├── navigation/
│   └── CalendarNavigationBar.kt
├── pickers/
│   ├── CalendarPickerDialogs.kt
│   └── PickerUtils.kt
└── NavigationComponents.kt (minimal, just re-exports)
```

## Benefits of Refactoring

1. **Separation of Concerns**: Navigation and picking logic are separated
2. **Improved Reusability**: Picker dialogs can be used in other contexts
3. **Better Maintainability**: Each component has focused responsibilities
4. **Enhanced Testability**: Smaller components are easier to unit test
5. **Cleaner Code Organization**: Related functionality is grouped logically

## Implementation Steps

### Step 1: Create Directory Structure
```bash
mkdir -p app/src/main/java/com/cocode/calendar/components/{navigation,pickers}
```

### Step 2: Extract Navigation Components
- Move `CalendarNavigation()` to `navigation/CalendarNavigationBar.kt`
- Extract any navigation-specific utilities

### Step 3: Extract Picker Dialogs
- Move `MonthPickerDialog()` and `YearPickerDialog()` to `pickers/CalendarPickerDialogs.kt`
- Identify and extract common picker logic

### Step 4: Create Picker Utilities
- Extract shared logic for month/year validation
- Extract common styling and theming logic
- Extract accessibility features

### Step 5: Update Dependencies
- Update all imports across the codebase
- Ensure all references are updated

## Dependencies to Consider

- `CalendarViewModel` - Used for navigation state
- `Strings` - Used for localization
- `DateTimeUtils` - Used for date calculations
- Material Design components for dialogs

## Testing Strategy

After refactoring, create comprehensive tests:
- `CalendarNavigationBarTest.kt` - Test navigation interactions
- `CalendarPickerDialogsTest.kt` - Test picker functionality
- `PickerUtilsTest.kt` - Test utility functions

## Migration Plan

1. Create new component files with extracted functions
2. Update all import statements across the codebase
3. Run tests to ensure functionality is preserved
4. Remove original functions from `NavigationComponents.kt`
5. Update any remaining references

## Success Metrics

- [ ] Each extracted file is under 150 lines
- [ ] Navigation functionality works identically
- [ ] Picker dialogs maintain all existing behavior
- [ ] All existing tests pass
- [ ] No import errors or compilation issues
- [ ] Improved code coverage with new tests

## Additional Improvements

Consider these enhancements during refactoring:
- Extract picker state management to a separate ViewModel
- Add more comprehensive accessibility support
- Improve keyboard navigation
- Add animation support for smoother transitions
