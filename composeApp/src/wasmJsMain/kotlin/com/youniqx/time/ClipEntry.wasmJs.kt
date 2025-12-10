package com.youniqx.time

import androidx.compose.ui.platform.ClipEntry

actual fun clipEntryOf(string: String) = ClipEntry.withPlainText(string)