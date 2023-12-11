package com.halilibo.richtext.markdown.node

import androidx.compose.runtime.Immutable

/**
 * All the pointers that can exist for a node in an AST.
 */
@Immutable
public data class AstNodeLinks(
  var parent: AstNode? = null,
  var firstChild: AstNode? = null,
  var lastChild: AstNode? = null,
  var previous: AstNode? = null,
  var next: AstNode? = null
) {

  /**
   * Stop infinite loop and only check towards bottom-right direction
   */
  override fun equals(other: Any?): Boolean {
    if (other !is AstNodeLinks) return false

    return firstChild == other.firstChild && next == other.next
  }

  /**
   * Stop infinite loop and only calculate towards bottom-right direction
   */
  override fun hashCode(): Int {
    return (firstChild ?: 0).hashCode() * 11 + (next ?: 0).hashCode() * 7
  }
}
