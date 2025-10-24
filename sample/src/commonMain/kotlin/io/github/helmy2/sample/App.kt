package io.github.helmy2.sample

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.helmy2.HijriDatePicker
import io.github.helmy2.rememberHijriDatePickerState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    var show by remember { mutableStateOf(true) }
    val state = rememberHijriDatePickerState()
    val isSystemInDarkTheme = isSystemInDarkTheme()

    MaterialTheme(colorScheme = if (isSystemInDarkTheme) darkColorScheme() else lightColorScheme()) {
        Column(
            Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { show = true }) { Text("Open picker") }
            Spacer(Modifier.height(12.dp))
        }
        if (show) {
            DatePickerDialog(
                onDismissRequest = {
                    show = false
                },
                confirmButton = {}
            ) {
                HijriDatePicker(state)
            }
        }
    }
}