package io.github.helmy2

import androidx.compose.ui.text.intl.Locale


/**
 * A platform-agnostic representation of a Hijri Date.
 * The 'actual' implementation will wrap the platform's native date class.
 */
expect class HijriDate {
    val year: Int
    val month: Int // 1-12
    val day: Int   // 1-31

    /** ISO-8601 day of week, where 1 is Monday and 7 is Sunday. */
    val dayOfWeek: Int

    fun lengthOfMonth(): Int
}

/**
 * A platform-agnostic factory for creating [HijriDate] instances.
 */
expect object HijriCalendar {
    fun now(): HijriDate
    fun of(year: Int, month: Int, day: Int): HijriDate
}

/**
 * Formats a [HijriDate] into a string based on a pattern and locale.
 */
expect fun formatHijriDate(
    date: HijriDate,
    pattern: String,
    locale: Locale
): String