package io.github.helmy2

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

actual fun getHijriDatePickerFormatter(): HijriDatePickerFormatter {
    return HijriDatePickerFormatter()
}

@Composable
internal actual fun rememberDefaultDatePickerStrings(): HijriDatePickerStrings {
    return HijriDatePickerStrings(
        selectDateTitle = stringResource(R.string.hijri_date_picker_title),
        selectDateHeadlineDefault = stringResource(R.string.hijri_date_picker_headline_default),
        nextMonthContentDescription = stringResource(R.string.hijri_date_picker_next_month),
        previousMonthContentDescription = stringResource(R.string.hijri_date_picker_previous_month),
        changeYearContentDescription = stringResource(R.string.hijri_date_picker_change_year)
    )
}