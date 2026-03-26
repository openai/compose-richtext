package com.halilibo.richtext.markdown

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import com.halilibo.richtext.markdown.node.AstCode
import com.halilibo.richtext.markdown.node.AstHtmlBlock
import com.halilibo.richtext.markdown.node.AstHtmlInline
import com.halilibo.richtext.markdown.node.AstNode
import com.halilibo.richtext.markdown.node.AstNodeLinks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TraverseUtilsTest {

  @Test
  fun firstStrongDirectionInFirstLineSkipsNeutralPrefix() {
    assertEquals(TextDirection.Rtl, "...שלום".firstStrongTextDirection(stopAtLineBreak = true))
    assertEquals(TextDirection.Ltr, "...hello".firstStrongTextDirection(stopAtLineBreak = true))
    assertNull("...123".firstStrongTextDirection(stopAtLineBreak = true))
  }

  @Test
  fun htmlBlockDirectionIgnoresTags() {
    val astNode = AstNode(
      type = AstHtmlBlock("<center>שלום עולם</center>"),
      links = AstNodeLinks(),
    )

    assertEquals(TextDirection.Rtl, astNode.firstStrongTextDirectionInSubtree())
  }

  @Test
  fun htmlInlineDirectionIgnoresTagsForNeutralContent() {
    val astNode = AstNode(
      type = AstHtmlInline("<p align=\"right\">12345</p>"),
      links = AstNodeLinks(),
    )

    assertNull(astNode.firstStrongTextDirectionInSubtree())
  }

  @Test
  fun codeDirectionUsesFirstLineOnly() {
    val code = """
      val english = "Hello"
      שלום
    """.trimIndent()

    assertEquals(TextDirection.Ltr, code.firstStrongTextDirection(stopAtLineBreak = true))
  }

  @Test
  fun compatibilityTextAlignMatchesDirection() {
    assertEquals(TextAlign.Left, TextDirection.Ltr.toCompatibilityTextAlign())
    assertEquals(TextAlign.Right, TextDirection.Rtl.toCompatibilityTextAlign())
    assertNull(null.toCompatibilityTextAlign())
  }
}
