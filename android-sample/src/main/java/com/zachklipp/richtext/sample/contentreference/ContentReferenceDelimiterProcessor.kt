package com.zachklipp.richtext.sample.contentreference


import org.commonmark.node.Nodes
import org.commonmark.node.Text
import org.commonmark.parser.delimiter.DelimiterProcessor
import org.commonmark.parser.delimiter.DelimiterRun

class ContentReferenceDelimiterProcessor(
  private val references: List<ApiContentReference>,
) : DelimiterProcessor {
  override fun getOpeningCharacter(): Char {
    return ApiContentReference.MarkdownStartDelimiter
  }

  override fun getClosingCharacter(): Char {
    return ApiContentReference.MarkdownEndDelimiter
  }

  override fun getMinLength(): Int {
    return 1
  }

  override fun process(openingRun: DelimiterRun, closingRun: DelimiterRun): Int {
    if (openingRun.length() != closingRun.length()) {
      return 0
    }
    val opener = openingRun.opener
    val closer = closingRun.closer
    val refIndex = (opener.next as? Text)?.literal?.toIntOrNull() ?: return 0
    val newNode = ContentReferenceNode(references[refIndex])

    // Remove all existing nodes between the opening and closing braces and replace it
    // with the content reference node.
    // By returning non-zero, the parser will strip out the opening/closing nodes so we
    // don't need to remove them ourselves.
    for (node in Nodes.between(opener, closer)) {
      // The docs says it is in between exclusive but the closer is included
      if (node == closer) continue
      node.unlink()
    }
    opener.insertAfter(newNode)
    closer.insertBefore(newNode)

    return openingRun.length()
  }
}
