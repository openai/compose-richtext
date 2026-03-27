package com.halilibo.richtext.markdown

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import com.halilibo.richtext.markdown.node.AstCode
import com.halilibo.richtext.markdown.node.AstFencedCodeBlock
import com.halilibo.richtext.markdown.node.AstHtmlBlock
import com.halilibo.richtext.markdown.node.AstHtmlInline
import com.halilibo.richtext.markdown.node.AstIndentedCodeBlock
import com.halilibo.richtext.markdown.node.AstNode
import com.halilibo.richtext.markdown.node.AstNodeLinks
import com.halilibo.richtext.markdown.node.AstParagraph
import com.halilibo.richtext.markdown.node.AstSoftLineBreak
import com.halilibo.richtext.markdown.node.AstText
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

    assertEquals(TextDirection.Rtl, astNode.firstStrongTextDirectionInFirstLine())
  }

  @Test
  fun htmlInlineDirectionIgnoresTagsForNeutralContent() {
    val astNode = AstNode(
      type = AstHtmlInline("<p align=\"right\">12345</p>"),
      links = AstNodeLinks(),
    )

    assertNull(astNode.firstStrongTextDirectionInFirstLine())
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
  fun blockCodeDirectionUsesFirstLineOnly() {
    val englishFirstLine = """
      val english = "Hello"
      שלום
    """.trimIndent()
    val hebrewFirstLine = """
      שלום
      val english = "Hello"
    """.trimIndent()

    assertEquals(
      TextDirection.Ltr,
      AstNode(AstFencedCodeBlock('`', 3, 0, "", englishFirstLine), AstNodeLinks())
        .firstStrongTextDirectionInFirstLine(),
    )
    assertEquals(
      TextDirection.Rtl,
      AstNode(AstIndentedCodeBlock(hebrewFirstLine), AstNodeLinks())
        .firstStrongTextDirectionInFirstLine(),
    )
  }

  @Test
  fun containerDirectionUsesFirstLineOnly() {
    val firstText = AstNode(AstText("123"), AstNodeLinks())
    val lineBreak = AstNode(AstSoftLineBreak, AstNodeLinks(previous = firstText))
    val secondText = AstNode(AstText("hello"), AstNodeLinks(previous = lineBreak))
    firstText.links.next = lineBreak
    lineBreak.links.next = secondText

    val paragraph = AstNode(
      AstParagraph,
      AstNodeLinks(
        firstChild = firstText,
        lastChild = secondText,
      ),
    )
    firstText.links.parent = paragraph
    lineBreak.links.parent = paragraph
    secondText.links.parent = paragraph

    assertNull(paragraph.firstStrongTextDirectionInFirstLine())
  }

  @Test
  fun compatibilityTextAlignMatchesDirection() {
    assertEquals(TextAlign.Left, TextDirection.Ltr.toCompatibilityTextAlign())
    assertEquals(TextAlign.Right, TextDirection.Rtl.toCompatibilityTextAlign())
    assertNull(null.toCompatibilityTextAlign())
  }

  @Test
  fun compatibilityTextDirectionMatchesDirection() {
    assertEquals(TextDirection.ContentOrLtr, TextDirection.Ltr.toCompatibilityTextDirection())
    assertEquals(TextDirection.ContentOrRtl, TextDirection.Rtl.toCompatibilityTextDirection())
    assertNull(null.toCompatibilityTextDirection())
  }
}
