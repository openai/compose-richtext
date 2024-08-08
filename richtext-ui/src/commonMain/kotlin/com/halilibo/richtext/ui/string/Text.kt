package com.halilibo.richtext.ui.string

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.halilibo.richtext.ui.ClickableText
import com.halilibo.richtext.ui.RichTextScope
import com.halilibo.richtext.ui.currentContentColor
import com.halilibo.richtext.ui.currentRichTextStyle
import com.halilibo.richtext.ui.string.RichTextString.Format
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import java.io.Console
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Renders a [RichTextString] as created with [richTextString].
 *
 * @sample com.halilibo.richtext.ui.previews.TextPreview
 */
@Composable
public fun RichTextScope.Text(
  text: RichTextString,
  modifier: Modifier = Modifier,
  onTextLayout: (TextLayoutResult) -> Unit = {},
  softWrap: Boolean = true,
  overflow: TextOverflow = TextOverflow.Clip,
  maxLines: Int = Int.MAX_VALUE
) {
  val style = currentRichTextStyle.stringStyle
  val contentColor = currentContentColor
  val annotated = remember(text, style, contentColor) {
    val resolvedStyle = (style ?: RichTextStringStyle.Default).resolveDefaults()
    text.toAnnotatedString(resolvedStyle, contentColor)
  }

  val rendered = remember { mutableStateOf("") }
  val debouncedTextFlow = remember { MutableStateFlow(AnnotatedString("")) }
  val coroutineScope = rememberCoroutineScope()
  val debouncedText by remember { debouncedTextFlow.sample(300.milliseconds) }
    .collectAsState(AnnotatedString(""), coroutineScope.coroutineContext)

  LaunchedEffect(text) {
    debouncedTextFlow.value = annotated
  }

  val textSlices = remember { mutableStateListOf(AnnotatedString("")) }
  LaunchedEffect(debouncedText) {
    // log the state
    if (debouncedText.length > rendered.value.length) {
      val newWords = debouncedText.subSequence(rendered.value.length, debouncedText.length)
      rendered.value += newWords.text
      val sliceIndex = textSlices.size
      textSlices.add(newWords)
      coroutineScope.launch {
        Animatable(0f).animateTo(
          targetValue = 1f,
          animationSpec = tween(durationMillis = 1000)
        ) {
          val newWordsStyles = newWords.spanStyles.map { spanstyle ->
            spanstyle.copy(item = spanstyle.item.copy(color = spanstyle.item.color.copy(alpha = value)))
          }.ifEmpty {
            listOf(
              AnnotatedString.Range(
                SpanStyle(contentColor.copy(alpha = value)),
                0,
                newWords.length
              )
            )
          }
          val animatedNewWords = AnnotatedString(newWords.text, newWordsStyles)
          textSlices[sliceIndex] = animatedNewWords
        }
      }
    }
  }

  val inlineContents = remember(text) { text.getInlineContents() }

  BoxWithConstraints(modifier = modifier) {
    val inlineTextContents = manageInlineTextContents(
      inlineContents = inlineContents,
      textConstraints = constraints,
    )

    ClickableText(
      text = textSlices.toList().reduce { acc, annotatedString -> acc + annotatedString },
      onTextLayout = onTextLayout,
      inlineContent = inlineTextContents,
      softWrap = softWrap,
      overflow = overflow,
      maxLines = maxLines,
      isOffsetClickable = { offset ->
        // When you click past the end of the string, the offset is where the caret should be
        // placed. However, when it is at the end, offset == text.length but parent links will at
        // most end at length - 1. So we need to coerce the offset to be at most length - 1.
        // This fixes an image where only the left side of an image wrapped with a link was only
        // clickable on the left side.
        // However, if a paragraph ends with a link, the link will be clickable past the
        // end of the last line.
        annotated.getConsumableAnnotations(text.formatObjects, offset.coerceAtMost(annotated.length - 1)).any()
      },
      onClick = { offset ->
        annotated.getConsumableAnnotations(text.formatObjects, offset.coerceAtMost(annotated.length - 1))
          .firstOrNull()
          ?.let { link -> link.onClick() }
      }
    )
  }
}

private fun AnnotatedString.getConsumableAnnotations(textFormatObjects: Map<String, Any>, offset: Int): Sequence<Format.Link> =
  getStringAnnotations(Format.FormatAnnotationScope, offset, offset)
    .asSequence()
    .mapNotNull {
      Format.findTag(
        it.item,
        textFormatObjects
      ) as? Format.Link
    }
