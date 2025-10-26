package io.github.helmy2

import androidx.compose.ui.text.intl.Locale
import kotlin.test.Test
import kotlin.test.assertTrue

class HijriDatePickerFormatterTest {

    @Test
    fun givenHijriDate_whenFormatDateWithArabicIndicNumbers_thenContainsArabicIndicDigits() {
        // Given
        val date = HijriCalendar.of(1447, 9, 1)
        val pattern = "d MMMM yyyy"
        val arabicLocale = Locale("ar")

        // When
        val formatted = formatHijriDate(date, pattern, arabicLocale)

        // Then
        // Arabic-Indic digits Unicode range
        val arabicIndicDigit = '١' // Arabic-Indic digit one
        assertTrue(formatted.contains(arabicIndicDigit), "Formatted string should contain Arabic-Indic digits")
    }

    @Test
    fun givenHijriDate_whenFormatDateWithWesternNumbers_thenContainsWesternDigits() {
        // Given
        val date = HijriCalendar.of(1447, 9, 1)
        val pattern = "d MMMM yyyy"
        val englishLocale = Locale("en")

        // When
        val formatted = formatHijriDate(date, pattern, englishLocale)

        // Then
        val westernDigit = '1'
        assertTrue(formatted.contains(westernDigit), "Formatted string should contain Western (ASCII) digits")
    }

    @Test
    fun givenHijriDate_whenFormatDate_thenContainsYearMonthDay() {
        // Given
        val date = HijriCalendar.of(1447, 9, 1)
        val pattern = "yyyy MMMM d"
        val locale = Locale("en")

        // When
        val formatted = formatHijriDate(date, pattern, locale)

        // Then
        assertTrue(formatted.contains("1447"), "Formatted string should contain the year")
        assertTrue(formatted.contains("Ramadan") || formatted.contains("شعبان"), "Formatted string should contain a month name")
        assertTrue(formatted.contains("1"), "Formatted string should contain the day number")
    }

    @Test
    fun givenDifferentLocales_whenFormat_thenMonthNamesDiffer() {
        // Given
        val date = HijriCalendar.of(1447, 1, 1)
        val arabicLocale = Locale("ar")
        val englishLocale = Locale("en")

        // When
        val arabicFormatted = formatHijriDate(date, "MMMM", arabicLocale)
        val englishFormatted = formatHijriDate(date, "MMMM", englishLocale)

        // Then
        assertTrue(arabicFormatted != englishFormatted, "Month names should differ between Arabic and English locales")
    }

    private val formatter = getHijriDatePickerFormatter()

    @Test
    fun givenArabicLocale_whenFormatHeadlineDate_thenUsesArabicPattern() {
        val date = HijriCalendar.of(1447, 9, 10)
        val arabicLocale = Locale("ar")

        val result = formatter.formatHeadlineDate(date, arabicLocale)

        // Arabic pattern is "d MMMM" - check for day number and Arabic month name presence
        assertTrue(result.contains(date.day.toString()), "Headline should contain the day number")
        // Arabic month name should contain Arabic characters
        val arabicRange = '\u0600'..'\u06FF'
        assertTrue(result.any { it in arabicRange }, "Headline should contain Arabic characters")
    }

    @Test
    fun givenNonArabicLocale_whenFormatHeadlineDate_thenUsesEnglishPattern() {
        val date = HijriCalendar.of(1447, 9, 10)
        val englishLocale = Locale("en")

        val result = formatter.formatHeadlineDate(date, englishLocale)

        // English pattern is "E, MMM d" - check for abbreviated weekday name presence (e.g. Mon)
        println(result)
        assertTrue(result.matches(Regex("""\w{3}, \w{3}\.?\s\d+""")), "Headline should match English date pattern")
    }

    @Test
    fun givenDate_whenFormatMonthYear_thenReturnsLocalizedMonthYear() {
        val date = HijriCalendar.of(1447, 9, 10)
        val locale = Locale("en")

        val result = formatter.formatMonthYear(date, locale)

        assertTrue(result.contains("1447"), "Month year string should contain the year")
        assertTrue(result.isNotEmpty(), "Month year string should not be empty")
    }

    @Test
    fun givenArabicLocale_whenFormatNumber_thenReturnsArabicIndicDigits() {
        val arabicLocale = Locale("ar")

        val formatted = formatter.formatNumber(12345, arabicLocale)

        val arabicIndicDigits = "٠١٢٣٤٥٦٧٨٩"
        // All digits should be Arabic-Indic digits in output string
        assertTrue(formatted.all { it in arabicIndicDigits }, "Number should be formatted with Arabic-Indic digits")
    }

    @Test
    fun givenNonArabicLocale_whenFormatNumber_thenReturnsWesternDigits() {
        val englishLocale = Locale("en")

        val formatted = formatter.formatNumber(12345, englishLocale)

        val westernDigits = "0123456789"
        // All digits should be Western digits in output string
        assertTrue(formatted.all { it in westernDigits }, "Number should be formatted with Western digits")
    }
}
