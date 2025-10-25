package io.github.helmy2.internal

import androidx.compose.ui.text.intl.Locale
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