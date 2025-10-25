package io.github.helmy2.internal

import androidx.compose.ui.text.intl.Locale
import io.github.helmy2.HijriCalendar
import io.github.helmy2.HijriDate

/**
 * Gets a list of 7 narrow weekday names, ordered from Saturday to Friday,
 * based on the provided [locale].
 *
 * e.g., ["S", "S", "M", "T", "W", "T", "F"] or ["س", "ح", "ن", "ث", "ر", "خ", "ج"]
 */
expect fun getNarrowWeekdayNames(locale: Locale): List<String>


/** Simple equality check for HijrahDate day/month/year */
internal fun areSameHijriDate(a: HijriDate, b: HijriDate): Boolean =
    a.year == b.year
            && a.month == b.month
            && a.day == b.day


/**
 * Generates a list of 42 cells (for a 6x7 grid) for a given Hijri month.
 *
 * Includes null-padding for days before the start of the month and after
 * the end of the month to ensure a stable 6-row grid.
 *
 * Assumes the grid starts on Saturday.
 *
 * @param year The Hijri year.
 * @param month The Hijri month (1-12).
 * @return A list of 42 elements, containing [HijriDate]s or nulls.
 */
internal fun generateCalendarCells(year: Int, month: Int): List<HijriDate?> {
    val firstOfMonth = HijriCalendar.of(year, month, 1)
    val dowOfFirst = firstOfMonth.dayOfWeek // ISO 8601: 1=Mon, 6=Sat, 7=Sun

    // Convert ISO day-of-week to a 0-based index where Saturday is 0
    // (1=Mon) -> (1+1)%7 = 2
    // (6=Sat) -> (6+1)%7 = 0
    // (7=Sun) -> (7+1)%7 = 1
    val startIndex = (dowOfFirst + 1) % 7

    val daysInMonth = firstOfMonth.lengthOfMonth()

    val cells = mutableListOf<HijriDate?>()

    // 1. Add null padding for empty days at the start
    for (i in 0 until startIndex) {
        cells.add(null)
    }

    // 2. Add all valid dates for the month
    for (d in 1..daysInMonth) {
        cells.add(HijriCalendar.of(year, month, d))
    }

    // 3. Add null padding to fill the 42-cell (6-row) grid
    while (cells.size < 42) {
        cells.add(null)
    }

    return cells
}