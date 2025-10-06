# CalendarViewModel.kt Refactoring Plan

## File Overview
- **Current Size**: 228 lines
- **Location**: `app/src/main/java/com/cocode/calendar/CalendarViewModel.kt`
- **Purpose**: Main ViewModel for calendar state management and business logic

## Current Structure
The ViewModel contains multiple responsibility areas:
1. **Calendar Navigation**: Date/month/year management functions
2. **Event Management**: CRUD operations for events
3. **Dialog Management**: Showing/hiding various dialogs
4. **State Management**: LiveData and StateFlow management

## Refactoring Strategy

### Phase 1: Extract Calendar Navigation Logic
**Create new file**: `viewmodel/CalendarNavigationViewModel.kt`

**Extract functions**:
- `updateGregorianDate()`
- `toggleIsJalaliCalendar()`
- `toggleConverter()`
- `toggleJalaliToGregorianConverter()`
- `changeMonth()`
- `changeYear()`

**Rationale**: Calendar navigation is a distinct responsibility that can be separated.

### Phase 2: Extract Event Management Logic
**Create new file**: `viewmodel/EventManagementViewModel.kt`

**Extract functions**:
- `addEvent()`
- `updateEvent()`
- `deleteEvent()` (extract from confirmDeleteEvent)
- `getEventsForDateOnly()`

**Rationale**: Event CRUD operations should be isolated for better testability.

### Phase 3: Extract Dialog Management Logic
**Create new file**: `viewmodel/DialogManagementViewModel.kt`

**Extract functions**:
- All dialog show/hide functions
- Dialog state management

**Rationale**: Dialog state management is a separate concern from business logic.

### Phase 4: Create Base Calendar ViewModel
**Update existing file**: `CalendarViewModel.kt`

**Purpose**: Act as a coordinator ViewModel that delegates to specialized ViewModels.

## Directory Structure After Refactoring

```
app/src/main/java/com/cocode/calendar/
├── viewmodel/
│   ├── CalendarNavigationViewModel.kt
│   ├── EventManagementViewModel.kt
│   ├── DialogManagementViewModel.kt
│   └── CalendarViewModel.kt (coordinator)
└── repository/
    └── CalendarRepository.kt (if not exists)
```

## Benefits of Refactoring

1. **Single Responsibility**: Each ViewModel has a focused purpose
2. **Improved Testability**: Smaller ViewModels are easier to unit test
3. **Better Maintainability**: Changes to one area don't affect others
4. **Enhanced Reusability**: Specialized ViewModels can be used independently
5. **Cleaner Architecture**: Follows MVVM principles more closely

## Implementation Steps

### Step 1: Create Directory Structure
```bash
mkdir -p app/src/main/java/com/cocode/calendar/viewmodel
```

### Step 2: Extract Calendar Navigation
- Create `CalendarNavigationViewModel.kt`
- Move calendar navigation functions and related state
- Update dependencies and imports

### Step 3: Extract Event Management
- Create `EventManagementViewModel.kt`
- Move event CRUD operations and related state
- Extract event repository if needed

### Step 4: Extract Dialog Management
- Create `DialogManagementViewModel.kt`
- Move all dialog state management
- Create dialog state models

### Step 5: Update Main ViewModel
- Refactor `CalendarViewModel.kt` to coordinate between specialized ViewModels
- Maintain public API for existing usage

## Dependencies to Consider

- `Event` - Event data model
- `LocalDate` - Date handling
- `YearMonth` - Month/year handling
- Room database for persistence
- LiveData/StateFlow for reactive state

## Testing Strategy

After refactoring, create comprehensive tests:
- `CalendarNavigationViewModelTest.kt` - Test navigation logic
- `EventManagementViewModelTest.kt` - Test event operations
- `DialogManagementViewModelTest.kt` - Test dialog state
- `CalendarViewModelTest.kt` - Test coordination logic

## Migration Plan

1. Create specialized ViewModel classes
2. Extract functions and state to appropriate ViewModels
3. Update the main CalendarViewModel to use composition
4. Update all references across the codebase
5. Run tests to ensure functionality is preserved

## Success Metrics

- [ ] Each extracted ViewModel is under 150 lines
- [ ] All existing functionality preserved
- [ ] Calendar navigation works identically
- [ ] Event management functions correctly
- [ ] Dialog state management is maintained
- [ ] All existing tests pass
- [ ] No import errors or compilation issues

## Additional Improvements

Consider these enhancements during refactoring:
- Add repository pattern for data access
- Implement proper error handling in each ViewModel
- Add comprehensive logging for debugging
- Implement ViewModel state persistence across configuration changes
