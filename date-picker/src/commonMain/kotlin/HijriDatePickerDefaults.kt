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
import io.github.helmy2.date_picker.generated.resources.Res
import io.github.helmy2.date_picker.generated.resources.hijri_date_picker_headline_default
import io.github.helmy2.date_picker.generated.resources.hijri_date_picker_title
import org.jetbrains.compose.resources.stringResource

/**
 * Creates and remembers a [HijriDatePickerState].
 *
 * @param initialDate The initial date to be selected, or null for no selection.
 * @param locale The locale to be used for all date logic. Defaults to [Locale.current].
 * @param yearRange An optional range of years to display in the year picker.
 * If null, a default range of (current year ± 50) will be used.
 */
@Composable
fun rememberHijriDatePickerState(
    initialDate: HijriDate? = null,
    locale: Locale = Locale.current,
    yearRange: IntRange = (HijriCalendar.now().year - 50)..(HijriCalendar.now().year + 50)
): HijriDatePickerState {
    return remember(initialDate, locale, yearRange) {
        HijriDatePickerState(
            initialDate = initialDate,
            locale = locale,
            yearRange = yearRange
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
     * Creates and remembers the default [HijriDatePickerStrings].
     */
    @Composable
    fun strings(): HijriDatePickerStrings {
        return HijriDatePickerStrings(
            selectDateTitle = stringResource(Res.string.hijri_date_picker_title),
            selectDateHeadlineDefault = stringResource(Res.string.hijri_date_picker_headline_default)
        )
    }

    /**
     * UPDATED: The default composable for the title of the date picker.
     *
     * @param strings The [HijriDatePickerStrings] to use.
     * @param modifier The [Modifier] to be applied to the title.
     * @param contentColor The color for the text.
     */
    @Composable
    fun DatePickerTitle(
        strings: HijriDatePickerStrings, 
        modifier: Modifier = Modifier,
        contentColor: Color = colors().titleContentColor
    ) {
        Text(
            text = strings.selectDateTitle,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            modifier = modifier.padding(start = 24.dp, end = 12.dp, top = 16.dp, bottom = 12.dp)
        )
    }

    /**
     * UPDATED: The default composable for the headline of the date picker.
     *
     * @param selectedDate The [HijriDate] to display.
     * @param dateFormatter The [HijriDatePickerFormatter] to use.
     * @param strings The [HijriDatePickerStrings] to use.
     * @param modifier The [Modifier] to be applied to the headline.
     * @param contentColor The color for the text.
     */
    @Composable
    fun DatePickerHeadline(
        selectedDate: HijriDate?,
        dateFormatter: HijriDatePickerFormatter,
        strings: HijriDatePickerStrings, 
        modifier: Modifier = Modifier,
        contentColor: Color = colors().headlineContentColor,
    ) {
        val text = selectedDate?.let {
            dateFormatter.formatHeadlineDate(it, Locale.current)
        } ?: strings.selectDateHeadlineDefault 

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