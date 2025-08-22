package com.halilibo.richtext.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import com.halilibo.richtext.ui.string.MarkdownAnimationState
import com.halilibo.richtext.ui.string.RichTextRenderOptions

@Composable
internal fun rememberMarkdownFade(
  richTextRenderOptions: RichTextRenderOptions,
  markdownAnimationState: MarkdownAnimationState,
  enqueue: Boolean = false,
): State<Float> {
  val targetAlpha = remember {
    mutableFloatStateOf(if (richTextRenderOptions.animate) 0f else 1f)
  }
  val alpha = animateFloatAsState(
    targetAlpha.value,
    tween(
      durationMillis = richTextRenderOptions.textFadeInMs,
      delayMillis = markdownAnimationState.toDelayMs(),
    )
  )
  LaunchedEffect(Unit) {
    if (enqueue) {
      markdownAnimationState.addAnimation(richTextRenderOptions)
    }
    targetAlpha.value = 1f
  }
  return alpha
}
