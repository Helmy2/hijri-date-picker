package io.github.helmy2

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Public composable API for the Hijri Date Picker.
 *
 * This composable is stateless and observes a [HijriDatePickerState].
 *
 * @param state The hoisted [HijriDatePickerState] that holds the selected date
 * and internal view state.
 * @param modifier The [Modifier] to be applied to the date picker.
 * @param colors The [HijriDatePickerColors] to customize the picker's appearance.
 * @param dateFormatter The [HijriDatePickerFormatter] to customize date formatting.
 * @param title The composable slot for the picker's title (e.g., "Select date").
 * @param headline The composable slot for the picker's headline (e.g., "12 Ramadan 1446").
 */
@Composable
fun HijriDatePicker(
    state: HijriDatePickerState,
    modifier: Modifier = Modifier,
    colors: HijriDatePickerColors = HijriDatePickerDefaults.colors(),
    dateFormatter: HijriDatePickerFormatter = HijriDatePickerDefaults.dateFormatter(),
    title: (@Composable () -> Unit)? = {
        HijriDatePickerDefaults.DatePickerTitle(
            contentColor = colors.titleContentColor
        )
    },
    headline: (@Composable () -> Unit)? = {
        HijriDatePickerDefaults.DatePickerHeadline(
            selectedDate = state.selectedDate,
            dateFormatter = dateFormatter,
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
        headline = headline
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
    animDuration: Int = 220
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // --- Header ---
        title?.invoke()
        headline?.invoke()

        HorizontalDivider()

        // --- Animated content: month grid <-> year grid ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                            dateFormatter = dateFormatter
                        )
                    }

                    PickerMode.Year -> {
                        HijriYearPicker(
                            state = state,
                            colors = colors
                        )
                    }
                }
            }
        }
    }
}

/* --------------------------- Calendar (Month) ------------------------- */

@Composable
internal fun HijriCalendarView(
    state: HijriDatePickerState,
    colors: HijriDatePickerColors,
    dateFormatter: HijriDatePickerFormatter
) {
    val isArabic = state.locale.language == "ar"
    val onSurface = MaterialTheme.colorScheme.onSurface

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier.clickable { state.onTogglePickerMode() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                val displayedHijriMonthStart =
                    KmpHijriCalendar.of(
                        state.displayedYearMonth.first,
                        state.displayedYearMonth.second,
                        1
                    )
                Text(
                    text = dateFormatter.formatMonthYear(displayedHijriMonthStart, state.locale),
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Change year",
                    tint = onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = { state.onPreviousMonth() }) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous month",
                    tint = onSurface
                )
            }

            IconButton(onClick = { state.onNextMonth() }) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next month",
                    tint = onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Weekday headers (simple localized labels)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val shortDays = getNarrowWeekdayNames(state.locale)
            shortDays.forEach { d ->
                Text(
                    text = d,
                    modifier = Modifier.width(40.dp),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar grid
        HijriCalendarGrid(
            state = state,
            colors = colors
        )
    }
}

@Composable
internal fun HijriCalendarGrid(
    state: HijriDatePickerState,
    colors: HijriDatePickerColors
) {
    val year = state.displayedYearMonth.first
    val month = state.displayedYearMonth.second
    val selectedDate = state.selectedDate

    // First day of the hijri month
    val firstOfMonth = KmpHijriCalendar.of(year, month, 1)

    val dowOfFirst = firstOfMonth.dayOfWeek // ISO 1=Mon..7=Sun
    val startIndex = (dowOfFirst + 1) % 7 // Sat=0, Sun=1, ... Fri=6

    val daysInMonth = firstOfMonth.lengthOfMonth()
    val cells = mutableListOf<HijriDate?>()
    for (i in 0 until startIndex) cells.add(null)
    for (d in 1..daysInMonth) cells.add(KmpHijriCalendar.of(year, month, d))
    while (cells.size < 42) cells.add(null)

    Column {
        for (row in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 0 until 7) {
                    val idx = row * 7 + col
                    val date = cells[idx]
                    DayCell(
                        date = date,
                        locale = state.locale,
                        colors = colors,
                        isSelected = date != null && selectedDate != null && areSameHijriDate(
                            date,
                            selectedDate
                        ),
                        isToday = date != null && areSameHijriDate(date, KmpHijriCalendar.now()),
                        onClick = { d -> if (d != null) state.onDaySelected(d) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
internal fun DayCell(
    date: HijriDate?,
    locale: Locale,
    colors: HijriDatePickerColors,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: (HijriDate?) -> Unit
) {
    val size = 40.dp

    val cellModifier = Modifier
        .width(size)
        .height(size)
        .clip(CircleShape)
        .background(
            when {
                isSelected -> colors.selectedDayContainerColor
                else -> Color.Transparent
            }
        )

    val finalModifier = if (date != null && isToday && !isSelected) {
        cellModifier.border(1.dp, colors.todayDateBorderColor, CircleShape)
    } else {
        cellModifier
    }


    Box(
        modifier = finalModifier
            .clickable(enabled = date != null) { onClick(date) },
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            val dayNumber = date.day
            Text(
                text = formatNumber(dayNumber, locale), // Using our expect util
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = when {
                    isSelected -> colors.onSelectedDayContainerColor
                    isToday -> colors.todayDateContentColor
                    else -> colors.dayContentColor
                }
            )
        } else {
            BasicText("")
        }
    }
}

/* --------------------------- Year Grid ------------------------------ */

@Composable
internal fun HijriYearPicker(
    state: HijriDatePickerState,
    colors: HijriDatePickerColors
) {
    val selectedYear = state.displayedYearMonth.first

    // Range: selectedYear ± 50
    val start = selectedYear - 50
    val end = selectedYear + 50
    val years = (start..end).toList()

    val selectedYearIndex = 50
    val itemsPerRow = 3
    val estimatedVisibleRows = 6
    val selectedRow = selectedYearIndex / itemsPerRow
    val firstVisibleRow = (selectedRow - (estimatedVisibleRows / 2)).coerceAtLeast(0)
    val initialFirstVisibleIndex = (firstVisibleRow * itemsPerRow)

    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = initialFirstVisibleIndex
    )

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(years) { year ->
                val isSelected = year == selectedYear
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (isSelected) colors.selectedYearContainerColor
                            else Color.Transparent
                        )
                        .clickable { state.onYearSelected(year) }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatNumber(year, state.locale),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) colors.onSelectedYearContainerColor else colors.yearContentColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    )
}