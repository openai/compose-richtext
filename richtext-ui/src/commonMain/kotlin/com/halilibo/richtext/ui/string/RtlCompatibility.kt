package com.halilibo.richtext.ui.string

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import kotlin.text.CharDirectionality

public fun Modifier.applyRtlCompatibility(renderOptions: RichTextRenderOptions): Modifier =
  if (renderOptions.enableRtlCompatibility) {
    fillMaxWidth()
  } else {
    this
  }

internal fun firstStrongTextDirection(text: CharSequence): TextDirection? {
  for (char in text) {
    when (char.directionality) {
      CharDirectionality.LEFT_TO_RIGHT,
      CharDirectionality.LEFT_TO_RIGHT_EMBEDDING,
      CharDirectionality.LEFT_TO_RIGHT_OVERRIDE -> return TextDirection.Ltr

      CharDirectionality.RIGHT_TO_LEFT,
      CharDirectionality.RIGHT_TO_LEFT_ARABIC,
      CharDirectionality.RIGHT_TO_LEFT_EMBEDDING,
      CharDirectionality.RIGHT_TO_LEFT_OVERRIDE -> return TextDirection.Rtl

      else -> Unit
    }
  }

  return null
}

internal fun applyRtlCompatibleTextDirection(
  textStyle: TextStyle,
  direction: TextDirection?,
): TextStyle {
  if (direction == null) {
    return textStyle
  }

  return textStyle.copy(
    textDirection = direction,
    textAlign = when (textStyle.textAlign) {
      TextAlign.Unspecified,
      TextAlign.Start,
      TextAlign.End -> when (direction) {
        TextDirection.Ltr -> TextAlign.Left
        TextDirection.Rtl -> TextAlign.Right
        else -> textStyle.textAlign
      }

      else -> textStyle.textAlign
    },
  )
}
