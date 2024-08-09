package com.halilibo.richtext.ui.string

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.invalidateGroupsWithKey
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import com.halilibo.richtext.ui.ClickableText
import com.halilibo.richtext.ui.RichTextScope
import com.halilibo.richtext.ui.currentContentColor
import com.halilibo.richtext.ui.currentRichTextStyle
import com.halilibo.richtext.ui.string.RichTextString.Format
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Renders a [RichTextString] as created with [richTextString].
 *
 * @sample com.halilibo.richtext.ui.previews.TextPreview
 */
@OptIn(FlowPreview::class)
@Composable
public fun RichTextScope.Text(
  text: RichTextString,
  modifier: Modifier = Modifier,
  onTextLayout: (TextLayoutResult) -> Unit = {},
  softWrap: Boolean = true,
  isLeafText: Boolean = true,
  animate: Boolean = false,
  textFadeInMs: Int = 600,
  debounceMs: Int = 150,
  overflow: TextOverflow = TextOverflow.Clip,
  maxLines: Int = Int.MAX_VALUE
) {
  val style = currentRichTextStyle.stringStyle
  val contentColor = currentContentColor
  val coroutineScope = rememberCoroutineScope()
  val annotated = remember(text, style, contentColor) {
    val resolvedStyle = (style ?: RichTextStringStyle.Default).resolveDefaults()
    text.toAnnotatedString(resolvedStyle, contentColor)
  }
  val animations = remember { mutableStateMapOf<Int, TextAnimation>() }
  val textToRender = remember { mutableStateOf(AnnotatedString("")) }

  if (animate) {
    val lastAnimatedIndex = remember { mutableIntStateOf(0) }
    val readyToAnimateText = remember { mutableStateOf(AnnotatedString("")) }
    // In case no changes happen for a while, we'll render after some timeout
    val debouncedTextFlow = remember { MutableStateFlow(AnnotatedString("")) }
    val debouncedText by remember { debouncedTextFlow.debounce(debounceMs.milliseconds) }
      .collectAsState(AnnotatedString(""), coroutineScope.coroutineContext)

    LaunchedEffect(text) {
      debouncedTextFlow.value = annotated
      // If we detect a new phrase, kick off the animation now.
      if (annotated.hasNewPhraseFrom(textToRender.value.text)) {
        readyToAnimateText.value = annotated
       }
    }
    LaunchedEffect(isLeafText) {
      if (!isLeafText) {
        readyToAnimateText.value = annotated
      }
    }
    LaunchedEffect(debouncedText) {
      if (debouncedText.text.isNotEmpty()) {
        readyToAnimateText.value = debouncedText
      }
    }

    LaunchedEffect(readyToAnimateText.value) {
      if (readyToAnimateText.value.text.length >= lastAnimatedIndex.value) {
        // TODO: split this into phrases.
        val animationIndex = lastAnimatedIndex.value
        lastAnimatedIndex.value = readyToAnimateText.value.text.length
        animations[animationIndex] = TextAnimation(animationIndex, 0f)
        coroutineScope.launch {
          textToRender.value = readyToAnimateText.value
          Animatable(0f).animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = textFadeInMs)
          ) {
            animations[animationIndex] = TextAnimation(animationIndex, value)
          }
          animations.remove(animationIndex)
        }
      } else {
        textToRender.value = readyToAnimateText.value
      }
    }
  } else {
    // If we're not animating, just render the text as is.
    textToRender.value = annotated
  }

  val inlineContents = remember(text) { text.getInlineContents() }

  // Ignore animated text if we have inline content, since it causes crashes.
  val animatedText = if (inlineContents.isEmpty()) {
    textToRender.value.animateAlphas(animations.values, contentColor)
  } else {
    annotated
  }

  BoxWithConstraints(modifier = modifier) {
    val inlineTextContents = manageInlineTextContents(
      inlineContents = inlineContents,
      textConstraints = constraints,
    )

    ClickableText(
      text = animatedText,
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

private data class TextAnimation(val startIndex: Int, val alpha: Float)

private fun AnnotatedString.animateAlphas(animations: Collection<TextAnimation>, contentColor: Color): AnnotatedString {
  if (this.text.isEmpty() || animations.isEmpty()) {
    return this
  }
  var remainingText = this
  val modifiedTextSnippets = mutableStateListOf<AnnotatedString>()
  animations.sortedByDescending { it.startIndex }.forEach { animation ->
    if (animation.startIndex < remainingText.length) {
      modifiedTextSnippets.add(
        remainingText.subSequence(animation.startIndex, remainingText.length)
          .changeAlpha(animation.alpha, contentColor)
      )
      remainingText = remainingText.subSequence(0, animation.startIndex)
    } else {
    }
  }
  return AnnotatedString.Builder(remainingText).apply {
    modifiedTextSnippets.reversed().forEach { append(it) }
  }.toAnnotatedString()
//  return modifiedTextSnippets.reversed().fold(remainingText) { acc, snippet -> acc + snippet }
}

private fun AnnotatedString.changeAlpha(alpha: Float, contentColor: Color): AnnotatedString {
  val newWordsStyles = spanStyles.map { spanstyle ->
    spanstyle.copy(item = spanstyle.item.copy(color = spanstyle.item.color.copy(alpha = alpha)))
  }.ifEmpty {
    listOf(AnnotatedString.Range(SpanStyle(contentColor.copy(alpha = alpha)), 0, length))
  }
  return AnnotatedString(text, newWordsStyles)
}

// Given a string, finds all the "phrases" (sentences, commas, and any word sequences longer than 5 words)
// and returns a list of all their starting indexes
private fun AnnotatedString.findPhrases(): List<Int> {
  val phrases = mutableListOf<Int>()
  var lastPhraseStart = 0
  var wordCount = 0
  forEachIndexed { index, char ->
    if (char == '.' || char == ',') {
      if (index - lastPhraseStart > 5) {
        phrases.add(lastPhraseStart)
        wordCount = 0
      }
      lastPhraseStart = index + 1
    }
    if (char == ' ') {
      wordCount++
      if (wordCount > 5 && index - lastPhraseStart > 10) {
        phrases.add(lastPhraseStart)
        wordCount = 0
      }
    }
  }
  return phrases
}

private fun AnnotatedString.hasNewPhraseFrom(rendered: String): Boolean {
  if (rendered.count { it == ',' } != this.count { it == ',' }) {
    return true
  }
  if (rendered.count { it == '.' } != this.count { it == '.' }) {
    return true
  }
  if (this.count { it == ' ' } - rendered.count { it == ' ' } > 4) {
    return true
  }
  return false
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
