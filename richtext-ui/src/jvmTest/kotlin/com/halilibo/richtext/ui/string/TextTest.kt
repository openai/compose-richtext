package com.halilibo.richtext.ui.string

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class TextTest {
  @Test
  fun `applies paragraph direction only when needed`() {
    val text = AnnotatedString("שלום")
    assertEquals(
      listOf(
        AnnotatedString.Range(
          item = ParagraphStyle(
            textDirection = TextDirection.Rtl,
          ),
          start = 0,
          end = text.length,
        ),
      ),
      applyParagraphDirection(
        text = text,
        textDirection = TextDirection.Rtl,
      ).paragraphStyles,
    )
    assertSame(text, applyParagraphDirection(text = text, textDirection = null))
  }
}
