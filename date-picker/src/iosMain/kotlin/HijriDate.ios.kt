package io.github.helmy2

import androidx.compose.ui.text.intl.Locale
import io.github.helmy2.internal.convertToArabicIndicDigits
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarIdentifierIslamicUmmAlQura
import platform.Foundation.NSDate
import platform.Foundation.NSDateComponents
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.Foundation.languageCode
import platform.darwin.NSInteger

internal fun Locale.toNSLocale(): NSLocale {
    return NSLocale(localeIdentifier = this.toLanguageTag())
}

/**
 * The actual iOS implementation of [HijriDate].
 * It wraps an Apple [NSDate] and uses an [NSCalendar] to interpret it.
 */
actual class HijriDate(
    internal val nsDate: NSDate,
    internal val calendar: NSCalendar
) {
    private fun getComponent(unit: ULong): NSInteger {
        return calendar.component(unit, fromDate = nsDate)
    }

    actual val year: Int
        get() = getComponent(platform.Foundation.NSCalendarUnitYear).toInt()
    actual val month: Int
        get() = getComponent(platform.Foundation.NSCalendarUnitMonth).toInt()
    actual val day: Int
        get() = getComponent(platform.Foundation.NSCalendarUnitDay).toInt()

    actual val dayOfWeek: Int
        get() {
            // Apple's weekday: 1 = Sunday, 2 = Monday, ..., 7 = Saturday
            // Our 'expect' contract (ISO-8601): 1 = Monday, ..., 7 = Sunday
            val appleWeekday = getComponent(platform.Foundation.NSCalendarUnitWeekday).toInt()
            return if (appleWeekday == 1) 7 else appleWeekday - 1
        }

    @OptIn(ExperimentalForeignApi::class)
    actual fun lengthOfMonth(): Int {
        // Get the year and month of the current date
        val components = calendar.components(
            platform.Foundation.NSCalendarUnitYear or platform.Foundation.NSCalendarUnitMonth,
            fromDate = nsDate
        )

        // Create components for the 1st of the *next* month
        val nextMonthComponents = NSDateComponents()
        if (components.month == 12L) {
            // It's the last month, go to month 1 of next year
            nextMonthComponents.setMonth(1)
            nextMonthComponents.setYear(components.year + 1)
        } else {
            // It's a regular month, just increment
            nextMonthComponents.setMonth(components.month + 1)
            nextMonthComponents.setYear(components.year)
        }
        nextMonthComponents.setDay(1) // 1st day

        // Get the NSDate for the 1st of next month
        val firstOfNextMonth = calendar.dateFromComponents(nextMonthComponents)!!

        // Create a component to subtract one day
        val dayComponent = NSDateComponents()
        dayComponent.setDay(-1)

        // Get the NSDate for the last day of *this* month
        val lastOfThisMonth =
            calendar.dateByAddingComponents(dayComponent, toDate = firstOfNextMonth, options = 0u)!!

        // Get the 'day' component of that last day. That's the length.
        return calendar.component(platform.Foundation.NSCalendarUnitDay, fromDate = lastOfThisMonth)
            .toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is HijriDate) return false
        return year == other.year && month == other.month && day == other.day
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month
        result = 31 * result + day
        return result
    }

    override fun toString(): String = "HijriDate(year=$year, month=$month, day=$day)"
}

/**
 * The actual iOS implementation of the calendar factory.
 */
actual object HijriCalendar {
    // Create a single, reusable Hijri calendar instance
    private val hijriCalendar =
        NSCalendar(calendarIdentifier = NSCalendarIdentifierIslamicUmmAlQura)

    // 👇 --- ADD THIS BLOCK ---
    /**
     * Force the calendar to use a known start of the week.
     * 1 = Sunday. This makes its behavior predictable and matches our
     * dayOfWeek conversion logic.
     */
    init {
        hijriCalendar.firstWeekday = 1u
    }
    // -----------------------

    actual fun now(): HijriDate {
        return HijriDate(NSDate(), hijriCalendar)
    }

    actual fun of(year: Int, month: Int, day: Int): HijriDate {
        // Validate month
        if (month !in 1..12) {
            throw IllegalArgumentException("Invalid Hijri month: $month")
        }

        // Validate day - we allow 1..30 but later you can improve with exact month length validation
        if (day !in 1..30) {
            throw IllegalArgumentException("Invalid Hijri day: $day")
        }

        val components = NSDateComponents()
        components.setYear(year.toLong())
        components.setMonth(month.toLong())
        components.setDay(day.toLong())
        val date = hijriCalendar.dateFromComponents(components)
            ?: throw IllegalArgumentException("Invalid Hijri date: year=$year, month=$month, day=$day")
        return HijriDate(date, hijriCalendar)
    }
}

/**
 * The actual iOS implementation for formatting a date.
 */
actual fun formatHijriDate(
    date: HijriDate,
    pattern: String,
    locale: Locale
): String {
    val dateFormatter = NSDateFormatter()
    dateFormatter.calendar = date.calendar
    dateFormatter.locale = locale.toNSLocale()
    // Note: iOS uses different pattern letters than Java.
    // We map the Java/ICU pattern to an Apple pattern.
    val applePattern = pattern
        .replace("yyyy", "y") // Year
        .replace("MMMM", "LLLL") // Full month name
        .replace("MMM", "LLL") // Short month name
        .replace("d", "d") // Day
        .replace("E", "ccc") // Short day of week

    dateFormatter.dateFormat = applePattern
    val formattedDate = dateFormatter.stringFromDate(date.nsDate)
    return if (dateFormatter.locale.languageCode == "ar") formattedDate.convertToArabicIndicDigits()
    else formattedDate
}