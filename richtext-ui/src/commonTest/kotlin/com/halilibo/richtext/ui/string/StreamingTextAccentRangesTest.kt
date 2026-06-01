package com.halilibo.richtext.ui.string

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import kotlin.test.Test
import kotlin.test.assertEquals

class StreamingTextAccentRangesTest {

  @Test
  fun excludesLinks() {
    val text = "before link after"
    val annotated = buildAnnotatedString {
      append(text)
      addLink(
        LinkAnnotation.Url("https://example.com"),
        start = text.indexOf("link"),
        end = text.indexOf("link") + "link".length,
      )
    }

    assertEquals(
      listOf("before ", " after"),
      annotated.streamingTextAccentRanges(
        start = 0,
        end = text.length,
        excludesEmoji = false,
      ).map { text.substring(it.start, it.end) },
    )
  }

  @Test
  fun includesEmojiByDefault() {
    val text = "before 😎 middle ❤️ after 1️⃣ done"

    assertEquals(
      listOf(text),
      buildAnnotatedString { append(text) }
        .streamingTextAccentRanges(
          start = 0,
          end = text.length,
          excludesEmoji = false,
        )
        .map { text.substring(it.start, it.end) },
    )
  }

  @Test
  fun excludesEmojiClustersWhenRequested() {
    val text = "before 😎 middle ❤️ after 1️⃣ done"

    assertEquals(
      listOf("before ", " middle ", " after ", " done"),
      buildAnnotatedString { append(text) }
        .streamingTextAccentRanges(
          start = 0,
          end = text.length,
          excludesEmoji = true,
        )
        .map { text.substring(it.start, it.end) },
    )
  }

  @Test
  fun excludesZeroWidthJoinerEmojiClusterWhenRequested() {
    val text = "before 👩‍💻 after"

    assertEquals(
      listOf("before ", " after"),
      buildAnnotatedString { append(text) }
        .streamingTextAccentRanges(
          start = 0,
          end = text.length,
          excludesEmoji = true,
        )
        .map { text.substring(it.start, it.end) },
    )
  }

  @Test
  fun excludesRegionalIndicatorEmojiClusterWhenRequested() {
    val text = "before 🇺🇸 after"

    assertEquals(
      listOf("before ", " after"),
      buildAnnotatedString { append(text) }
        .streamingTextAccentRanges(
          start = 0,
          end = text.length,
          excludesEmoji = true,
        )
        .map { text.substring(it.start, it.end) },
    )
  }

  @Test
  fun decayStartIsSharedAcrossRecreatedAnimationStates() {
    val accent = StreamingTextAccent(
      color = Color.Red,
      decayDurationMs = 2_000,
      decayStartMs = 1_000,
    )

    assertEquals(0.5f, MarkdownAnimationState().streamingTextAccentInitialAlpha(accent, nowMs = 2_000))
    assertEquals(0f, MarkdownAnimationState().streamingTextAccentInitialAlpha(accent, nowMs = 3_000))
  }

  @Test
  fun additionsUseImmutableAppendedRanges() {
    val accentedEndByAnimationIndex = mutableMapOf<Int, Int>()

    assertEquals(
      listOf(
        StreamingTextAccentAddition(0, StreamingTextAccentRange(0, 4)),
        StreamingTextAccentAddition(4, StreamingTextAccentRange(4, 13)),
      ),
      streamingTextAccentAdditions(
        phraseSegments = listOf(0, 4, 13),
        renderedEnd = 13,
        accentedEndByAnimationIndex = accentedEndByAnimationIndex,
      ),
    )
    assertEquals(
      listOf(
        StreamingTextAccentAddition(13, StreamingTextAccentRange(13, 20)),
      ),
      streamingTextAccentAdditions(
        phraseSegments = listOf(0, 4, 13, 20),
        renderedEnd = 20,
        accentedEndByAnimationIndex = accentedEndByAnimationIndex,
      ),
    )
  }

  @Test
  fun additionsTrackGrowthWithinAnUnfinishedPhrase() {
    val accentedEndByAnimationIndex = mutableMapOf<Int, Int>()

    assertEquals(
      listOf(
        StreamingTextAccentAddition(0, StreamingTextAccentRange(0, 2)),
      ),
      streamingTextAccentAdditions(
        phraseSegments = listOf(0),
        renderedEnd = 2,
        accentedEndByAnimationIndex = accentedEndByAnimationIndex,
      ),
    )
    assertEquals(
      listOf(
        StreamingTextAccentAddition(0, StreamingTextAccentRange(2, 3)),
      ),
      streamingTextAccentAdditions(
        phraseSegments = listOf(0),
        renderedEnd = 3,
        accentedEndByAnimationIndex = accentedEndByAnimationIndex,
      ),
    )
  }
}
