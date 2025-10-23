# Compose Hijri Date Picker

[![Maven Central](https://img.shields.io/maven-central/v/io.github.helmy2/hijri-date-picker/0.0.1)](https://central.sonatype.com/artifact/io.github.helmy2/hijri-date-picker)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A simple, clean, and Material 3-styled Hijri (Islamic) calendar and date picker for Jetpack Compose.

This library is built with **Kotlin Multiplatform (KMP)**, but this initial version **targets Android only**. Support for iOS, Desktop, and Web is planned for future releases.

[Image of the Hijri Date Picker component in light and dark mode]

## ⚠️ Experimental Release: v0.0.1

This is an early experimental release. It is intended for testing and feedback. The API is not yet stable and may change in future versions.

---

## ✨ Features

* **Material 3 Design:** A clean, modern UI that fits perfectly with M3 themes.
* **Lightweight & Simple API:** Just one composable to call: `HijriDatePicker(...)`.
* **Full Localization:**
    * Month and day names are automatically localized.
    * Numbers are converted to Arabic-Indic digits (`٠١٢٣`) when using an Arabic locale.
    * Layout automatically switches to **RTL** for Arabic.

---

## 🚀 Installation

This library is published to **Maven Central**.

As this is an **Android-only** release, you can add it directly to your app's `build.gradle.kts` file:

```kotlin
// In your app/build.gradle.kts
dependencies {
    implementation("io.github.helmy2:hijri-date-picker:0.0.1")
}
```

### 💻 Usage
Call the HijriDatePicker composable from your code. You can show it in a Dialog, AlertDialog, or ModalBottomSheet.

``` kotlin

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.intl.Locale
import io.github.helmy2.HijriDatePicker 

// ...

var showPicker by remember { mutableStateOf(false) }

if (showPicker) {
    // 2. CALL THE COMPOSABLE
    HijriDatePicker(
        // Use a specific locale or Locale.current()
        locale = Locale("ar"), 
        onDateSelected = { hijriDate ->
            // 'hijriDate' is your KmpHijriDate object
            // (e.g., "${hijriDate.day}/${hijriDate.month}/${hijriDate.year}")
            showPicker = false
        },
        onDismissRequest = {
            showPicker = false
        }
    )
}
```

## 🗺️ Roadmap 
This library is structured for KMP, with platform support planned as follows:

- [x] ✅ Android

- [ ] ⏳ Desktop (JVM) (Coming soon)

- [ ] ⏳ iOS (Coming in a future release)

- [ ] ⏳ Web (JS) (Coming in a future release)

## 📄 License
This library is licensed under the Apache 2.0 License.