package com.halilibo.richtext.ui.rtl

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
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

internal val LocalCompatibilityTextAlignOverride = compositionLocalOf<TextAlign?> { null }

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
