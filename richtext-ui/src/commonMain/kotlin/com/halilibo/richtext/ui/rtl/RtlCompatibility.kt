package com.halilibo.richtext.ui.rtl

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection

internal fun shouldFillWidthForRtlCompatibility(
  enableRtlCompatibility: Boolean,
  contentDirection: TextDirection?,
  layoutDirection: LayoutDirection,
): Boolean = enableRtlCompatibility && contentDirection.isOppositeOf(layoutDirection)

@Composable
internal fun Modifier.fillMaxWidthForRtlCompatibility(
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

private fun TextDirection?.isOppositeOf(layoutDirection: LayoutDirection): Boolean = when (this) {
  TextDirection.Ltr -> layoutDirection == LayoutDirection.Rtl
  TextDirection.Rtl -> layoutDirection == LayoutDirection.Ltr
  else -> false
}
