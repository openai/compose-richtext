package com.halilibo.richtext.ui.string

import androidx.compose.ui.text.style.TextDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RtlCompatibilityTest {

  @Test
  fun englishTextResolvesToLtr() {
    assertEquals(TextDirection.Ltr, firstStrongTextDirection("This paragraph starts in English."))
  }

  @Test
  fun hebrewTextResolvesToRtl() {
    assertEquals(TextDirection.Rtl, firstStrongTextDirection("הפסקה הזאת מתחילה בעברית."))
  }

  @Test
  fun neutralDigitsStayAmbient() {
    assertNull(firstStrongTextDirection("12345"))
  }
}
