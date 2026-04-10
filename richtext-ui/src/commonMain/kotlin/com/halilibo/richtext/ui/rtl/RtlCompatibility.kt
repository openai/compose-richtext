package com.halilibo.richtext.ui.rtl

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import com.halilibo.richtext.ui.string.RichTextRenderOptions

internal fun shouldFillWidthForRtlCompatibility(
  renderOptions: RichTextRenderOptions,
  contentDirection: TextDirection?,
  layoutDirection: LayoutDirection,
): Boolean = renderOptions.enableRtlCompatibility && contentDirection.isOppositeOf(layoutDirection)

@Composable
internal fun Modifier.fillMaxWidthForRtlCompatibility(
  renderOptions: RichTextRenderOptions,
  contentDirection: TextDirection?,
): Modifier = if (
  shouldFillWidthForRtlCompatibility(
    renderOptions = renderOptions,
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

private fun TextDirection?.isOppositeOf(layoutDirection: LayoutDirection): Boolean = when (this) {
  TextDirection.Ltr -> layoutDirection == LayoutDirection.Rtl
  TextDirection.Rtl -> layoutDirection == LayoutDirection.Ltr
  else -> false
}
