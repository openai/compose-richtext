package com.halilibo.richtext.ui.string

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextLinkStyles

/**
 * Defines how specific links should be decorated based on their destination.
 */
public data class LinkDecoration(
  val matcher: (destination: String, text: String) -> Boolean,
  val underlineStyle: UnderlineStyle = UnderlineStyle.Solid,
  val linkStyleOverride: ((TextLinkStyles?) -> TextLinkStyles)? = null,
)

/**
 * Collection of decorations to apply when rendering a [RichTextString].
 */
public data class RichTextDecorations(
  val linkDecorations: List<LinkDecoration> = emptyList(),
)

/**
 * The underline style to use for a matched link.
 */
public sealed class UnderlineStyle {
  public object Solid : UnderlineStyle()

  public data class Dotted(
    val strokeWidth: Dp = 1.dp,
    val gap: Dp = 2.dp,
    val offset: Dp = 0.dp,
  ) : UnderlineStyle()

  public data class Dashed(
    val dash: Dp = 6.dp,
    val gap: Dp = 4.dp,
    val strokeWidth: Dp = 1.dp,
    val offset: Dp = 1.dp,
  ) : UnderlineStyle()
}
