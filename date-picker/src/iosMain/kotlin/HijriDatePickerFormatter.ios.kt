package io.github.helmy2


import androidx.compose.runtime.Stable
import androidx.compose.ui.text.intl.Locale
import io.github.helmy2.internal.convertToArabicIndicDigits
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.languageCode

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

    actual fun formatNumber(number: Int, locale: Locale): String {
        val formatter = NSNumberFormatter()
        formatter.locale = locale.toNSLocale()
        val formattedNumber =
            formatter.stringFromNumber(NSNumber(number.toDouble())) ?: number.toString()
        return if (formatter.locale.languageCode == "ar") formattedNumber.convertToArabicIndicDigits()
        else formattedNumber
    }
}