package io.github.helmy2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale

enum class PickerMode { Month, Year }

/**
 * A state object that can be hoisted to observe and control the [HijriDatePicker].
 *
 * @param initialDate The initial date to be selected.
 * @param locale The locale to be used for all date logic.
 */
class HijriDatePickerState(
    initialDate: HijriDate?,
    val locale: Locale,
) {
    /**
     * The currently selected date, or null if no date is selected.
     */
    var selectedDate by mutableStateOf(initialDate)
        private set

    /**
     * The month/year pair currently being displayed in the calendar.
     */
    var displayedYearMonth by mutableStateOf(
        Pair(
            initialDate?.year ?: KmpHijriCalendar.now().year,
            initialDate?.month ?: KmpHijriCalendar.now().month
        )
    )
        private set

    /**
     * The current view mode (Month or Year grid).
     */
    var pickerMode by mutableStateOf(PickerMode.Month)
        private set

    // --- Public Methods to Mutate State ---

    /**
     * Handles the selection of a specific day.
     *
     * @param date The [HijriDate] that was clicked.
     */
    fun onDaySelected(date: HijriDate) {
        selectedDate = date
        displayedYearMonth = Pair(date.year, date.month)
    }

    /**
     * Handles the selection of a specific year from the year picker.
     * This action also switches the view back to [PickerMode.Month].
     *
     * @param year The year that was selected.
     */
    fun onYearSelected(year: Int) {
        val month = displayedYearMonth.second
        val currentDay = selectedDate?.day ?: 1
        val maxDay = KmpHijriCalendar.of(year, month, 1).lengthOfMonth()
        val safeDay = currentDay.coerceAtMost(maxDay)

        selectedDate = KmpHijriCalendar.of(year, month, safeDay)
        displayedYearMonth = Pair(year, month)
        pickerMode = PickerMode.Month
    }

    /**
     * Toggles the [pickerMode] between [PickerMode.Month] and [PickerMode.Year].
     */
    fun onTogglePickerMode() {
        pickerMode = if (pickerMode == PickerMode.Month) PickerMode.Year else PickerMode.Month
    }

    /**
     * Changes the displayed month to the next month.
     */
    fun onNextMonth() {
        val (y, m) = displayedYearMonth
        val next = if (m == 12) Pair(y + 1, 1) else Pair(y, m + 1)
        displayedYearMonth = next
    }

    /**
     * Changes the displayed month to the previous month.
     */
    fun onPreviousMonth() {
        val (y, m) = displayedYearMonth
        val prev = if (m == 1) Pair(y - 1, 12) else Pair(y, m - 1)
        displayedYearMonth = prev
    }
}