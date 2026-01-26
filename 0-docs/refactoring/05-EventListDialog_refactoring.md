# EventListDialog.kt Refactoring Plan

## File Overview
- **Current Size**: 313 lines
- **Location**: `app/src/main/java/com/cocode/calendar/components/EventListDialog.kt`
- **Purpose**: Contains dialog components for displaying and managing calendar events

## Current Structure
The file contains 2 main composable functions:
1. `EventListDialog()` - Main dialog for displaying list of events
2. `EventItemCard()` - Individual event display card component

## Refactoring Strategy

### Phase 1: Extract Event Display Components
**Create new file**: `components/events/EventDisplayComponents.kt`

**Extract functions**:
- `EventItemCard()` → Move to new file

**Rationale**: Event display logic is reusable and should be separated from dialog management.

### Phase 2: Extract Event List Dialog
**Create new file**: `components/dialogs/EventListDialog.kt`

**Extract functions**:
- `EventListDialog()` → Move to new file

**Rationale**: Dialog management and event list logic should be separated for better organization.

### Phase 3: Create Event Management Utilities
**Create new file**: `components/events/EventUtils.kt`

**Purpose**: Extract event-related utility functions and business logic.

## Directory Structure After Refactoring

```
app/src/main/java/com/cocode/calendar/components/
├── events/
│   ├── EventDisplayComponents.kt
│   └── EventUtils.kt
├── dialogs/
│   └── EventListDialog.kt
└── EventListDialog.kt (minimal, just re-exports)
```

## Benefits of Refactoring

1. **Better Separation of Concerns**: Display logic separated from dialog management
2. **Improved Reusability**: Event components can be used in other contexts
3. **Enhanced Maintainability**: Smaller, focused components are easier to maintain
4. **Better Testability**: Each component can be tested independently
5. **Cleaner Architecture**: Related functionality is properly grouped

## Implementation Steps

### Step 1: Create Directory Structure
```bash
mkdir -p app/src/main/java/com/cocode/calendar/components/{events,dialogs}
```

### Step 2: Extract Event Display Components
- Move `EventItemCard()` to `events/EventDisplayComponents.kt`
- Extract any event display-related utilities

### Step 3: Extract Dialog Components
- Move `EventListDialog()` to `dialogs/EventListDialog.kt`
- Extract dialog state management logic

### Step 4: Create Event Utilities
- Extract event filtering and sorting logic
- Extract event formatting utilities
- Extract accessibility features

### Step 5: Update Dependencies
- Update all imports across the codebase
- Ensure proper dependency injection

## Dependencies to Consider

- `Event` - Core event data model
- `CalendarViewModel` - Used for event state management
- `Strings` - Used for localization
- Material Design components for dialogs and cards

## Testing Strategy

After refactoring, create comprehensive tests:
- `EventDisplayComponentsTest.kt` - Test event card rendering
- `EventListDialogTest.kt` - Test dialog interactions
- `EventUtilsTest.kt` - Test utility functions

## Migration Plan

1. Create new component files with extracted functions
2. Update all import statements across the codebase
3. Run tests to ensure functionality is preserved
4. Remove original functions from `EventListDialog.kt`
5. Update any remaining references

## Success Metrics

- [ ] Each extracted file is under 150 lines
- [ ] Event display functionality works identically
- [ ] Dialog interactions are preserved
- [ ] All existing tests pass
- [ ] No import errors or compilation issues
- [ ] Improved code coverage with new tests

## Additional Improvements

Consider these enhancements during refactoring:
- Add drag-and-drop support for event reordering
- Implement search functionality for large event lists
- Add export/import capabilities for events
- Improve accessibility with better screen reader support
