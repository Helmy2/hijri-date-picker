package io.github.helmy2.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import hijri_date_picker.sample.generated.resources.Res
import hijri_date_picker.sample.generated.resources.app_name
import hijri_date_picker.sample.generated.resources.dark_theme
import hijri_date_picker.sample.generated.resources.hijri_picker_cancel
import hijri_date_picker.sample.generated.resources.hijri_picker_ok
import hijri_date_picker.sample.generated.resources.language
import hijri_date_picker.sample.generated.resources.no_date_selected
import hijri_date_picker.sample.generated.resources.open_picker
import hijri_date_picker.sample.generated.resources.switch_language
import io.github.helmy2.HijriDate
import io.github.helmy2.HijriDatePicker
import io.github.helmy2.HijriDatePickerDefaults
import io.github.helmy2.rememberHijriDatePickerState
import org.jetbrains.compose.resources.stringResource

@Composable
fun App() {
    var customAppLocale by remember {
        mutableStateOf<String?>("en")
    }
    val layoutDirection = if (customAppLocale == "ar")
        LayoutDirection.Rtl else LayoutDirection.Ltr


    var isDarkTheme by remember { mutableStateOf(false) }
    val colors = if (isDarkTheme) darkColorScheme() else lightColorScheme()

    CompositionLocalProvider(
        LocalAppLocale provides customAppLocale,
        LocalLayoutDirection provides layoutDirection,
    ) {
        key(customAppLocale) {
            MaterialTheme(colorScheme = colors) {
                MainScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = it },
                    onLocaleToggle = {
                        customAppLocale = if (customAppLocale == "ar") "en" else "ar"
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onLocaleToggle: () -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<HijriDate?>(null) }

    val datePickerState = rememberHijriDatePickerState(
        initialDate = selectedDate,
    )

    val dateFormatter = HijriDatePickerDefaults.dateFormatter()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(Res.string.app_name)) })
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.dark_theme))
                    Spacer(Modifier.weight(1f))
                    Switch(checked = isDarkTheme, onCheckedChange = onThemeToggle)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(Res.string.language))
                    Spacer(Modifier.weight(1f))
                    Button(onClick = onLocaleToggle) {
                        Text(stringResource(Res.string.switch_language))
                    }
                }

                HorizontalDivider()

                Button(onClick = { showPicker = true }) {
                    Text(stringResource(Res.string.open_picker))
                }

                val selectedDateText = selectedDate?.let {
                    dateFormatter.formatHeadlineDate(it)
                } ?: stringResource(Res.string.no_date_selected)

                Text(
                    text = selectedDateText,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    )

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDate = datePickerState.selectedDate
                        showPicker = false
                    }
                ) {
                    Text(stringResource(Res.string.hijri_picker_ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text(stringResource(Res.string.hijri_picker_cancel))
                }
            }
        ) {
            HijriDatePicker(state = datePickerState)
        }
    }
}


expect object LocalAppLocale {
    val current: String @Composable get

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}