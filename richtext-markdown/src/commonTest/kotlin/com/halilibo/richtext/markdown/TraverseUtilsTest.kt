package com.halilibo.richtext.markdown

import androidx.compose.ui.text.style.TextDirection
import com.halilibo.richtext.markdown.node.AstHtmlBlock
import com.halilibo.richtext.markdown.node.AstHtmlInline
import com.halilibo.richtext.markdown.node.AstNode
import com.halilibo.richtext.markdown.node.AstNodeLinks
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TraverseUtilsTest {

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
}
