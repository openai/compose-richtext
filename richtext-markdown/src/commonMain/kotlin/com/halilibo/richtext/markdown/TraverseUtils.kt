package com.halilibo.richtext.markdown

import androidx.compose.ui.text.style.TextDirection
import com.halilibo.richtext.markdown.node.AstCode
import com.halilibo.richtext.markdown.node.AstHardLineBreak
import com.halilibo.richtext.markdown.node.AstImage
import com.halilibo.richtext.markdown.node.AstNode
import com.halilibo.richtext.markdown.node.AstNodeType
import com.halilibo.richtext.markdown.node.AstSoftLineBreak
import com.halilibo.richtext.markdown.node.AstText
import kotlin.text.CharDirectionality

internal fun AstNode.childrenSequence(
  reverse: Boolean = false
): Sequence<AstNode> {
  return if (!reverse) {
    generateSequence(this.links.firstChild) { it.links.next }
  } else {
    generateSequence(this.links.lastChild) { it.links.previous }
  }
}

/**
 * Markdown rendering is susceptible to have assumptions. Hence, some rendering rules
 * may force restrictions on children. So, valid children nodes should be selected
 * before traversing. This function returns a LinkedList of children which conforms to
 * [filter] function.
 *
 * @param filter A lambda to select valid children.
 */
internal fun AstNode.filterChildren(
  reverse: Boolean = false,
  filter: (AstNode) -> Boolean
): Sequence<AstNode> {
  return childrenSequence(reverse).filter(filter)
}

internal inline fun <reified T : AstNodeType> AstNode.filterChildrenType(): Sequence<AstNode> {
  return filterChildren { it.type is T }
}

/**
 * These ASTNode types should never have any children. If any exists, ignore them.
 */
internal fun AstNode.isRichTextTerminal(): Boolean {
  return type is AstText
          || type is AstCode
          || type is AstImage
          || type is AstSoftLineBreak
          || type is AstHardLineBreak
}

internal fun AstNode.firstStrongTextDirectionInFirstLine(): TextDirection? {
  val firstChild = links.firstChild ?: return null
  val firstLine = StringBuilder()
  firstChild.appendFirstLineText(firstLine)
  return firstStrongTextDirection(firstLine)
}

internal fun AstNode.firstStrongTextDirectionInSubtree(): TextDirection? {
  when (val currentType = type) {
    is AstText -> return firstStrongTextDirection(currentType.literal)
    is AstCode -> return firstStrongTextDirection(currentType.literal)
    else -> Unit
  }

  childrenSequence().forEach { child ->
    child.firstStrongTextDirectionInSubtree()?.let { return it }
  }

  return null
}

private fun AstNode.appendFirstLineText(builder: StringBuilder): Boolean {
  when (val currentType = type) {
    is AstText -> builder.append(currentType.literal)
    is AstCode -> builder.append(currentType.literal)
    is AstSoftLineBreak, is AstHardLineBreak -> return true
    else -> Unit
  }

  childrenSequence().forEach { child ->
    if (child.appendFirstLineText(builder)) {
      return true
    }
  }

  return false
}

private fun firstStrongTextDirection(text: CharSequence): TextDirection? {
  for (char in text) {
    when (char.directionality) {
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

  return null
}
