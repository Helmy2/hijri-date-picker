package io.github.helmy2


import androidx.compose.runtime.Stable
import androidx.compose.ui.text.intl.Locale

/**
 * iOS implementation of [HijriDatePickerFormatter].
 */
@Stable
actual class HijriDatePickerFormatter {
    actual fun formatHeadlineDate(date: HijriDate, locale: Locale): String {
        val pattern = if (locale.language == "ar") "d MMMM" else "E, MMM d"
        // We can re-use the global formatting function
        return formatHijriDate(date, pattern, locale)
    }

    actual fun formatMonthYear(date: HijriDate, locale: Locale): String {
        // We can re-use the global formatting function
        return formatHijriDate(date, "MMMM yyyy", locale)
    }
}