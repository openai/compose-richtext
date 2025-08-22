package com.halilibo.richtext.ui

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.halilibo.richtext.ui.string.MarkdownAnimationState
import com.halilibo.richtext.ui.string.RichTextRenderOptions
import kotlinx.coroutines.launch

@Composable
internal fun rememberMarkdownFade(
  richTextRenderOptions: RichTextRenderOptions,
  markdownAnimationState: MarkdownAnimationState,
  enqueue: Boolean = true,
): State<Float> {
  val coroutineScope = rememberCoroutineScope()
  val alpha = remember { mutableFloatStateOf(if (richTextRenderOptions.animate) 0f else 1f) }
  LaunchedEffect(Unit) {
    if (enqueue) {
      coroutineScope.launch {
        markdownAnimationState.addAnimation(richTextRenderOptions)
        animate(
          initialValue = 0f,
          targetValue = 1f,
          animationSpec = tween(
            durationMillis = richTextRenderOptions.textFadeInMs,
            delayMillis = markdownAnimationState.toDelayMs(),
          )
        ) { value, _ ->
          alpha.floatValue = value
        }
      }
    } else {
      alpha.floatValue = 1f
    }
  }
  return alpha
}
