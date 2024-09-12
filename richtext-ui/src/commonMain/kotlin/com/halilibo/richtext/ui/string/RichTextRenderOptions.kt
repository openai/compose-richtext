package com.halilibo.richtext.ui.string

/**
 * Allows configuration of the Markdown renderer
 */
public data class RichTextRenderOptions(
  val animate: Boolean = false,
  val textFadeInMs: Int = 500,
  val debounceMs: Int = 100050,
  val delayMs: Int = 70,
  val onTextAnimate: () -> Unit = {},
) {
  public companion object {
    public val Default: RichTextRenderOptions = RichTextRenderOptions()
  }
}
