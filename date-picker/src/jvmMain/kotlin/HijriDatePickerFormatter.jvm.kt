package io.github.helmy2

import androidx.compose.runtime.Stable
import androidx.compose.ui.text.intl.Locale
import io.github.helmy2.internal.convertToArabicIndicDigits
import java.time.format.DateTimeFormatter

@Stable
actual class HijriDatePickerFormatter {
    actual fun formatHeadlineDate(date: HijriDate, locale: Locale): String {
        val pattern = if (locale.language == "ar") "d MMMM" else "E, MMM d"
        return date.javaDate.format(DateTimeFormatter.ofPattern(pattern, locale.platformLocale))
    }

    actual fun formatMonthYear(date: HijriDate, locale: Locale): String {
        return date.javaDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", locale.platformLocale))
    }

    actual fun formatNumber(number: Int, locale: Locale): String {
        val javaLocale = locale.platformLocale
        if (javaLocale.language != "ar") return number.toString()

        return number.toString().convertToArabicIndicDigits()
    }
}