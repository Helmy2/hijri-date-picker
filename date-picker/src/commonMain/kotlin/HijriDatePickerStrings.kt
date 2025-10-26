package io.github.helmy2

import androidx.compose.runtime.Immutable

/**
 * Represents the localized strings used by the [HijriDatePicker].
 *
 * @property selectDateTitle The text for the date picker's title.
 * @property selectDateHeadlineDefault The placeholder text for the headline when no date is selected.
 * @property nextMonthContentDescription The content description for the "next month" button.
 * @property previousMonthContentDescription The content description for the "previous month" button.
 */
@Immutable
data class HijriDatePickerStrings(
    val selectDateTitle: String,
    val selectDateHeadlineDefault: String,
    val nextMonthContentDescription: String,
    val previousMonthContentDescription: String,
    val changeYearContentDescription: String
)