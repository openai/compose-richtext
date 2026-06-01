package com.halilibo.richtext.ui.string

import androidx.compose.ui.graphics.Color
import kotlin.time.ComparableTimeMark
import kotlin.time.TimeSource

/**
 * Allows configuration of the Markdown renderer
 */
public data class RichTextRenderOptions(
  val animate: Boolean = false,
  val textFadeInMs: Int = 500,
  val debounceMs: Int = 100050,
  val delayMs: Int = 70,
  val delayExponent: Double = 0.7,
  val maxPhraseLength: Int = 30,
  val phraseMarkersOverride: List<Char>? = null,
  val onlyRenderVisibleText: Boolean = false,
  val enableRtlCompatibility: Boolean = false,
  val streamingTextAccent: StreamingTextAccent? = null,
  val onTextAnimate: () -> Unit = {},
  val onPhraseAnimate: () -> Unit = {},
) {
  public companion object {
    public val Default: RichTextRenderOptions = RichTextRenderOptions()
  }
}

/**
 * Draws a short-lived accent overlay above newly revealed streaming text.
 *
 * Each revealed text part fades independently while [decayDurationMs] reduces the starting alpha of
 * later parts relative to [decayStartTimeMark]. This keeps the accent treatment limited to the
 * opening portion of a response even if its markdown blocks are recreated.
 */
public data class StreamingTextAccent(
  val color: Color,
  val fadeOutMs: Int = 600,
  val decayDurationMs: Int = 2_000,
  val excludesEmoji: Boolean = false,
  val decayStartTimeMark: ComparableTimeMark = TimeSource.Monotonic.markNow(),
) {
  init {
    require(fadeOutMs > 0)
    require(decayDurationMs > 0)
  }
}
