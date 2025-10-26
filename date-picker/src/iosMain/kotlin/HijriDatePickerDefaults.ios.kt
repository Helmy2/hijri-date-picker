package io.github.helmy2

import androidx.compose.runtime.Composable
import io.github.helmy2.date_picker.generated.resources.Res
import io.github.helmy2.date_picker.generated.resources.hijri_date_picker_change_year
import io.github.helmy2.date_picker.generated.resources.hijri_date_picker_headline_default
import io.github.helmy2.date_picker.generated.resources.hijri_date_picker_next_month
import io.github.helmy2.date_picker.generated.resources.hijri_date_picker_previous_month
import io.github.helmy2.date_picker.generated.resources.hijri_date_picker_title
import org.jetbrains.compose.resources.stringResource

actual fun getHijriDatePickerFormatter(): HijriDatePickerFormatter {
    return HijriDatePickerFormatter()
}

@Composable
internal actual fun rememberDefaultDatePickerStrings(): HijriDatePickerStrings {
    return HijriDatePickerStrings(
        selectDateTitle = stringResource(Res.string.hijri_date_picker_title),
        selectDateHeadlineDefault = stringResource(Res.string.hijri_date_picker_headline_default),
        nextMonthContentDescription = stringResource(Res.string.hijri_date_picker_next_month),
        previousMonthContentDescription = stringResource(Res.string.hijri_date_picker_previous_month),
        changeYearContentDescription = stringResource(Res.string.hijri_date_picker_change_year)
    )
}
