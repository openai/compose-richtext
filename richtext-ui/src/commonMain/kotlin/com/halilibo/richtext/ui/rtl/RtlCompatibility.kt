package com.halilibo.richtext.ui.rtl

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlin.text.CharDirectionality

internal fun shouldFillWidthForRtlCompatibility(
  enableRtlCompatibility: Boolean,
  contentDirection: TextDirection?,
  layoutDirection: LayoutDirection,
): Boolean = enableRtlCompatibility && contentDirection.isOppositeOf(layoutDirection)

@Composable
public fun Modifier.fillMaxWidthForRtlCompatibility(
  enableRtlCompatibility: Boolean,
  contentDirection: TextDirection?,
): Modifier = if (
  shouldFillWidthForRtlCompatibility(
    enableRtlCompatibility = enableRtlCompatibility,
    contentDirection = contentDirection,
    layoutDirection = LocalLayoutDirection.current,
  )
) {
  fillMaxWidth()
} else {
  this
}

internal fun resolveRtlCompatibleLayoutDirection(
  contentDirection: TextDirection?,
  systemDirection: LayoutDirection,
): LayoutDirection = when (contentDirection) {
  TextDirection.Ltr -> LayoutDirection.Ltr
  TextDirection.Rtl -> LayoutDirection.Rtl
  else -> systemDirection
}

internal fun TextAlign?.toCompatibilityDirection(): TextDirection? = when (this) {
  TextAlign.Left -> TextDirection.Ltr
  TextAlign.Right -> TextDirection.Rtl
  else -> null
}

public fun TextDirection?.toCompatibilityTextAlign(): TextAlign? = when (this) {
  TextDirection.Ltr -> TextAlign.Left
  TextDirection.Rtl -> TextAlign.Right
  else -> null
}

public fun TextDirection?.toCompatibilityTextDirection(): TextDirection? = when (this) {
  TextDirection.Ltr -> TextDirection.ContentOrLtr
  TextDirection.Rtl -> TextDirection.ContentOrRtl
  else -> null
}

/**
 * Scans text for the first strong bidi character and returns its direction.
 */
public fun CharSequence.firstStrongTextDirection(
  stopAtLineBreak: Boolean = false,
  ignoreHtmlTags: Boolean = false,
  onLineBreak: () -> Unit = {},
): TextDirection? {
  var insideHtmlTag = false
  for (char in this) {
    if (stopAtLineBreak && (char == '\n' || char == '\r')) {
      onLineBreak()
      return null
    }

    when {
      ignoreHtmlTags && char == '<' -> insideHtmlTag = true
      ignoreHtmlTags && insideHtmlTag && char == '>' -> insideHtmlTag = false
      ignoreHtmlTags && insideHtmlTag -> Unit
      else -> when (char.directionality) {
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
  }

  return null
}

private fun TextDirection?.isOppositeOf(layoutDirection: LayoutDirection): Boolean = when (this) {
  TextDirection.Ltr -> layoutDirection == LayoutDirection.Rtl
  TextDirection.Rtl -> layoutDirection == LayoutDirection.Ltr
  else -> false
}
