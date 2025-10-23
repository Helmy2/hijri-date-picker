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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private enum class PickerMode { Month, Year }

/**
 * Public composable API.
 *
 * @param locale controls month names, numerals, and RTL if Arabic.
 * @param initialDate initial selected Hijri date.
 * @param onDateSelected callback when user presses OK with selected hijri date.
 * @param onDismissRequest cancel/dismiss callback.
 */
@Composable
public fun HijriDatePicker(
    locale: Locale,
    initialDate: KmpHijriDate = KmpHijriCalendar.now(),
    onDateSelected: (KmpHijriDate) -> Unit,
    onDismissRequest: () -> Unit
) {
    val isArabic = locale.language == "ar"

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr
    ) {
        Surface(
            tonalElevation = 8.dp,
            shape = MaterialTheme.shapes.extraLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            HijriDatePickerDialogContent(
                locale = locale,
                initialDate = initialDate,
                onDateConfirmed = onDateSelected,
                onDismissRequest = onDismissRequest
            )
        }
    }
}

@Composable
internal fun HijriDatePickerDialogContent(
    locale: Locale,
    initialDate: KmpHijriDate,
    onDateConfirmed: (KmpHijriDate) -> Unit, 
    onDismissRequest: () -> Unit
) {
    // state: displayed month/year and selected date
    var displayedYearMonth by remember {
        mutableStateOf(
            Pair(
                initialDate.year, 
                initialDate.month 
            )
        )
    }
    var selectedDate by remember { mutableStateOf(initialDate) }
    var mode by remember { mutableStateOf(PickerMode.Month) }
    // Animation durations
    val animDuration = 220

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
    ) {
        // Header (clickable - toggles to year grid)
        HijriDatePickerHeader(
            locale = locale,
            selectedDate = selectedDate
        )

        HorizontalDivider()

        // Animated content: month grid <-> year grid with slide
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            AnimatedContent(
                targetState = mode,
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
                        // Month/calendar view
                        HijriCalendarView(
                            locale = locale,
                            displayedYearMonth = displayedYearMonth,
                            selectedDate = selectedDate,
                            onMonthYearClicked = {
                                mode =
                                    if (mode == PickerMode.Month) PickerMode.Year else PickerMode.Month
                            },
                            onMonthChange = { displayedYearMonth = it },
                            onDaySelected = { clicked ->
                                selectedDate = clicked
                                displayedYearMonth = Pair(
                                    clicked.year, 
                                    clicked.month 
                                )
                            }
                        )
                    }

                    PickerMode.Year -> {
                        // Year grid view
                        HijriYearPicker(
                            locale = locale,
                            selectedYear = displayedYearMonth.first,
                            onYearSelected = { year ->
                                // choose safe day for new year/month (if day > month length adjust)
                                val month = displayedYearMonth.second
                                val currentDay = selectedDate.day 
                                val maxDay = KmpHijriCalendar.of(year, month, 1)
                                    .lengthOfMonth() 
                                val safeDay = currentDay.coerceAtMost(maxDay)

                                selectedDate =
                                    KmpHijriCalendar.of(year, month, safeDay) 
                                displayedYearMonth = Pair(year, month)
                                // go back to month view
                                mode = PickerMode.Month
                            }
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        // Buttons aligned to end (Cancel / OK)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onDismissRequest) {
                Text(text = if (locale.language == "ar") "إلغاء" else "Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            TextButton(onClick = { onDateConfirmed(selectedDate) }) {
                Text(text = if (locale.language == "ar") "موافق" else "OK")
            }
        }
    }
}

/* ------------------------------ Header ------------------------------ */

@Composable
internal fun HijriDatePickerHeader( 
    locale: Locale,
    selectedDate: KmpHijriDate 
) {
    val isArabic = locale.language == "ar"
    val onSurface = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {
            Text(
                text = if (isArabic) "اختر التاريخ" else "Select date",
                color = onSurface,
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = formatSelectedDateHeader(selectedDate, locale),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/* --------------------------- Calendar (Month) ------------------------- */

@Composable
internal fun HijriCalendarView( 
    locale: Locale,
    displayedYearMonth: Pair<Int, Int>,
    selectedDate: KmpHijriDate, 
    onMonthYearClicked: () -> Unit,
    onMonthChange: (Pair<Int, Int>) -> Unit,
    onDaySelected: (KmpHijriDate) -> Unit 
) {
    val isArabic = locale.language == "ar"
    val onSurface = MaterialTheme.colorScheme.onSurface

    Column {
        // month navigator (left arrow, title, right arrow)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                modifier = Modifier.clickable { onMonthYearClicked() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                val displayedHijriMonthStart =
                    KmpHijriCalendar.of(
                        displayedYearMonth.first,
                        displayedYearMonth.second,
                        1
                    ) 
                Text(
                    text = titleMonthName(displayedHijriMonthStart, locale),
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Change year",
                    tint = onSurface
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                val (y, m) = displayedYearMonth
                val prev = if (m == 1) Pair(y - 1, 12) else Pair(y, m - 1)
                onMonthChange(prev)
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous month",
                    tint = onSurface
                )
            }

            IconButton(onClick = {
                val (y, m) = displayedYearMonth
                val next = if (m == 12) Pair(y + 1, 1) else Pair(y, m + 1)
                onMonthChange(next)
            }) {
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
            // TODO: Localize these day names from locale data instead of hardcoding
            val shortDays = if (isArabic) listOf("ح", "ن", "ث", "ر", "خ", "ج", "س")
            else listOf("Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri")
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
            year = displayedYearMonth.first,
            month = displayedYearMonth.second,
            locale = locale,
            selectedDate = selectedDate,
            onDayClick = onDaySelected
        )
    }
}

@Composable
internal fun HijriCalendarGrid( 
    year: Int,
    month: Int,
    locale: Locale,
    selectedDate: KmpHijriDate, 
    onDayClick: (KmpHijriDate) -> Unit 
) {
    // First day of the hijri month
    val firstOfMonth = KmpHijriCalendar.of(year, month, 1) 

    // We get ISO day of week (1=Mon..7=Sun) and adjust to Sat=0..Fri=6
    val dowOfFirst = firstOfMonth.dayOfWeek 
    val startIndex = (dowOfFirst + 1) % 7 // Sat=0, Sun=1, ... Fri=6

    val daysInMonth = firstOfMonth.lengthOfMonth() 
    val cells = mutableListOf<KmpHijriDate?>() 
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
                        locale = locale,
                        isSelected = date != null && areSameHijriDate(date, selectedDate),
                        isToday = date != null && areSameHijriDate(
                            date,
                            KmpHijriCalendar.now()
                        ), 
                        onClick = { d -> if (d != null) onDayClick(d) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
internal fun DayCell( 
    date: KmpHijriDate?, 
    locale: Locale,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: (KmpHijriDate?) -> Unit 
) {
    val primary = MaterialTheme.colorScheme.primary
    val onSurface = MaterialTheme.colorScheme.onSurface

    val size = 40.dp

    // ⬇️ Define modifier logic first
    val cellModifier = Modifier
        .width(size)
        .height(size)
        .clip(CircleShape)
        .background(
            when {
                date != null && isSelected -> primary
                else -> Color.Transparent
            }
        )

    val finalModifier = if (date != null && isToday && !isSelected) {
        cellModifier.border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
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
                text = formatNumber(dayNumber, locale),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = when {
                    isSelected -> MaterialTheme.colorScheme.onPrimary
                    isToday -> MaterialTheme.colorScheme.primary
                    else -> onSurface
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
    locale: Locale,
    selectedYear: Int,
    onYearSelected: (Int) -> Unit
) {
    val onSurface = MaterialTheme.colorScheme.onSurface
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary

    // Range: selectedYear ± 50
    val start = selectedYear - 50
    val end = selectedYear + 50
    val years = (start..end).toList()

    // Logic to auto-scroll to the selected year
    val selectedYearIndex = 50 // The selectedYear is always at index 50 in your list
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
                            if (isSelected) primary
                            else Color.Transparent
                        )
                        .clickable { onYearSelected(year) }
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatNumber(year, locale),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isSelected) onPrimary else onSurface,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    )
}

/* --------------------------- Utilities ------------------------------ */

/** Simple equality check for HijrahDate day/month/year */
internal fun areSameHijriDate(a: KmpHijriDate, b: KmpHijriDate): Boolean = 
    a.year == b.year
            && a.month == b.month
            && a.day == b.day

/** Return a month title like "Ramadan 1446" or Arabic equivalent. */
internal fun titleMonthName(hijriStart: KmpHijriDate, locale: Locale): String { 
    return formatHijriDate(hijriStart, "MMMM yyyy", locale)
}

/** Formats a date for the header, e.g., "Jum. I 12" or "١٢ جمادى الأولى" */
internal fun formatSelectedDateHeader(date: KmpHijriDate, locale: Locale): String { 
    val pattern = if (locale.language == "ar") "d MMMM" else "E, MMM d"
    return formatHijriDate(date, pattern, locale)
}

