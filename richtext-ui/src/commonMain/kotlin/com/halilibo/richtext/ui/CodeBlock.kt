package com.halilibo.richtext.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Defines how [CodeBlock]s are rendered.
 *
 * @param textStyle The [TextStyle] to use for the block.
 * @param background The [Color] of a code block, drawn behind the text.
 * @param padding The amount of space between the edge of the text and the edge of the background.
 */
@Immutable
public data class CodeBlockStyle(
  val textStyle: TextStyle? = null,
  // TODO Make background just a modifier instead?
  val background: Color? = null,
  val padding: TextUnit? = null
) {
  public companion object {
    public val Default: CodeBlockStyle = CodeBlockStyle()
  }
}

private val DefaultCodeBlockTextStyle = TextStyle(
  fontFamily = FontFamily.Monospace
)
internal val DefaultCodeBlockBackground: Color = Color.LightGray.copy(alpha = .5f)
private val DefaultCodeBlockPadding: TextUnit = 16.sp

internal fun CodeBlockStyle.resolveDefaults() = CodeBlockStyle(
  textStyle = textStyle ?: DefaultCodeBlockTextStyle,
  background = background ?: DefaultCodeBlockBackground,
  padding = padding ?: DefaultCodeBlockPadding
)

/**
 * A specially-formatted block of text that typically uses a monospace font with a tinted
 * background.
 */
@Composable public fun RichTextScope.CodeBlock(text: String) {
  CodeBlock {
    Text(text)
  }
}

/**
 * A specially-formatted block of text that typically uses a monospace font with a tinted
 * background.
 */
@Composable public fun RichTextScope.CodeBlock(children: @Composable RichTextScope.() -> Unit) {
  val codeBlockStyle = currentRichTextStyle.resolveDefaults().codeBlockStyle!!
  val textStyle = currentTextStyle.merge(codeBlockStyle.textStyle)
  val blockPadding = with(LocalDensity.current) {
    codeBlockStyle.padding!!.toDp()
  }

  Box(
    modifier = Modifier
      .background(color = codeBlockStyle.background!!)
      .padding(blockPadding)
  ) {
    ProvideTextStyle(textStyle) {
      children()
    }
  }
}