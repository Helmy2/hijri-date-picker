package io.github.helmy2

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.helmy2.internal.HijriCalendarView
import io.github.helmy2.internal.HijriYearPicker

// Height constants for stable layout ---
internal val DAY_CELL_HEIGHT = 40.dp
internal val DAY_CELL_SPACER = 4.dp
internal val MONTH_HEADER_HEIGHT = 52.dp
internal val WEEKDAY_HEADER_HEIGHT = 24.dp
internal val CALENDAR_TOP_PADDING = 8.dp
internal val WEEKDAY_TOP_PADDING = 8.dp

// 6 rows of (day + spacer)
internal val CALENDAR_GRID_HEIGHT = (DAY_CELL_HEIGHT + DAY_CELL_SPACER) * 6

// Total height of the month view
internal val CALENDAR_VIEW_HEIGHT = MONTH_HEADER_HEIGHT + CALENDAR_TOP_PADDING +
        WEEKDAY_HEADER_HEIGHT + WEEKDAY_TOP_PADDING +
        CALENDAR_GRID_HEIGHT
// ----------------------------------------------


/**
 * A composable that provides a full-screen Hijri (Islamic) date picker.
 *
 * This component allows users to select a date from the Hijri calendar. It consists of a
 * title, a headline showing the selected date, and a calendar view for day, month, and year
 * selection. The picker's state, including the selected date and current view (month or year),
 * is managed by a [HijriDatePickerState] instance.
 *
 * The picker can be customized with different colors, strings, and date formatting.
 *
 * Example usage:
 * ```
 * val state = rememberHijriDatePickerState()
 * HijriDatePicker(state = state)
 * ```
 *
 * @param state The state object to be used to control and observe the date picker's state.
 * @param modifier The modifier to be applied to the date picker.
 * @param colors An optional [HijriDatePickerColors] that will be used to resolve the colors used for
 * this date picker in different states. See [HijriDatePickerDefaults.colors].
 * @param strings An optional [HijriDatePickerStrings] that will be used to provide custom strings
 * for the date picker, such as the "OK" and "Cancel" button labels. See
 * [HijriDatePickerDefaults.strings].
 * @param dateFormatter An optional [HijriDatePickerFormatter] that will be used to format dates
 * for display. See [HijriDatePickerDefaults.dateFormatter].
 * @param title An optional composable to be displayed as the title of the date picker.
 * Defaults to a standard title implementation.
 * @param headline An optional composable to be displayed as the headline of the date picker,
 * which typically shows the selected date. Defaults to a standard headline implementation.
 */
@Composable
fun HijriDatePicker(
    state: HijriDatePickerState,
    modifier: Modifier = Modifier,
    colors: HijriDatePickerColors = HijriDatePickerDefaults.colors(),
    strings: HijriDatePickerStrings = HijriDatePickerDefaults.strings(),
    dateFormatter: HijriDatePickerFormatter = HijriDatePickerDefaults.dateFormatter(),
    title: (@Composable () -> Unit)? = {
        HijriDatePickerDefaults.DatePickerTitle(
            strings = strings,
            contentColor = colors.titleContentColor
        )
    },
    headline: (@Composable () -> Unit)? = {
        HijriDatePickerDefaults.DatePickerHeadline(
            selectedDate = state.selectedDate,
            dateFormatter = dateFormatter,
            strings = strings,
            contentColor = colors.headlineContentColor
        )
    },
) {
    HijriDatePickerDialogContent(
        state = state,
        modifier = modifier,
        colors = colors,
        dateFormatter = dateFormatter,
        title = title,
        headline = headline,
        strings = strings
    )
}

@Composable
internal fun HijriDatePickerDialogContent(
    state: HijriDatePickerState,
    modifier: Modifier = Modifier,
    colors: HijriDatePickerColors,
    dateFormatter: HijriDatePickerFormatter,
    title: (@Composable () -> Unit)?,
    headline: (@Composable () -> Unit)?,
    strings: HijriDatePickerStrings,
    animDuration: Int = 220
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        title?.invoke()
        headline?.invoke()

        HorizontalDivider()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(CALENDAR_VIEW_HEIGHT)
                .padding(horizontal = 12.dp)
        ) {
            AnimatedContent(
                targetState = state.pickerMode,
                transitionSpec = {
                    val forward = initialState == PickerMode.Month && targetState == PickerMode.Year

                    val enterTransition = slideInHorizontally(
                        initialOffsetX = { fullWidth ->
                            if (forward) fullWidth else -fullWidth
                        },
                        animationSpec = tween(durationMillis = animDuration)
                    ) + fadeIn(animationSpec = tween(durationMillis = animDuration))

                    val exitTransition = slideOutHorizontally(
                        targetOffsetX = { fullWidth ->
                            if (forward) -fullWidth else fullWidth
                        },
                        animationSpec = tween(durationMillis = animDuration)
                    ) + fadeOut(animationSpec = tween(durationMillis = animDuration))

                    enterTransition togetherWith exitTransition
                },
                contentKey = { it }
            ) { targetMode ->
                when (targetMode) {
                    PickerMode.Month -> {
                        HijriCalendarView(
                            state = state,
                            colors = colors,
                            dateFormatter = dateFormatter,
                            strings = strings
                        )
                    }

                    PickerMode.Year -> {
                        HijriYearPicker(
                            state = state,
                            colors = colors,
                            dateFormatter = dateFormatter
                        )
                    }
                }
            }
        }
    }
}