package com.halilibo.richtext.ui.string

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class TextTest {
  @Test
  fun `applies paragraph style only when needed`() {
    val text = AnnotatedString("שלום")
    assertEquals(
      listOf(
        AnnotatedString.Range(
          item = ParagraphStyle(
            textAlign = TextAlign.Right,
            textDirection = TextDirection.Rtl,
          ),
          start = 0,
          end = text.length,
        ),
      ),
      applyParagraphStyle(
        text = text,
        textAlign = TextAlign.Right,
        textDirection = TextDirection.Rtl,
      ).paragraphStyles,
    )
    assertSame(text, applyParagraphStyle(text = text, textAlign = null, textDirection = null))
  }
}
