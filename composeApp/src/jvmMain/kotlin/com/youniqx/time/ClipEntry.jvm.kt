package com.youniqx.time

import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

actual fun clipEntryOf(string: String) = ClipEntry(StringSelection(string))