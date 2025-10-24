package io.github.helmy2

import androidx.compose.ui.text.intl.Locale
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField

/**
 * The actual Android implementation of [HijriDate].
 * It wraps the Java [HijrahDate] class.
 */
actual class HijriDate(internal val javaDate: HijrahDate) {
    actual val year: Int
        get() = javaDate.get(ChronoField.YEAR)
    actual val month: Int
        get() = javaDate.get(ChronoField.MONTH_OF_YEAR)
    actual val day: Int
        get() = javaDate.get(ChronoField.DAY_OF_MONTH)

    // Java's DAY_OF_WEEK is already ISO-8601 (1=Mon, 7=Sun), so no conversion needed.
    actual val dayOfWeek: Int
        get() = javaDate.get(ChronoField.DAY_OF_WEEK)

    actual fun lengthOfMonth(): Int = javaDate.lengthOfMonth()
}

/**
 * The actual Android implementation of the calendar factory.
 */
actual object KmpHijriCalendar {
    actual fun now(): HijriDate = HijriDate(HijrahDate.now())
    actual fun of(year: Int, month: Int, day: Int): HijriDate =
        HijriDate(HijrahDate.of(year, month, day))
}

/**
 * The actual Android implementation for formatting a date.
 */
actual fun formatHijriDate(
    date: HijriDate,
    pattern: String,
    locale: Locale
): String {
    // We convert the Compose Locale to the Java Locale
    val javaLocale = locale.platformLocale
    return date.javaDate.format(DateTimeFormatter.ofPattern(pattern, javaLocale))
}

/**
 * The actual Android implementation for formatting a number.
 */
actual fun formatNumber(number: Int, locale: Locale): String {
    val javaLocale = locale.platformLocale
    if (javaLocale.language != "ar") return number.toString()

    val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return number.toString().map { ch ->
        if (ch in '0'..'9') arabicDigits[ch - '0'] else ch
    }.joinToString("")
}