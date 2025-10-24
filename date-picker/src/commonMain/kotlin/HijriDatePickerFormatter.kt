package io.github.helmy2

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.intl.Locale

/**
 * An interface for formatting Hijri dates.
 */
@Stable
expect class HijriDatePickerFormatter {

    /**
     * Formats a [HijriDate] for the date picker headline
     * (e.g., "Jum. I 12" or "١٢ جمادى الأولى").
     */
    fun formatHeadlineDate(date: HijriDate, locale: Locale): String

    /**
     * Formats a [HijriDate] for the month/year title
     * (e.g., "Ramadan 1446" or "رمضان ١٤٤٦").
     */
    fun formatMonthYear(date: HijriDate, locale: Locale): String
}