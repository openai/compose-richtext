package com.halilibo.richtext.ui.rtl

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RtlCompatibilityTest {

  @Test
  fun firstStrongDirectionSkipsNeutralPrefix() {
    assertEquals(TextDirection.Rtl, "...שלום".firstStrongTextDirection(stopAtLineBreak = true))
    assertEquals(TextDirection.Ltr, "...hello".firstStrongTextDirection(stopAtLineBreak = true))
    assertNull("...123".firstStrongTextDirection(stopAtLineBreak = true))
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
