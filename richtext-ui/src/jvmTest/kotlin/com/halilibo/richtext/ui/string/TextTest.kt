package com.halilibo.richtext.ui.string

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TextTest {
  @Test
  fun `applyParagraphDirection adds paragraph direction style`() {
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
  }

  @Test
  fun `applyParagraphDirection returns original text when direction is null`() {
    val text = AnnotatedString("שלום")

    assertSame(text, applyParagraphDirection(text = text, textDirection = null))
  }

  @Test
  fun `shouldFillWidthForExplicitParagraphAlignment returns false when opt in is disabled`() {
    val text = AnnotatedString(
      text = "Hello",
      paragraphStyles = listOf(
        AnnotatedString.Range(
          item = ParagraphStyle(textAlign = TextAlign.Center),
          start = 0,
          end = 5,
        ),
      ),
    )

    assertFalse(
      shouldFillWidthForExplicitParagraphAlignment(
        text = text,
        fillWidthForExplicitParagraphAlignment = false,
      )
    )
  }

  @Test
  fun `shouldFillWidthForExplicitParagraphAlignment ignores direction only paragraphs`() {
    val text = AnnotatedString(
      text = "שלום",
      paragraphStyles = listOf(
        AnnotatedString.Range(
          item = ParagraphStyle(textDirection = TextDirection.Rtl),
          start = 0,
          end = 4,
        ),
      ),
    )

    assertFalse(
      shouldFillWidthForExplicitParagraphAlignment(
        text = text,
        fillWidthForExplicitParagraphAlignment = true,
      )
    )
  }

  @Test
  fun `shouldFillWidthForExplicitParagraphAlignment returns true for centered paragraphs when enabled`() {
    val text = AnnotatedString(
      text = "Hello",
      paragraphStyles = listOf(
        AnnotatedString.Range(
          item = ParagraphStyle(textAlign = TextAlign.Center),
          start = 0,
          end = 5,
        ),
      ),
    )

    assertTrue(
      shouldFillWidthForExplicitParagraphAlignment(
        text = text,
        fillWidthForExplicitParagraphAlignment = true,
      )
    )
  }
}
