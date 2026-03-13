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
}
