package com.halilibo.richtext.ui.string

import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * Defines how specific links should be decorated based on their destination.
 */
public data class LinkDecoration(
  val matcher: (destination: String, text: String) -> Boolean,
  val underlineStyle: UnderlineStyle = UnderlineStyle.Solid,
  val linkStyleOverride: ((TextLinkStyles?) -> TextLinkStyles)? = null,
  val inlineContent: LinkInlineContent? = null,
)

/**
 * Collection of decorations to apply when rendering a [RichTextString].
 */
public data class RichTextDecorations(
  val linkDecorations: List<LinkDecoration> = emptyList(),
)

public data class LinkInlineContent(
  val leading: InlineIconSpec? = null,
  val trailing: InlineIconSpec? = null,
  val spacing: Dp = 4.dp,
  val includeInHitTarget: Boolean = true,
)

public sealed class InlineIconSpec(
  public val size: DpSize,
  public val placeholderVerticalAlign: PlaceholderVerticalAlign,
) {
  public data class Painter(
    val painter: androidx.compose.ui.graphics.painter.Painter,
    val tint: Color? = null,
    val contentDescription: String? = null,
    val iconSize: DpSize = DefaultSize,
    val iconPlaceholderVerticalAlign: PlaceholderVerticalAlign = DefaultPlaceholderVerticalAlign,
  ) : InlineIconSpec(
    size = iconSize,
    placeholderVerticalAlign = iconPlaceholderVerticalAlign,
  )

  public data class Composable(
    val content: LinkComposableContent,
    val iconSize: DpSize = DefaultSize,
    val iconPlaceholderVerticalAlign: PlaceholderVerticalAlign = DefaultPlaceholderVerticalAlign,
  ) : InlineIconSpec(
    size = iconSize,
    placeholderVerticalAlign = iconPlaceholderVerticalAlign,
  )

  public companion object {
    public val DefaultSize: DpSize = DpSize(16.dp, 16.dp)
    public val DefaultPlaceholderVerticalAlign: PlaceholderVerticalAlign =
      PlaceholderVerticalAlign.Center
  }
}

public data class LinkContext(
  val destination: String,
  val text: String,
)

public fun interface LinkComposableContent {
  @Composable public fun Render(context: LinkContext)
}

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
