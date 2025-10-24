package io.github.helmy2

import androidx.compose.ui.text.intl.Locale
import platform.Foundation.NSDateFormatter

/**
 * iOS implementation to get 7 narrow weekday names, ordered from Saturday to Friday.
 */
actual fun getNarrowWeekdayNames(locale: Locale): List<String> {
    val dateFormatter = NSDateFormatter()
    dateFormatter.locale = locale.toNSLocale()

    // Apple's list of narrow weekdays: ["S", "M", "T", "W", "T", "F", "S"]
    // It ALWAYS starts on Sunday.
    val appleNames = dateFormatter.veryShortWeekdaySymbols.map { it.toString() }

    // We need to re-order it to start on Saturday for our grid logic.
    // Apple list: [Sun, Mon, Tue, Wed, Thu, Fri, Sat]
    // Our list: [Sat, Sun, Mon, Tue, Wed, Thu, Fri]

    val sat = appleNames.last() // Get Saturday from the end
    val sunToFri = appleNames.dropLast(1) // Get Sunday-Friday

    return listOf(sat) + sunToFri
}