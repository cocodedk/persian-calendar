package com.cocode.calendar.components.forms

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.cocode.calendar.CalColors
import java.time.LocalDate

/**
 * Result of form validation.
 */
data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String>
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
    val errors = mutableListOf<String>()

    if (formData.title.isBlank()) {
        errors.add("Event title is required")
    }

    if (formData.title.length > 100) {
        errors.add("Event title must be less than 100 characters")
    }

    if (formData.description != null && formData.description.length > 500) {
        errors.add("Description must be less than 500 characters")
    }

    if (formData.isRepeating && formData.repetitionEndYear.isNotBlank()) {
        try {
            val year = formData.repetitionEndYear.toInt()
            if (year < LocalDate.now().year) {
                errors.add("End year must be in the future")
            }
        } catch (e: NumberFormatException) {
            errors.add("End year must be a valid number")
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
            ValidationResult(isValid = false, errors = listOf("End year must be in the future"))
        } else {
            ValidationResult(isValid = true, errors = emptyList())
        }
    } catch (e: NumberFormatException) {
        ValidationResult(isValid = false, errors = listOf("End year must be a valid number"))
    }
}

/**
 * Validates event title.
 */
fun validateEventTitle(title: String): ValidationResult {
    val errors = mutableListOf<String>()

    if (title.isBlank()) {
        errors.add("Event title is required")
    }

    if (title.length > 100) {
        errors.add("Event title must be less than 100 characters")
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

    val errors = mutableListOf<String>()

    if (description.length > 500) {
        errors.add("Description must be less than 500 characters")
    }

    return ValidationResult(isValid = errors.isEmpty(), errors = errors)
}
