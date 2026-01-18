package com.youniqx.time.presentation.workitems

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class AddedTextVisualTransformation(private val addedText: AnnotatedString) : VisualTransformation {
    override fun filter(text: AnnotatedString) = TransformedText(
        text = text + addedText,
        offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int) = offset
            override fun transformedToOriginal(offset: Int) = offset.coerceAtMost(text.length)
        }
    )
}