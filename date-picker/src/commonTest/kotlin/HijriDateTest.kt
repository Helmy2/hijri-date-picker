package io.github.helmy2

import androidx.compose.ui.text.intl.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HijriDateTest {

    @Test
    fun givenValidYearMonthDay_whenCreatingHijriDate_thenPropertiesMatch() {
        // Given
        val year = 1447
        val month = 9
        val day = 1

        // When
        val date = HijriCalendar.of(year, month, day)

        // Then
        assertEquals(year, date.year, "Year should match")
        assertEquals(month, date.month, "Month should match")
        assertEquals(day, date.day, "Day should match")
    }

    @Test
    fun givenInvalidMonth_whenCreatingHijriDate_thenThrowsIllegalArgumentException() {
        // Given
        val invalidMonth = 13

        // When & Then
        assertFailsWith<IllegalArgumentException> {
            HijriCalendar.of(1447, invalidMonth, 10)
        }
    }

    @Test
    fun givenInvalidDay_whenCreatingHijriDate_thenThrowsIllegalArgumentException() {
        // Given
        val invalidDay = 32

        // When & Then
        assertFailsWith<IllegalArgumentException> {
            HijriCalendar.of(1447, 9, invalidDay)
        }
    }

    @Test
    fun givenTwoHijriDatesWithSameValues_whenCompared_thenAreEqual() {
        // Given
        val d1 = HijriCalendar.of(1447, 9, 1)
        val d2 = HijriCalendar.of(1447, 9, 1)

        // When & Then
        assertTrue(d1 == d2, "Equal dates should be equal")
        assertTrue(d1.hashCode() == d2.hashCode(), "Hashcodes for equal dates should be equal")
    }

    @Test
    fun givenTwoHijriDatesWithDifferentValues_whenCompared_thenAreEqual() {
        // Given
        val d1 = HijriCalendar.of(1447, 9, 1)
        val d2 = HijriCalendar.of(1447, 9, 2)

        // When & Then
        assertFalse(d1 == d2, "Different dates should not be equal")
        assertFalse(d1 == d2, "Different dates should not be equal")
        assertFalse(
            d1.hashCode() == d2.hashCode(),
            "Hashcodes for different dates should not be equal"
        )
    }

    @Test
    fun givenTwoHijriDatesWithDifferentValues_whenCompared_thenAreNotEqual() {
        // Given
        val d1 = HijriCalendar.of(1447, 9, 1)
        val d2 = HijriCalendar.of(1447, 10, 1)

        // When & Then
        assertTrue(d1 != d2, "Different dates should not be equal")
    }

    @Test
    fun givenHijriDate_whenConvertingToString_thenContainsYearMonthDay() {
        // Given
        val date = HijriCalendar.of(1447, 9, 1)

        // When
        val result = date.toString()

        // Then
        assertTrue(result.contains("1447"), "String should contain year")
        assertTrue(result.contains("9"), "String should contain month")
        assertTrue(result.contains("1"), "String should contain day")
    }

    @Test
    fun givenHijriDate_whenGetLengthOfMonth_thenReturnsValidLength() {
        // Given
        val date = HijriCalendar.of(1447, 9, 1) // Ramadan 1447

        // When
        val length = date.lengthOfMonth()

        // Then
        // Hijri months are either 29 or 30 days - check in this range
        assertTrue(length in 29..30, "Length of month should be 29 or 30 days")
    }

    @Test
    fun givenHijriDateAndPattern_whenFormatHijriDate_thenFormattedStringContainsExpectedParts() {
        // Given
        val date = HijriCalendar.of(1447, 9, 1)
        val pattern = "yyyy MMMM d E"
        val locale = Locale("en")

        // When
        val formatted = formatHijriDate(date, pattern, locale)

        // Then
        assertTrue(formatted.contains("1447"), "Formatted string should contain year")
        assertTrue(
            formatted.contains("Ramadan") || formatted.contains("شعبان"),
            "Formatted string should contain month name"
        )
        assertTrue(
            formatted.matches(Regex(""".*\b1\b.*""")),
            "Formatted string should contain day number"
        )
        // The day of week is locale-specific - checking presence is enough
        assertTrue(formatted.isNotEmpty())
    }

    @Test
    fun givenCurrentTime_whenNowCalled_thenReturnsValidHijriDate() {
        // When
        val nowDate = HijriCalendar.now()

        // Then
        assertNotNull(nowDate, "HijriCalendar.now() should return a non-null HijriDate")

        with(nowDate) {
            assertTrue(year > 0, "Year should be positive")
            assertTrue(month in 1..12, "Month should be between 1 and 12")
            assertTrue(day in 1..30, "Day should be between 1 and 30")
            assertTrue(dayOfWeek in 1..7, "Day of week should be between 1 (Mon) and 7 (Sun)")
        }
    }

    @Test
    fun givenSameInstance_whenEquals_thenReturnsTrue() {
        val date = HijriCalendar.now()
        assertTrue(date == date)
    }

    @Test
    fun givenEqualHijriDates_whenEquals_thenReturnsTrue() {
        val d1 = HijriCalendar.of(1447, 9, 1)
        val d2 = HijriCalendar.of(1447, 9, 1)
        assertTrue(d1 == d2)
        assertTrue(d1.hashCode() == d2.hashCode())
    }

    @Test
    fun givenDifferentYear_whenEquals_thenReturnsFalse() {
        val d1 = HijriCalendar.of(1447, 9, 1)
        val d2 = HijriCalendar.of(1448, 9, 1)
        assertTrue(d1 != d2)
    }

    @Test
    fun givenDifferentMonth_whenEquals_thenReturnsFalse() {
        val d1 = HijriCalendar.of(1447, 9, 1)
        val d2 = HijriCalendar.of(1447, 10, 1)
        assertTrue(d1 != d2)
    }

    @Test
    fun givenDifferentDay_whenEquals_thenReturnsFalse() {
        val d1 = HijriCalendar.of(1447, 9, 1)
        val d2 = HijriCalendar.of(1447, 9, 2)
        assertTrue(d1 != d2)
    }

    @Test
    fun givenNonHijriDateObject_whenEquals_thenReturnsFalse() {
        val date = HijriCalendar.now()
        assertFalse(date.equals("not a HijriDate"))
    }

    @Test
    fun givenNull_whenEquals_thenReturnsFalse() {
        val date = HijriCalendar.now()
        assertFalse(date.equals(null))
    }

}
