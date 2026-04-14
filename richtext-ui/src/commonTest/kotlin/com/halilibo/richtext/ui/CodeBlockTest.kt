package com.halilibo.richtext.ui

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import kotlin.test.Test
import kotlin.test.assertEquals

class CodeBlockTest {

  @Test
  fun compatibilityAppliesParagraphDirectionPerLine() {
    val text = "שלום\nhello"

    val paragraphStyles = text.applyLineDirectionForCodeBlock(
      fallbackTextAlign = TextAlign.Right,
      fallbackTextDirection = TextDirection.ContentOrRtl,
    ).paragraphStyles

    assertEquals(
      listOf(
        AnnotatedParagraphStyle(
          start = 0,
          end = 5,
          textAlign = TextAlign.Right,
          textDirection = TextDirection.ContentOrRtl,
        ),
        AnnotatedParagraphStyle(
          start = 5,
          end = text.length,
          textAlign = TextAlign.Left,
          textDirection = TextDirection.ContentOrLtr,
        ),
      ),
      paragraphStyles.toParagraphStyles(),
    )
  }

  @Test
  fun neutralLineFallsBackToBlockDirection() {
    val text = "123\nשלום"

    val paragraphStyles = text.applyLineDirectionForCodeBlock(
      fallbackTextAlign = TextAlign.Left,
      fallbackTextDirection = TextDirection.ContentOrLtr,
    ).paragraphStyles

    assertEquals(
      listOf(
        AnnotatedParagraphStyle(
          start = 0,
          end = 4,
          textAlign = TextAlign.Left,
          textDirection = TextDirection.ContentOrLtr,
        ),
        AnnotatedParagraphStyle(
          start = 4,
          end = text.length,
          textAlign = TextAlign.Right,
          textDirection = TextDirection.ContentOrRtl,
        ),
      ),
      paragraphStyles.toParagraphStyles(),
    )
  }
}

private data class AnnotatedParagraphStyle(
  val start: Int,
  val end: Int,
  val textAlign: TextAlign,
  val textDirection: TextDirection,
)

private fun List<AnnotatedString.Range<ParagraphStyle>>.toParagraphStyles(): List<AnnotatedParagraphStyle> =
  map { range ->
    AnnotatedParagraphStyle(
      start = range.start,
      end = range.end,
      textAlign = range.item.textAlign,
      textDirection = range.item.textDirection,
    )
  }
