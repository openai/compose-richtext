package com.halilibo.richtext.commonmark

import com.halilibo.richtext.markdown.node.AstNode
import com.halilibo.richtext.markdown.node.AstParagraph
import com.halilibo.richtext.markdown.node.AstTableRoot
import com.halilibo.richtext.markdown.node.AstText
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class CommonmarkAstNodeParserTest {

  private val parser = CommonmarkAstNodeParser()

  private fun AstNode.childrenSequence(): Sequence<AstNode> =
    generateSequence(links.firstChild) { node -> node.links.next }

  @Test
  fun `malformed zero-column tables fall back to plain markdown`() {
    val ast = parser.parse(
      """
      |
      |-|
      |
      """.trimIndent()
    )

    assertFalse(ast.childrenSequence().any { child -> child.type is AstTableRoot })
    assertEquals(
      listOf(AstParagraph),
      ast.childrenSequence().map { child -> child.type }.toList()
    )
    assertEquals(
      listOf("|", "|-|", "|"),
      ast.childrenSequence()
        .flatMap { paragraph -> paragraph.childrenSequence() }
        .mapNotNull { child -> (child.type as? AstText)?.literal }
        .toList()
    )
  }

  @Test
  fun `valid tables still render as tables`() {
    val ast = parser.parse(
      """
      | Header |
      | --- |
      | Value |
      """.trimIndent()
    )

    assertTrue(ast.childrenSequence().any { child -> child.type is AstTableRoot })
  }
}
