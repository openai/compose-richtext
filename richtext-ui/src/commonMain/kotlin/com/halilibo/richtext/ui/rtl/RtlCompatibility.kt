package com.halilibo.richtext.ui.rtl

import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import com.halilibo.richtext.ui.string.RichTextRenderOptions

internal fun shouldFillWidthForRtlCompatibility(
  renderOptions: RichTextRenderOptions,
  contentDirection: TextDirection?,
): Boolean = renderOptions.enableRtlCompatibility && contentDirection != null

internal fun resolveRtlCompatibleLayoutDirection(
  contentDirection: TextDirection?,
  systemDirection: LayoutDirection,
): LayoutDirection = when (contentDirection) {
  TextDirection.Ltr -> LayoutDirection.Ltr
  TextDirection.Rtl -> LayoutDirection.Rtl
  else -> systemDirection
}
