package com.halilibo.richtext.markdown.rtl

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import com.halilibo.richtext.markdown.childrenSequence
import com.halilibo.richtext.markdown.node.AstCode
import com.halilibo.richtext.markdown.node.AstFencedCodeBlock
import com.halilibo.richtext.markdown.node.AstHardLineBreak
import com.halilibo.richtext.markdown.node.AstHtmlBlock
import com.halilibo.richtext.markdown.node.AstHtmlInline
import com.halilibo.richtext.markdown.node.AstIndentedCodeBlock
import com.halilibo.richtext.markdown.node.AstNode
import com.halilibo.richtext.markdown.node.AstSoftLineBreak
import com.halilibo.richtext.markdown.node.AstText
import kotlin.text.CharDirectionality

/**
 * Lets list and quote containers override paragraph alignment without forcing a different
 * direction-detection rule for the text inside the item.
 */
internal val LocalCompatibilityTextAlignOverride = compositionLocalOf<TextAlign?> { null }

/**
 * Returns the first strong bidi direction found before the first rendered line break in this node.
 *
 * Examples:
 * - `AstText("...שלום")` returns [TextDirection.Rtl].
 * - `AstText("...123")` returns `null`.
 *
 * Edge case:
 * - A paragraph like `123\nhello` still returns `null`, because compatibility mode treats the
 *   first visible line as decisive for block alignment and ignores stronger text on later lines.
 */
internal fun AstNode.firstStrongTextDirectionInFirstLine(): TextDirection? {
  var lineEnded = false

  fun AstNode.findFirstStrongTextDirection(): TextDirection? {
    if (lineEnded) return null

    if (type is AstSoftLineBreak || type is AstHardLineBreak) {
      lineEnded = true
      return null
    }

    val literal = when (type) {
      is AstText -> type.literal
      is AstCode -> type.literal
      is AstIndentedCodeBlock -> type.literal
      is AstFencedCodeBlock -> type.literal
      is AstHtmlBlock -> type.literal
      is AstHtmlInline -> type.literal
      else -> null
    }
    val direction = literal?.firstStrongTextDirection(
      stopAtLineBreak = true,
      ignoreHtmlTags = type is AstHtmlBlock || type is AstHtmlInline,
      onLineBreak = { lineEnded = true },
    )

    return direction ?: childrenSequence().firstNotNullOfOrNull { child ->
      child.findFirstStrongTextDirection()
    }
  }

  return findFirstStrongTextDirection()
}

internal fun TextDirection?.toCompatibilityTextAlign(): TextAlign? = when (this) {
  TextDirection.Ltr -> TextAlign.Left
  TextDirection.Rtl -> TextAlign.Right
  else -> null
}

internal fun TextDirection?.toCompatibilityTextDirection(): TextDirection? = when (this) {
  TextDirection.Ltr -> TextDirection.ContentOrLtr
  TextDirection.Rtl -> TextDirection.ContentOrRtl
  else -> null
}

@Composable
internal fun Modifier.fillMaxWidthForRtlCompatibility(
  enableRtlCompatibility: Boolean,
  contentDirection: TextDirection?,
): Modifier = if (
  enableRtlCompatibility &&
  contentDirection.isOppositeOf(LocalLayoutDirection.current)
) {
  fillMaxWidth()
} else {
  this
}

private fun TextDirection?.isOppositeOf(layoutDirection: LayoutDirection): Boolean = when (this) {
  TextDirection.Ltr -> layoutDirection == LayoutDirection.Rtl
  TextDirection.Rtl -> layoutDirection == LayoutDirection.Ltr
  else -> false
}

/**
 * Scans text for the first strong bidi character and returns its direction.
 *
 * Examples:
 * - `"123 hello"` returns [TextDirection.Ltr].
 * - `"123 שלום"` returns [TextDirection.Rtl].
 *
 * Edge cases:
 * - With `stopAtLineBreak = true`, `"123\nhello"` returns `null` instead of
 *   [TextDirection.Ltr].
 * - With `ignoreHtmlTags = true`, `"<b>שלום</b>"` returns [TextDirection.Rtl] rather than
 *   treating tag characters as content.
 */
internal fun CharSequence.firstStrongTextDirection(
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
