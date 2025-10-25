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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

// Height constants for stable layout ---
private val DAY_CELL_HEIGHT = 40.dp
private val DAY_CELL_SPACER = 4.dp
private val MONTH_HEADER_HEIGHT = 52.dp
private val WEEKDAY_HEADER_HEIGHT = 24.dp
private val CALENDAR_TOP_PADDING = 8.dp
private val WEEKDAY_TOP_PADDING = 8.dp

// 6 rows of (day + spacer)
private val CALENDAR_GRID_HEIGHT = (DAY_CELL_HEIGHT + DAY_CELL_SPACER) * 6

// Total height of the month view
private val CALENDAR_VIEW_HEIGHT = MONTH_HEADER_HEIGHT + CALENDAR_TOP_PADDING +
        WEEKDAY_HEADER_HEIGHT + WEEKDAY_TOP_PADDING +
        CALENDAR_GRID_HEIGHT
// ----------------------------------------------


/**
 * Public composable API for the Hijri Date Picker.
 * (Omitted KDoc for brevity, no changes here)
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

// --- Helper functions for Pager ---
// We use 1/1/1 AH as our absolute reference point (Page 0).
private val REFERENCE_YEAR_MONTH = Pair(1, 1)

/**
 * Calculates the total number of months between two (year, month) pairs.
 * (This function is updated for the new reference)
 */
private fun monthsDifference(start: Pair<Int, Int>, end: Pair<Int, Int>): Int {
    return (end.first - start.first) * 12 + (end.second - start.second)
}

/**
 * Adds a number of months to a given (year, month) pair.
 * (This function is updated for the new reference)
 */
private fun plusMonths(start: Pair<Int, Int>, months: Int): Pair<Int, Int> {
    // We calculate months from 1/1 AH, so we adjust by -1
    val totalMonths = (start.first - 1) * 12 + (start.second - 1) + months
    val newYear = totalMonths / 12 + 1 // Convert back to 1-based year
    val newMonth = totalMonths % 12 + 1 // Convert back to 1-based month
    return Pair(newYear, newMonth)
}

@Composable
internal fun HijriCalendarView(
    state: HijriDatePickerState,
    colors: HijriDatePickerColors,
    dateFormatter: HijriDatePickerFormatter
) {
    val onSurface = MaterialTheme.colorScheme.onSurface

    fun pageToMonth(page: Int): Pair<Int, Int> {
        return plusMonths(REFERENCE_YEAR_MONTH, page)
    }

    fun monthToPage(month: Pair<Int, Int>): Int {
        return monthsDifference(REFERENCE_YEAR_MONTH, month)
    }

    val pagerState = rememberPagerState(
        initialPage = monthToPage(state.displayedYearMonth),
        pageCount = { Int.MAX_VALUE }
    )

    // --- State Synchronization ---
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .filter { it != monthToPage(state.displayedYearMonth) }
            .collect { page ->
                val newMonth = pageToMonth(page)
                state.setDisplayedYearMonth(newMonth)
            }
    }

    LaunchedEffect(state.displayedYearMonth) {
        val targetPage = monthToPage(state.displayedYearMonth)
        if (targetPage != pagerState.currentPage) {
            if (targetPage == pagerState.currentPage + 1 || targetPage == pagerState.currentPage - 1) {
                pagerState.animateScrollToPage(targetPage)
            } else {
                pagerState.scrollToPage(targetPage)
            }
        }
    }
    // -----------------------------

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MONTH_HEADER_HEIGHT),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier.clickable { state.onTogglePickerMode() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                val titleMonth = HijriCalendar.of(
                    state.displayedYearMonth.first,
                    state.displayedYearMonth.second,
                    1
                )
                Text(
                    text = dateFormatter.formatMonthYear(titleMonth, state.locale),
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

        Spacer(Modifier.height(CALENDAR_TOP_PADDING))

        // Weekday headers (localized labels)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(WEEKDAY_HEADER_HEIGHT),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val shortDays = getNarrowWeekdayNames(state.locale)

            shortDays.forEach { d ->
                Text(
                    text = d,
                    modifier = Modifier.width(DAY_CELL_HEIGHT),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(WEEKDAY_TOP_PADDING))

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(CALENDAR_GRID_HEIGHT)
        ) { page ->
            val (year, month) = pageToMonth(page)

            HijriCalendarGrid(
                year = year,
                month = month,
                selectedDate = state.selectedDate,
                locale = state.locale,
                colors = colors,
                onDayClick = { state.onDaySelected(it) }
            )
        }
    }
}

@Composable
internal fun HijriCalendarGrid(
    year: Int,
    month: Int,
    selectedDate: HijriDate?,
    locale: Locale,
    colors: HijriDatePickerColors,
    onDayClick: (HijriDate) -> Unit
) {
    val firstOfMonth = HijriCalendar.of(year, month, 1)

    val dowOfFirst = firstOfMonth.dayOfWeek // ISO 1=Mon..7=Sun
    val startIndex = (dowOfFirst + 1) % 7 // Sat=0, Sun=1, ... Fri=6

    val daysInMonth = firstOfMonth.lengthOfMonth()
    val cells = mutableListOf<HijriDate?>()
    for (i in 0 until startIndex) cells.add(null)
    for (d in 1..daysInMonth) cells.add(HijriCalendar.of(year, month, d))
    while (cells.size < 42) cells.add(null) // Ensure 6 rows

    Column {
        for (row in 0 until 6) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 0 until 7) {
                    val idx = row * 7 + col
                    val date = cells.getOrNull(idx)
                    DayCell(
                        date = date,
                        locale = locale,
                        colors = colors,
                        isSelected = date != null && selectedDate != null && areSameHijriDate(
                            date,
                            selectedDate
                        ),
                        isToday = date != null && areSameHijriDate(date, HijriCalendar.now()),
                        onClick = { d -> if (d != null) onDayClick(d) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(DAY_CELL_SPACER))
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
    val size = DAY_CELL_HEIGHT

    if (date == null) {
        Box(modifier = Modifier.width(size).height(size))
        return
    }

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

    val finalModifier = if (isToday && !isSelected) {
        cellModifier.border(1.dp, colors.todayDateBorderColor, CircleShape)
    } else {
        cellModifier
    }

    Box(
        modifier = finalModifier
            .clickable { onClick(date) },
        contentAlignment = Alignment.Center
    ) {
        val dayNumber = date.day
        Text(
            text = formatNumber(dayNumber, locale),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = when {
                isSelected -> colors.onSelectedDayContainerColor
                isToday -> colors.todayDateContentColor
                else -> colors.dayContentColor
            }
        )
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
    val years = remember(state.yearRange, selectedYear) {
        if (state.yearRange != null) {
            // Use the developer-provided range
            state.yearRange.toList()
        } else {
            // Use the default ± 50 range
            val start = selectedYear - 50
            val end = selectedYear + 50
            (start..end).toList()
        }
    }

    val selectedYearIndex = remember(years, selectedYear) {
        years.indexOf(selectedYear).coerceAtLeast(0)
    }
    val itemsPerRow = 3
    val estimatedVisibleRows = 6
    val selectedRow = selectedYearIndex / itemsPerRow

    val firstVisibleRow = (selectedRow - (estimatedVisibleRows / 2)).coerceAtLeast(0)
    val initialFirstVisibleIndex = (firstVisibleRow * itemsPerRow)
        .coerceIn(0, (years.size - 1).coerceAtLeast(0))

    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = initialFirstVisibleIndex
    )

    LazyVerticalGrid(
        state = gridState,
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .height(CALENDAR_VIEW_HEIGHT),
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