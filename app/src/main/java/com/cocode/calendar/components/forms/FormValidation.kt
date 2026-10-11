package com.cocode.calendar.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cocode.calendar.CalColors
import com.cocode.calendar.R
import java.time.LocalDate

/**
 * Result of form validation. Each error is the id of a string resource, so the
 * screen shows it in the language of the app and the checks stay free of Android context.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<Int>
)

/**
 * Utility functions for form validation and error handling.
 * Contains reusable validation logic for event forms.
 */

/**
 * Validates event form data and returns validation result.
 * This function can be used independently of UI components.
 */
fun validateEventForm(formData: EventFormData): ValidationResult {
    val errors = mutableListOf<Int>()

    if (formData.title.isBlank()) {
        errors.add(R.string.event_error_title_required)
    }

    if (formData.title.length > 100) {
        errors.add(R.string.event_error_title_too_long)
    }

    if (formData.description != null && formData.description.length > 500) {
        errors.add(R.string.event_error_description_too_long)
    }

    if (formData.isRepeating && formData.repetitionEndYear.isNotBlank()) {
        try {
            val year = formData.repetitionEndYear.toInt()
            if (year < LocalDate.now().year) {
                errors.add(R.string.event_error_end_year_past)
            }
        } catch (e: NumberFormatException) {
            errors.add(R.string.event_error_end_year_invalid)
        }
    }

    return ValidationResult(isValid = errors.isEmpty(), errors = errors)
}

/**
 * Validates a year input for repetition end date.
 */
fun validateRepetitionEndYear(yearString: String): ValidationResult {
    if (yearString.isBlank()) {
        return ValidationResult(isValid = true, errors = emptyList()) // Optional field
    }

    return try {
        val year = yearString.toInt()
        if (year < LocalDate.now().year) {
            ValidationResult(isValid = false, errors = listOf(R.string.event_error_end_year_past))
        } else {
            ValidationResult(isValid = true, errors = emptyList())
        }
    } catch (e: NumberFormatException) {
        ValidationResult(isValid = false, errors = listOf(R.string.event_error_end_year_invalid))
    }
}

/**
 * Validates event title.
 */
fun validateEventTitle(title: String): ValidationResult {
    val errors = mutableListOf<Int>()

    if (title.isBlank()) {
        errors.add(R.string.event_error_title_required)
    }

    if (title.length > 100) {
        errors.add(R.string.event_error_title_too_long)
    }

    return ValidationResult(isValid = errors.isEmpty(), errors = errors)
}

/**
 * Validates event description.
 */
fun validateEventDescription(description: String?): ValidationResult {
    if (description == null) {
        return ValidationResult(isValid = true, errors = emptyList()) // Optional field
    }

    val errors = mutableListOf<Int>()

    if (description.length > 500) {
        errors.add(R.string.event_error_description_too_long)
    }

    return ValidationResult(isValid = errors.isEmpty(), errors = errors)
}
