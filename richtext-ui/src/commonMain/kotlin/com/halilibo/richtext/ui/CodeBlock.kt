package com.halilibo.richtext.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.ui.string.MarkdownAnimationState
import com.halilibo.richtext.ui.string.RichTextRenderOptions
import com.halilibo.richtext.ui.string.applyRtlCompatibilityFillWidth

/**
 * Defines how [CodeBlock]s are rendered.
 *
 * @param textStyle The [TextStyle] to use for the block.
 * @param modifier The [Modifier] to use for the block.
 * @param padding The amount of space between the edge of the text and the edge of the background.
 * @param wordWrap Whether a code block breaks the lines or scrolls horizontally.
 */
@Immutable
public data class CodeBlockStyle(
  val textStyle: TextStyle? = null,
  val modifier: Modifier? = null,
  val padding: TextUnit? = null,
  val wordWrap: Boolean? = null
) {
  public companion object {
    public val Default: CodeBlockStyle = CodeBlockStyle()
  }
}

private val DefaultCodeBlockTextStyle = TextStyle(
  fontFamily = FontFamily.Monospace
)
internal val DefaultCodeBlockBackgroundColor: Color = Color.LightGray.copy(alpha = .5f)
private val DefaultCodeBlockModifier: Modifier =
  Modifier.background(color = DefaultCodeBlockBackgroundColor)
private val DefaultCodeBlockPadding: TextUnit = 16.sp
private const val DefaultCodeWordWrap: Boolean = true

internal fun CodeBlockStyle.resolveDefaults() = CodeBlockStyle(
  textStyle = textStyle ?: DefaultCodeBlockTextStyle,
  modifier = modifier ?: DefaultCodeBlockModifier,
  padding = padding ?: DefaultCodeBlockPadding,
  wordWrap = wordWrap ?: DefaultCodeWordWrap
)

/**
 * A specially-formatted block of text that typically uses a monospace font with a tinted
 * background.
 *
 * @param wordWrap Overrides word wrap preference coming from [CodeBlockStyle]
 */
@Composable public fun RichTextScope.CodeBlock(
  text: String,
  markdownAnimationState: MarkdownAnimationState = remember { MarkdownAnimationState() },
  richTextRenderOptions: RichTextRenderOptions = RichTextRenderOptions(),
  wordWrap: Boolean? = null,
  modifier: Modifier = Modifier,
  textAlign: TextAlign? = null,
  textDirection: TextDirection? = null,
) {
  CodeBlock(
    wordWrap = wordWrap,
    markdownAnimationState = markdownAnimationState,
    richTextRenderOptions = richTextRenderOptions,
    modifier = modifier,
    textAlign = textAlign,
    textDirection = textDirection,
  ) {
    Text(
      text = text,
      modifier = if (textAlign != null || textDirection != null) {
        Modifier.fillMaxWidth()
      } else {
        Modifier
      },
      textAlign = textAlign,
      textDirection = textDirection,
    )
  }
}

/**
 * A specially-formatted block of text that typically uses a monospace font with a tinted
 * background.
 *
 * @param wordWrap Overrides word wrap preference coming from [CodeBlockStyle]
 */
@Composable public fun RichTextScope.CodeBlock(
  wordWrap: Boolean? = null,
  markdownAnimationState: MarkdownAnimationState = remember { MarkdownAnimationState() },
  richTextRenderOptions: RichTextRenderOptions = RichTextRenderOptions(),
  modifier: Modifier = Modifier,
  textAlign: TextAlign? = null,
  textDirection: TextDirection? = null,
  children: @Composable RichTextScope.() -> Unit
) {
  val codeBlockStyle = currentRichTextStyle.resolveDefaults().codeBlockStyle!!
  val textStyle = currentTextStyle.merge(codeBlockStyle.textStyle)
  val blockModifier = codeBlockStyle.modifier!!
  val blockPadding = with(LocalDensity.current) {
    codeBlockStyle.padding!!.toDp()
  }
  val resolvedWordWrap = wordWrap ?: codeBlockStyle.wordWrap!!
  val alpha = rememberMarkdownFade(richTextRenderOptions, markdownAnimationState)

  CodeBlockLayout(
    wordWrap = resolvedWordWrap
  ) { layoutModifier ->
    Box(
      modifier = layoutModifier
        .then(modifier)
        .applyRtlCompatibilityFillWidth(richTextRenderOptions, textDirection)
        .graphicsLayer { this.alpha = alpha.value }
        .then(blockModifier)
        .padding(blockPadding)
    ) {
      textStyleBackProvider(textStyle) {
        children()
      }
    }
  }
}

/**
 * Desktop composable adds an optional horizontal scrollbar.
 */
@Composable
internal expect fun RichTextScope.CodeBlockLayout(
  wordWrap: Boolean,
  children: @Composable RichTextScope.(Modifier) -> Unit
)
