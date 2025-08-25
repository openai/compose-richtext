package com.halilibo.richtext.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.halilibo.richtext.ui.string.MarkdownAnimationState
import com.halilibo.richtext.ui.string.RichTextRenderOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun rememberMarkdownFade(
  richTextRenderOptions: RichTextRenderOptions,
  markdownAnimationState: MarkdownAnimationState,
): State<Float> {
  val coroutineScope = rememberCoroutineScope()
  val targetAlpha = remember {
    mutableFloatStateOf(if (richTextRenderOptions.animate) 0f else 1f)
  }
  LaunchedEffect(Unit) {
    if (richTextRenderOptions.animate) {
      coroutineScope.launch {
        markdownAnimationState.addAnimation(richTextRenderOptions)
        delay(markdownAnimationState.toDelayMs().milliseconds)
        targetAlpha.floatValue = 1f
      }
    }
  }
  val alpha = animateFloatAsState(
    targetAlpha.floatValue,
    tween(
      durationMillis = richTextRenderOptions.textFadeInMs,
    )
  )
  return alpha
}
