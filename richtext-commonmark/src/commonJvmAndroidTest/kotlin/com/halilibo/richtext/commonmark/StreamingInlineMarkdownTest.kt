package com.halilibo.richtext.commonmark

import kotlin.test.Test
import kotlin.test.assertEquals

internal class StreamingInlineMarkdownTest {

  @Test
  fun `auto-closes strong emphasis when the closer is missing`() {
    assertEquals(
      expected = "**streaming**",
      actual = autoCloseStreamingInlineMarkdown("**streaming")
    )
  }

  @Test
  fun `auto-closes nested inline delimiters`() {
    assertEquals(
      expected = "**bold _and italic_**",
      actual = autoCloseStreamingInlineMarkdown("**bold _and italic")
    )
  }

  @Test
  fun `trims trailing empty opener`() {
    assertEquals(
      expected = "Hello ",
      actual = autoCloseStreamingInlineMarkdown("Hello **")
    )
  }

  @Test
  fun `auto-closes code delimiter`() {
    assertEquals(
      expected = "`code`",
      actual = autoCloseStreamingInlineMarkdown("`code")
    )
  }

  @Test
  fun `does not modify completed markdown`() {
    val markdown = "**ready** and `done`"
    assertEquals(
      expected = markdown,
      actual = autoCloseStreamingInlineMarkdown(markdown)
    )
  }
}
