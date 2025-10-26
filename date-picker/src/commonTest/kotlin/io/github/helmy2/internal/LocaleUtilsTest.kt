package io.github.helmy2.internal

import androidx.compose.ui.text.intl.Locale
import io.github.helmy2.HijriCalendar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InternalUtilsTest {

    @Test
    fun givenYearMonth_whenGenerateCalendarCells_thenReturns42CellsWithCorrectPadding() {
        val year = 1447
        val month = 9
        val cells = generateCalendarCells(year, month)
        assertEquals(42, cells.size)

        val firstOfMonth = cells.indexOfFirst { it != null }
        assertTrue(firstOfMonth >= 0, "First day of month should be in list")

        val lastOfMonth = cells.indexOfLast { it != null }
        val expectedDays = HijriCalendar.of(year, month, 1).lengthOfMonth()
        assertEquals(expectedDays, lastOfMonth - firstOfMonth + 1, "Days count in grid should match month length")

        // Check nulls padding before and after
        for (i in 0 until firstOfMonth) {
            assertEquals(null, cells[i])
        }
        for (i in lastOfMonth + 1 until 42) {
            assertEquals(null, cells[i])
        }
    }

    @Test
    fun givenStringWithWesternDigits_whenConvertToArabicIndicDigits_thenDigitsAreConverted() {
        val original = "Date: 123, Year: 2025"
        val expected = "Date: ١٢٣, Year: ٢٠٢٥"
        val converted = original.convertToArabicIndicDigits()
        assertEquals(expected, converted)
    }

    @Test
    fun givenEnglishLocale_whenGetNarrowWeekdayNames_thenCorrectOrderAndNames() {
        val locale = Locale("en")
        val weekdayNames = getNarrowWeekdayNames(locale)

        // Should be 7 days
        assertEquals(7, weekdayNames.size)

        // Should start from Saturday and end at Friday, example
        assertEquals("S", weekdayNames.first())
        assertEquals("F", weekdayNames.last())
    }

    @Test
    fun givenArabicLocale_whenGetNarrowWeekdayNames_thenNamesAreArabicLetters() {
        val locale = Locale("ar")
        val weekdayNames = getNarrowWeekdayNames(locale)

        assertEquals(7, weekdayNames.size)

        // Check that at least one day name contains Arabic characters
        val arabicRange = '\u0600'..'\u06FF'
        assertTrue(weekdayNames.any { it.any { char -> char in arabicRange } })
    }
}
