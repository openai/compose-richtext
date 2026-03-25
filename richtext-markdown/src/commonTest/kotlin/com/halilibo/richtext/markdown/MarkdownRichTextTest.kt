package com.halilibo.richtext.markdown

import androidx.compose.ui.text.style.TextDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MarkdownRichTextTest {
  @Test
  fun `detects direction from the first strong letter`() {
    assertEquals(TextDirection.Rtl, firstStrongLetterLayoutDirection("...שלום"))
    assertEquals(TextDirection.Ltr, firstStrongLetterLayoutDirection("...hello"))
    assertNull(firstStrongLetterLayoutDirection("...123"))
  }

  @Test
  fun `html block direction ignores tags`() {
    assertEquals(TextDirection.Rtl, htmlBlockTextDirection("<center>مرحبا</center>"))
    assertEquals(TextDirection.Ltr, htmlBlockTextDirection("<div>hello</div>"))
    assertNull(htmlBlockTextDirection("<center>12345</center>"))
  }

  @Test
  fun `html block alignment detects centered and trailing tags`() {
    assertEquals(MarkdownBlockAlignment.Center, htmlBlockAlignment("<center>12345</center>"))
    assertEquals(
      MarkdownBlockAlignment.Center,
      htmlBlockAlignment("<p align=\"center\">مرحبا</p>"),
    )
    assertEquals(
      MarkdownBlockAlignment.End,
      htmlBlockAlignment("<p align='right'>مرحبا</p>"),
    )
    assertNull(htmlBlockAlignment("<div>hello</div>"))
  }

  @Test
  fun `effective text direction inherits document direction for neutral blocks`() {
    assertEquals(
      TextDirection.Rtl,
      effectiveTextDirection(blockTextDirection = null, documentTextDirection = TextDirection.Rtl),
    )
    assertEquals(
      TextDirection.Ltr,
      effectiveTextDirection(blockTextDirection = TextDirection.Ltr, documentTextDirection = TextDirection.Rtl),
    )
    assertNull(effectiveTextDirection(blockTextDirection = null, documentTextDirection = null))
  }
}
