package io.github.helmy2.io.github.helmy2.internal

import io.github.helmy2.internal.monthsDifference
import io.github.helmy2.internal.plusMonths
import kotlin.test.Test
import kotlin.test.assertEquals

class PagerUtilsTest {
    @Test
    fun givenSameYearAndMonth_whenMonthsDifference_thenZero() {
        val start = Pair(1447, 9)
        val end = Pair(1447, 9)
        val diff = monthsDifference(start, end)
        assertEquals(0, diff)
    }

    @Test
    fun givenSameYearAndDifferentMonth_whenMonthsDifference_thenPositive() {
        val start = Pair(1447, 9)
        val end = Pair(1447, 12)
        val diff = monthsDifference(start, end)
        assertEquals(3, diff)
    }

    @Test
    fun givenDifferentYearAndMonth_whenMonthsDifference_thenPositive() {
        val start = Pair(1447, 9)
        val end = Pair(1448, 2)
        val diff = monthsDifference(start, end)
        assertEquals(5, diff)
    }

    @Test
    fun givenEndBeforeStart_whenMonthsDifference_thenNegative() {
        val start = Pair(1448, 3)
        val end = Pair(1447, 12)
        val diff = monthsDifference(start, end)
        assertEquals(-3, diff)
    }

    @Test
    fun givenStartMonthPlusZero_whenPlusMonths_thenSameMonth() {
        val start = Pair(1447, 9)
        val result = plusMonths(start, 0)
        assertEquals(start, result)
    }

    @Test
    fun givenStartMonthPlusWithinYear_whenPlusMonths_thenCorrectMonth() {
        val start = Pair(1447, 9)
        val result = plusMonths(start, 3)
        assertEquals(Pair(1447, 12), result)
    }

    @Test
    fun givenMonthAdditionCrossYear_whenPlusMonths_thenYearIncrements() {
        val start = Pair(1447, 9)
        val result = plusMonths(start, 5)
        assertEquals(Pair(1448, 2), result)
    }

    @Test
    fun givenNegativeMonths_whenPlusMonths_thenDecrementsCorrectly() {
        val start = Pair(1448, 2)
        val result = plusMonths(start, -3)
        assertEquals(Pair(1447, 11), result)
    }
}