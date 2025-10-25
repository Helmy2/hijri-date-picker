package io.github.helmy2.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.helmy2.CALENDAR_VIEW_HEIGHT
import io.github.helmy2.HijriDatePickerColors
import io.github.helmy2.HijriDatePickerState
import io.github.helmy2.formatNumber

@Composable
internal fun HijriYearPicker(
    state: HijriDatePickerState,
    colors: HijriDatePickerColors
) {
    val selectedYear = state.displayedYearMonth.first

    val years = state.yearRange.toList()

    val selectedYearIndex = remember(years, selectedYear) {
        years.indexOf(selectedYear).coerceAtLeast(0)
    }

    val gridState = rememberLazyGridState(
        initialFirstVisibleItemIndex = 0
    )

    LaunchedEffect(selectedYearIndex) {
        val itemsPerRow = 3
        val estimatedVisibleRows = 6

        // Calculate the row of the selected item
        val selectedRow = selectedYearIndex / itemsPerRow

        // Calculate the row to scroll to, attempting to put it
        // near the middle of the visible rows.
        val targetRow = (selectedRow - (estimatedVisibleRows / 2) + 1).coerceAtLeast(0)

        // Convert the target row back to the first item index of that row
        val targetIndex = (targetRow * itemsPerRow)
            .coerceIn(0, (years.size - 1).coerceAtLeast(0))

        // Animate the scroll
        gridState.animateScrollToItem(index = targetIndex)
    }

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