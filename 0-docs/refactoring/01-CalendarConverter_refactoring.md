# CalendarConverter.kt Refactoring Plan

## File Overview
- **Current Size**: 181 lines
- **Location**: `app/src/main/java/CalendarConverter.kt`
- **Purpose**: Utility class for converting between Gregorian and Jalali (Persian) calendar systems

## Current Structure
The class contains 3 main conversion functions:
1. `getJalaliWeekNumber()` - Calculates week number for Jalali dates
2. `gregorianToJalali()` - Converts Gregorian dates to Jalali dates
3. `jalaliToGregorian()` - Converts Jalali dates to Gregorian dates

## Refactoring Strategy

### Phase 1: Extract Week Calculation Logic
**Create new file**: `utils/JalaliWeekCalculator.kt`

**Extract functions**:
- `getJalaliWeekNumber()` → Move to new file

**Rationale**: Week calculation is a specific utility that can be used independently.

### Phase 2: Extract Gregorian to Jalali Conversion
**Create new file**: `converter/GregorianToJalaliConverter.kt`

**Extract functions**:
- `gregorianToJalali()` → Move to new file

**Rationale**: Gregorian to Jalali conversion is a distinct conversion direction.

### Phase 3: Extract Jalali to Gregorian Conversion
**Create new file**: `converter/JalaliToGregorianConverter.kt`

**Extract functions**:
- `jalaliToGregorian()` → Move to new file

**Rationale**: Jalali to Gregorian conversion is a separate conversion direction.

### Phase 4: Create Common Data and Models
**Create new file**: `models/CalendarModels.kt`

**Purpose**: Extract shared data classes and constants.

## Directory Structure After Refactoring

```
app/src/main/java/com/cocode/calendar/
├── utils/
│   └── JalaliWeekCalculator.kt
├── converter/
│   ├── GregorianToJalaliConverter.kt
│   └── JalaliToGregorianConverter.kt
├── models/
│   └── CalendarModels.kt
└── CalendarConverter.kt (minimal, just re-exports)
```

## Benefits of Refactoring

1. **Single Responsibility**: Each class handles one specific conversion direction
2. **Improved Testability**: Smaller classes are easier to unit test
3. **Better Maintainability**: Changes to one conversion don't affect others
4. **Enhanced Reusability**: Converters can be used independently
5. **Cleaner Architecture**: Proper separation between different conversion types

## Implementation Steps

### Step 1: Create Directory Structure
```bash
mkdir -p app/src/main/java/com/cocode/calendar/{utils,converter,models}
```

### Step 2: Extract Calendar Models
- Move `JalaliDate` and `JalaliMonth` data classes to `models/CalendarModels.kt`
- Extract shared constants and arrays

### Step 3: Extract Week Calculator
- Move `getJalaliWeekNumber()` to `utils/JalaliWeekCalculator.kt`
- Extract week calculation algorithms

### Step 4: Extract Gregorian to Jalali Converter
- Move `gregorianToJalali()` to `converter/GregorianToJalaliConverter.kt`
- Extract Gregorian to Jalali conversion algorithms

### Step 5: Extract Jalali to Gregorian Converter
- Move `jalaliToGregorian()` to `converter/JalaliToGregorianConverter.kt`
- Extract Jalali to Gregorian conversion algorithms

### Step 6: Update Main Converter
- Refactor `CalendarConverter.kt` to delegate to specialized converters
- Maintain backward compatibility

## Dependencies to Consider

- `LocalDate` - Standard Java date type
- `Strings` - For localization support

## Testing Strategy

After refactoring, create comprehensive tests:
- `JalaliWeekCalculatorTest.kt` - Test week number calculations
- `GregorianToJalaliConverterTest.kt` - Test Gregorian to Jalali conversion
- `JalaliToGregorianConverterTest.kt` - Test Jalali to Gregorian conversion
- `CalendarModelsTest.kt` - Test data models

## Migration Plan

1. Create new converter and utility files
2. Extract functions and data to appropriate files
3. Update imports across the codebase
4. Run tests to ensure conversion accuracy is preserved
5. Remove original functions from `CalendarConverter.kt`

## Success Metrics

- [ ] Each extracted file is under 150 lines
- [ ] All conversion functions work identically
- [ ] Week calculations are accurate
- [ ] All existing tests pass
- [ ] No import errors or compilation issues
- [ ] Improved code coverage with new tests

## Additional Improvements

Consider these enhancements during refactoring:
- Add comprehensive input validation
- Implement caching for expensive calculations
- Add support for different Jalali calendar variations
- Improve error handling for edge cases
