package io.github.helmy2.internal

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.helmy2.CALENDAR_GRID_HEIGHT
import io.github.helmy2.CALENDAR_TOP_PADDING
import io.github.helmy2.DAY_CELL_HEIGHT
import io.github.helmy2.DAY_CELL_SPACER
import io.github.helmy2.HijriCalendar
import io.github.helmy2.HijriDate
import io.github.helmy2.HijriDatePickerColors
import io.github.helmy2.HijriDatePickerFormatter
import io.github.helmy2.HijriDatePickerState
import io.github.helmy2.MONTH_HEADER_HEIGHT
import io.github.helmy2.WEEKDAY_HEADER_HEIGHT
import io.github.helmy2.WEEKDAY_TOP_PADDING
import io.github.helmy2.formatNumber
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

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