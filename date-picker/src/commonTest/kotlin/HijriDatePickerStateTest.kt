package io.github.helmy2

import androidx.compose.ui.text.intl.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HijriDatePickerStateTest {

    private val locale = Locale("en")
    private val yearRange = 1400..1500

    @Test
    fun givenNoInitialDate_whenCreated_thenSelectedDateIsNullAndDisplaysCurrentYearMonth() {
        val state = HijriDatePickerState(null, locale, yearRange)
        assertNull(state.selectedDate)
        val now = HijriCalendar.now()
        assertEquals(Pair(now.year, now.month), state.displayedYearMonth)
    }

    @Test
    fun givenInitialDate_whenCreated_thenSelectedDateAndDisplayedYearMonthMatch() {
        val initialDate = HijriCalendar.of(1447, 9, 10)
        val state = HijriDatePickerState(initialDate, locale, yearRange)
        assertEquals(initialDate, state.selectedDate)
        assertEquals(Pair(initialDate.year, initialDate.month), state.displayedYearMonth)
    }

    @Test
    fun givenState_whenOnDaySelected_thenSelectedDateAndDisplayedYearMonthUpdate() {
        val state = HijriDatePickerState(null, locale, yearRange)
        val newDate = HijriCalendar.of(1447, 10, 5)
        state.onDaySelected(newDate)
        assertEquals(newDate, state.selectedDate)
        assertEquals(Pair(newDate.year, newDate.month), state.displayedYearMonth)
    }

    @Test
    fun givenState_whenOnYearSelected_thenSelectedDateDisplayedYearMonthAndPickerModeUpdate() {
        val initialDate = HijriCalendar.of(1447, 9, 30)
        val state = HijriDatePickerState(initialDate, locale, yearRange)

        val selectedYear = 1448
        state.onYearSelected(selectedYear)

        val expectedMaxDay = HijriCalendar.of(selectedYear, initialDate.month, 1).lengthOfMonth()
        val expectedDay = if (initialDate.day > expectedMaxDay) expectedMaxDay else initialDate.day
        val expectedDate = HijriCalendar.of(selectedYear, initialDate.month, expectedDay)

        assertEquals(expectedDate, state.selectedDate)
        assertEquals(Pair(selectedYear, initialDate.month), state.displayedYearMonth)
        assertEquals(PickerMode.Month, state.pickerMode)
    }

    @Test
    fun givenPickerModeMonth_whenOnTogglePickerMode_thenPickerModeChangesToYear() {
        val state = HijriDatePickerState(null, locale, yearRange)
        assertEquals(PickerMode.Month, state.pickerMode)
        state.onTogglePickerMode()
        assertEquals(PickerMode.Year, state.pickerMode)
    }

    @Test
    fun givenPickerModeYear_whenOnTogglePickerMode_thenPickerModeChangesToMonth() {
        val state = HijriDatePickerState(null, locale, yearRange)
        state.onTogglePickerMode() // To Year mode
        assertEquals(PickerMode.Year, state.pickerMode)
        state.onTogglePickerMode()
        assertEquals(PickerMode.Month, state.pickerMode)
    }

    @Test
    fun givenDisplayedYearMonthDecember_whenOnNextMonth_thenYearIncrementsAndMonthIsJanuary() {
        val state = HijriDatePickerState(null, locale, yearRange)
        state.setDisplayedYearMonth(Pair(1447, 12))
        state.onNextMonth()
        assertEquals(Pair(1448, 1), state.displayedYearMonth)
    }

    @Test
    fun givenDisplayedYearMonthJanuary_whenOnPreviousMonth_thenYearDecrementsAndMonthIsDecember() {
        val state = HijriDatePickerState(null, locale, yearRange)
        state.setDisplayedYearMonth(Pair(1447, 1))
        state.onPreviousMonth()
        assertEquals(Pair(1446, 12), state.displayedYearMonth)
    }

    @Test
    fun givenDisplayedYearMonth_whenOnNextMonth_thenMonthIncrements() {
        val state = HijriDatePickerState(null, locale, yearRange)
        state.setDisplayedYearMonth(Pair(1447, 5))
        state.onNextMonth()
        assertEquals(Pair(1447, 6), state.displayedYearMonth)
    }

    @Test
    fun givenDisplayedYearMonth_whenOnPreviousMonth_thenMonthDecrements() {
        val state = HijriDatePickerState(null, locale, yearRange)
        state.setDisplayedYearMonth(Pair(1447, 6))
        state.onPreviousMonth()
        assertEquals(Pair(1447, 5), state.displayedYearMonth)
    }

    @Test
    fun givenSelectedDateNull_whenOnYearSelected_thenSelectedDateUsesDayOneAndPickerModeMonth() {
        // Given
        val initialMonth = 9
        val state = HijriDatePickerState(
            initialDate = null,
            locale = Locale("en"),
            yearRange = 1400..1500
        )
        // Set displayedYearMonth manually to have a month value
        state.setDisplayedYearMonth(Pair(1447, initialMonth))

        val newYear = 1448

        // When
        state.onYearSelected(newYear)

        // Then
        val expectedMaxDay = HijriCalendar.of(newYear, initialMonth, 1).lengthOfMonth()

        val expectedDate = HijriCalendar.of(newYear, initialMonth, 1.coerceAtMost(expectedMaxDay))
        assertEquals(expectedDate, state.selectedDate)
        assertEquals(Pair(newYear, initialMonth), state.displayedYearMonth)
        assertEquals(PickerMode.Month, state.pickerMode)
    }
}
