package io.github.helmy2

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp

/**
 * Creates and remembers a [HijriDatePickerState].
 *
 * @param initialDate The initial date to be selected, or null for no selection.
 * @param locale The locale to be used for all date logic. Defaults to [Locale.current].
 */
@Composable
fun rememberHijriDatePickerState(
    initialDate: HijriDate? = null,
    locale: Locale = Locale.current
): HijriDatePickerState {
    return remember(initialDate, locale) {
        HijriDatePickerState(
            initialDate = initialDate,
            locale = locale
        )
    }
}

/**
 * Contains default values used by the [HijriDatePicker].
 */
object HijriDatePickerDefaults {

    /**
     * Creates and remembers a [HijriDatePickerFormatter].
     */
    @Composable
    fun dateFormatter(): HijriDatePickerFormatter {
        return remember {
            getHijriDatePickerFormatter()
        }
    }

    /**
     * Creates and remembers a [HijriDatePickerColors] that will be used by the date picker.
     */
    @Composable
    fun colors(): HijriDatePickerColors = defaultDatePickerColors()

    /**
     * The default composable for the title of the date picker.
     *
     * @param modifier The [Modifier] to be applied to the title.
     * @param contentColor The color for the text.
     */
    @Composable
    fun DatePickerTitle(
        modifier: Modifier = Modifier,
        contentColor: Color = colors().titleContentColor
    ) {
        val text = if (Locale.current.language == "ar") "اختر التاريخ" else "Select date"
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            modifier = modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp, bottom = 12.dp)
        )
    }

    /**
     * The default composable for the headline of the date picker.
     *
     * @param selectedDate The [HijriDate] to display.
     * @param dateFormatter The [HijriDatePickerFormatter] to use.
     * @param modifier The [Modifier] to be applied to the headline.
     * @param contentColor The color for the text.
     */
    @Composable
    fun DatePickerHeadline(
        selectedDate: HijriDate?,
        dateFormatter: HijriDatePickerFormatter,
        modifier: Modifier = Modifier,
        contentColor: Color = colors().headlineContentColor,
    ) {
        // TODO: Replace with string resources
        val defaultText = if (Locale.current.language == "ar") "حدد تاريخ" else "Select a date"

        val text = selectedDate?.let {
            dateFormatter.formatHeadlineDate(it, Locale.current)
        } ?: defaultText

        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
            modifier = modifier.padding(start = 24.dp, end = 12.dp, bottom = 12.dp)
        )
    }
}

expect fun getHijriDatePickerFormatter(): HijriDatePickerFormatter