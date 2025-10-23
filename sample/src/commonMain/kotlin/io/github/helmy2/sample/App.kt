package io.github.helmy2.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import io.github.helmy2.HijriDatePicker
import io.github.helmy2.KmpHijriDate
import org.jetbrains.compose.ui.tooling.preview.Preview


@Preview(showBackground = true)
@Composable
fun PreviewHijriPickerLight() {
    var show by remember { mutableStateOf(true) }
    var picked by remember { mutableStateOf<KmpHijriDate?>(null) }

    MaterialTheme {
        Column(
            Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { show = true }) { Text("Open picker (EN)") }
            Spacer(Modifier.height(12.dp))
        }
        if (show) {
            HijriDatePicker(
                locale = Locale.current,
                initialDate = picked ?: KmpHijriDate.now(),
                onDateSelected = {
                    picked = it
                    show = false
                },
                onDismissRequest = { show = false }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHijriPickerDark() {
    var show by remember { mutableStateOf(true) }
    var picked by remember { mutableStateOf<KmpHijriDate?>(null) }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Column(
            Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { show = true }) { Text("Open picker (AR)") }
            Spacer(Modifier.height(12.dp))
        }
        if (show) {
            HijriDatePicker(
                locale = Locale("ar"),
                initialDate = picked ?: KmpHijriDate.now(),
                onDateSelected = {
                    picked = it
                    show = false
                },
                onDismissRequest = { show = false }
            )
        }
    }
}