package com.zachklipp.richtext.sample.contentreference

import org.commonmark.node.CustomNode
import org.commonmark.node.Delimited

/**
 * Custom markdown node that represents a [ApiContentReference] that spans this portion of the markdown.
 */
class ContentReferenceNode(
  val contentReference: ApiContentReference,
) : CustomNode(), Delimited {
  override fun getOpeningDelimiter(): String {
    return ApiContentReference.MarkdownStartDelimiter.toString()
  }

  override fun getClosingDelimiter(): String {
    return ApiContentReference.MarkdownEndDelimiter.toString()
  }
}
