package io.github.helmy2

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

/**
 * Represents the colors used by a [HijriDatePicker].
 */
@Immutable
class HijriDatePickerColors internal constructor(
    val titleContentColor: Color,
    val headlineContentColor: Color,
    val selectedDayContainerColor: Color,
    val onSelectedDayContainerColor: Color,
    val todayDateBorderColor: Color,
    val dayContentColor: Color,
    val todayDateContentColor: Color,
    val selectedYearContainerColor: Color,
    val onSelectedYearContainerColor: Color,
    val yearContentColor: Color
)

/**
 * Creates and remembers the default [HijriDatePickerColors].
 */
@Composable
internal fun defaultDatePickerColors(): HijriDatePickerColors {
    val colors = MaterialTheme.colorScheme
    return remember(colors) {
        HijriDatePickerColors(
            titleContentColor = colors.onSurfaceVariant,
            headlineContentColor = colors.onSurfaceVariant,
            selectedDayContainerColor = colors.primary,
            onSelectedDayContainerColor = colors.onPrimary,
            todayDateBorderColor = colors.primary,
            dayContentColor = colors.onSurface,
            todayDateContentColor = colors.primary,
            selectedYearContainerColor = colors.primary,
            onSelectedYearContainerColor = colors.onPrimary,
            yearContentColor = colors.onSurfaceVariant
        )
    }
}