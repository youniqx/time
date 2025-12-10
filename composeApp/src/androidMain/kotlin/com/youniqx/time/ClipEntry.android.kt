package com.youniqx.time

import android.content.ClipData
import androidx.compose.ui.platform.toClipEntry

actual fun clipEntryOf(string: String) = ClipData.newPlainText(null, string).toClipEntry()
