package io.github.helmy2

import androidx.compose.ui.text.intl.Locale
import java.time.DayOfWeek
import java.time.format.TextStyle

/**
 * Android implementation to get 7 narrow weekday names, ordered from Saturday to Friday.
 */
actual fun getNarrowWeekdayNames(locale: Locale): List<String> {
    val javaLocale = locale.platformLocale
    val textStyle = TextStyle.NARROW

    // Create the list in the required order: Sat, Sun, Mon, Tue, Wed, Thu, Fri
    return listOf(
        DayOfWeek.SATURDAY.getDisplayName(textStyle, javaLocale),
        DayOfWeek.SUNDAY.getDisplayName(textStyle, javaLocale),
        DayOfWeek.MONDAY.getDisplayName(textStyle, javaLocale),
        DayOfWeek.TUESDAY.getDisplayName(textStyle, javaLocale),
        DayOfWeek.WEDNESDAY.getDisplayName(textStyle, javaLocale),
        DayOfWeek.THURSDAY.getDisplayName(textStyle, javaLocale),
        DayOfWeek.FRIDAY.getDisplayName(textStyle, javaLocale)
    )
}