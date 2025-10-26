package io.github.helmy2.internal

// We use 1/1/1 AH as our absolute reference point (Page 0).
internal val REFERENCE_YEAR_MONTH = Pair(1, 1)

/**
 * Calculates the total number of months between two (year, month) pairs.
 * (This function is updated for the new reference)
 */
internal fun monthsDifference(start: Pair<Int, Int>, end: Pair<Int, Int>): Int {
    return (end.first - start.first) * 12 + (end.second - start.second)
}

/**
 * Adds a number of months to a given (year, month) pair.
 * (This function is updated for the new reference)
 */
internal fun plusMonths(start: Pair<Int, Int>, months: Int): Pair<Int, Int> {
    // We calculate months from 1/1 AH, so we adjust by -1
    val totalMonths = (start.first - 1) * 12 + (start.second - 1) + months
    val newYear = totalMonths / 12 + 1 // Convert back to 1-based year
    val newMonth = totalMonths % 12 + 1 // Convert back to 1-based month
    return Pair(newYear, newMonth)
}