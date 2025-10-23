package io.github.helmy2

import androidx.compose.ui.text.intl.Locale

actual class KmpHijriDate {
    actual val year: Int
        get() = TODO("Not yet implemented")
    actual val month: Int
        get() = TODO("Not yet implemented")
    actual val day: Int
        get() = TODO("Not yet implemented")
    actual val dayOfWeek: Int
        get() = TODO("Not yet implemented")

    actual fun lengthOfMonth(): Int {
        TODO("Not yet implemented")
    }

    actual companion object {
        actual fun now(): KmpHijriDate {
            TODO("Not yet implemented")
        }
    }
}

actual object KmpHijriCalendar {
    actual fun now(): KmpHijriDate {
        TODO("Not yet implemented")
    }

    actual fun of(
        year: Int,
        month: Int,
        day: Int
    ): KmpHijriDate {
        TODO("Not yet implemented")
    }
}

actual fun formatHijriDate(
    date: KmpHijriDate,
    pattern: String,
    locale: Locale
): String {
    TODO("Not yet implemented")
}

actual fun formatNumber(
    number: Int,
    locale: Locale
): String {
    TODO("Not yet implemented")
}