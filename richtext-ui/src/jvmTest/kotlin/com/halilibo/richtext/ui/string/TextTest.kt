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
  fun `applyParagraphStyle adds directional paragraph layout`() {
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
        textDirection = TextDirection.Rtl,
      ).paragraphStyles,
    )
  }

  @Test
  fun `applyParagraphStyle returns original text when direction is null`() {
    val text = AnnotatedString("שלום")

    assertSame(text, applyParagraphStyle(text = text, textDirection = null))
  }

  @Test
  fun `applyParagraphStyle keeps centered paragraphs centered`() {
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

    assertEquals(
      listOf(
        AnnotatedString.Range(
          item = ParagraphStyle(
            textAlign = TextAlign.Center,
          ),
          start = 0,
          end = 5,
        ),
        AnnotatedString.Range(
          item = ParagraphStyle(
            textAlign = TextAlign.Left,
            textDirection = TextDirection.Ltr,
          ),
          start = 0,
          end = 5,
        ),
        AnnotatedString.Range(
          item = ParagraphStyle(
            textAlign = TextAlign.Center,
            textDirection = TextDirection.Ltr,
          ),
          start = 0,
          end = 5,
        ),
      ),
      applyParagraphStyle(
        text = text,
        textDirection = TextDirection.Ltr,
      ).paragraphStyles,
    )
  }

  @Test
  fun `applyParagraphStyle preserves explicit text direction overrides`() {
    val text = AnnotatedString(
      text = "שלום",
      paragraphStyles = listOf(
        AnnotatedString.Range(
          item = ParagraphStyle(
            textAlign = TextAlign.Center,
            textDirection = TextDirection.Rtl,
          ),
          start = 0,
          end = 4,
        ),
      ),
    )

    assertEquals(
      listOf(
        AnnotatedString.Range(
          item = ParagraphStyle(
            textAlign = TextAlign.Center,
            textDirection = TextDirection.Rtl,
          ),
          start = 0,
          end = 4,
        ),
        AnnotatedString.Range(
          item = ParagraphStyle(
            textAlign = TextAlign.Left,
            textDirection = TextDirection.Ltr,
          ),
          start = 0,
          end = 4,
        ),
        AnnotatedString.Range(
          item = ParagraphStyle(
            textAlign = TextAlign.Center,
            textDirection = TextDirection.Rtl,
          ),
          start = 0,
          end = 4,
        ),
      ),
      applyParagraphStyle(
        text = text,
        textDirection = TextDirection.Ltr,
      ).paragraphStyles,
    )
  }
}
