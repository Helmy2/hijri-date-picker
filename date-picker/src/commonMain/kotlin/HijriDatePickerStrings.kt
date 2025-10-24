package io.github.helmy2

import androidx.compose.runtime.Immutable

/**
 * Represents the localized strings used by the [HijriDatePicker].
 *
 * @property selectDateTitle The text for the date picker's title.
 * @property selectDateHeadlineDefault The placeholder text for the headline when no date is selected.
 */
@Immutable
data class HijriDatePickerStrings(
    val selectDateTitle: String,
    val selectDateHeadlineDefault: String
)