package com.youniqx.time

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

fun String.toColorInt(): Int {
    if (this[0] == '#') {
        var color = substring(1).toLong(16)
        if (length == 7) {
            color = color or 0x00000000ff000000L
        } else if (length != 9) {
            throw IllegalArgumentException("Unknown color")
        }
        return color.toInt()
    }
    throw IllegalArgumentException("Unknown color")
}

/**
 * Returns either Black or White as a high-contrast text color
 * for this background color.
 *
 * @param threshold The luminance value to check against.
 * WCAG suggests a threshold of 0.179 for true sRGB.
 * You can adjust this value to fine-tune the results.
 * @return `Color.Black` if the background is light, or `Color.White` if it's dark.
 */
fun Color.contrastingTextColor(threshold: Double = 0.25): Color =
    if (luminance() > threshold) Color.Black else Color.White
