package com.halilibo.richtext.ui.string

import androidx.compose.ui.text.style.TextDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

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

  @Test
  fun rtlCompatibilityFillWidthOnlyForRtlText() {
    val renderOptions = RichTextRenderOptions(enableRtlCompatibility = true)

    assertTrue(shouldFillWidthForRtlCompatibility(renderOptions, TextDirection.Rtl))
    assertFalse(shouldFillWidthForRtlCompatibility(renderOptions, TextDirection.Ltr))
    assertFalse(shouldFillWidthForRtlCompatibility(renderOptions, null))
  }

  @Test
  fun rtlCompatibilityNeverFillsWidthWhenDisabled() {
    val renderOptions = RichTextRenderOptions(enableRtlCompatibility = false)

    assertFalse(shouldFillWidthForRtlCompatibility(renderOptions, TextDirection.Rtl))
  }
}
