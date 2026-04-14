package com.halilibo.richtext.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.ui.rtl.fillMaxWidthForRtlCompatibility
import com.halilibo.richtext.ui.rtl.firstStrongTextDirection
import com.halilibo.richtext.ui.rtl.toCompatibilityDirection
import com.halilibo.richtext.ui.rtl.toCompatibilityTextAlign
import com.halilibo.richtext.ui.rtl.toCompatibilityTextDirection
import com.halilibo.richtext.ui.string.MarkdownAnimationState
import com.halilibo.richtext.ui.string.RichTextRenderOptions

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
  val compatibilityDirection = textAlign.toCompatibilityDirection()

  CodeBlock(
    wordWrap = wordWrap,
    markdownAnimationState = markdownAnimationState,
    richTextRenderOptions = richTextRenderOptions,
    modifier = modifier,
    textAlign = textAlign,
    textDirection = textDirection,
  ) {
    val textModifier = Modifier.fillMaxWidthForRtlCompatibility(
      enableRtlCompatibility = richTextRenderOptions.enableRtlCompatibility,
      contentDirection = compatibilityDirection,
    )

    if (richTextRenderOptions.enableRtlCompatibility) {
      val codeBlockText = remember(text, textAlign, textDirection) {
        text.applyLineDirectionForCodeBlock(
          fallbackTextAlign = textAlign,
          fallbackTextDirection = textDirection,
        )
      }

      Text(
        text = codeBlockText,
        modifier = textModifier,
      )
    } else {
      Text(
        text = text,
        modifier = textModifier,
        textAlign = textAlign,
        textDirection = textDirection,
      )
    }
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
  val compatibilityDirection = textAlign.toCompatibilityDirection()
  val alpha = rememberMarkdownFade(richTextRenderOptions, markdownAnimationState)

  CodeBlockLayout(
    wordWrap = resolvedWordWrap
  ) { layoutModifier ->
    Box(
      modifier = layoutModifier
        .graphicsLayer{ this.alpha = alpha.value }
        .then(
          modifier.fillMaxWidthForRtlCompatibility(
            enableRtlCompatibility = richTextRenderOptions.enableRtlCompatibility,
            contentDirection = compatibilityDirection,
          )
        )
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

internal fun String.applyLineDirectionForCodeBlock(
  fallbackTextAlign: TextAlign?,
  fallbackTextDirection: TextDirection?,
): AnnotatedString {
  val fallbackParagraphStyle = ParagraphStyle(
    textAlign = fallbackTextAlign ?: TextAlign.Unspecified,
    textDirection = fallbackTextDirection ?: TextDirection.Unspecified,
  ).takeIf {
    fallbackTextAlign != null || fallbackTextDirection != null
  }

  return buildAnnotatedString {
    append(this@applyLineDirectionForCodeBlock)

    var lineStart = 0
    while (lineStart < length) {
      val lineBreakIndex = indexOfLineBreak(startIndex = lineStart)
      val lineEnd = lineBreakIndex.takeIf { it >= 0 } ?: length
      val nextLineStart = when {
        lineBreakIndex < 0 -> length
        this@applyLineDirectionForCodeBlock[lineBreakIndex] == '\r' &&
          lineBreakIndex + 1 < length &&
          this@applyLineDirectionForCodeBlock[lineBreakIndex + 1] == '\n' -> lineBreakIndex + 2
        else -> lineBreakIndex + 1
      }

      val paragraphStyle = this@applyLineDirectionForCodeBlock
        .subSequence(lineStart, lineEnd)
        .firstStrongTextDirection()
        ?.let { direction ->
          ParagraphStyle(
            textAlign = direction.toCompatibilityTextAlign() ?: TextAlign.Unspecified,
            textDirection = direction.toCompatibilityTextDirection() ?: TextDirection.Unspecified,
          )
        }
        ?: fallbackParagraphStyle

      if (paragraphStyle != null) {
        addStyle(
          paragraphStyle,
          start = lineStart,
          end = nextLineStart,
        )
      }

      lineStart = nextLineStart
    }
  }
}

private fun CharSequence.indexOfLineBreak(startIndex: Int): Int {
  for (index in startIndex until length) {
    if (this[index] == '\n' || this[index] == '\r') {
      return index
    }
  }

  return -1
}
