package com.halilibo.richtext.ui.string

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.halilibo.richtext.ui.RichTextScope
import com.halilibo.richtext.ui.Text
import com.halilibo.richtext.ui.currentContentColor
import com.halilibo.richtext.ui.currentRichTextStyle
import com.halilibo.richtext.ui.string.RichTextString.Format
import com.halilibo.richtext.ui.util.PhraseAnnotatedString
import com.halilibo.richtext.ui.util.segmentIntoPhrases
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

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
  isLeafText: Boolean = true,
  renderOptions: RichTextRenderOptions = RichTextRenderOptions(),
  sharedAnimationState: MarkdownAnimationState = remember { MarkdownAnimationState() },
  decorations: RichTextDecorations = RichTextDecorations(),
  overflow: TextOverflow = TextOverflow.Clip,
  maxLines: Int = Int.MAX_VALUE,
  textAlign: TextAlign? = null,
  textDirection: TextDirection? = null,
) {
  val style = currentRichTextStyle.stringStyle
  val contentColor = currentContentColor
  val resolvedStyle = remember(style) {
    (style ?: RichTextStringStyle.Default).resolveDefaults()
  }
  val annotated = remember(text, resolvedStyle, contentColor, decorations) {
    text.toAnnotatedString(resolvedStyle, contentColor, decorations)
  }
  val baseInlineContents = remember(text) { text.getInlineContents() }
  val resolvedLinkDecorations = remember(text, decorations) {
    text.resolveLinkDecorations(decorations)
  }
  val hasInlineIcons = remember(resolvedLinkDecorations) {
    resolvedLinkDecorations.any { it.hasInlineContent() }
  }
  val decoratedTextResult = remember(
    annotated,
    baseInlineContents,
    resolvedLinkDecorations,
    hasInlineIcons,
  ) {
    if (hasInlineIcons) {
      decorateAnnotatedStringWithLinkIcons(
        annotated = annotated,
        baseInlineContents = baseInlineContents,
        linkDecorations = resolvedLinkDecorations,
      )
    } else {
      DecoratedTextResult(
        annotatedString = annotated,
        inlineContents = baseInlineContents,
        decoratedLinkRanges = resolvedLinkDecorations
          .filter { it.underlineStyle !is UnderlineStyle.Solid }
          .map { range ->
            DecoratedLinkRange(
              start = range.start,
              end = range.end,
              destination = range.destination,
              underlineStyle = range.underlineStyle,
              underlineColor = range.underlineColor,
              linkStyleOverride = range.linkStyleOverride,
            )
          },
      )
    }
  }
  val inlineContents = decoratedTextResult.inlineContents
  val decoratedLinkRanges = decoratedTextResult.decoratedLinkRanges
  var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
  val underlineSpecs = remember(decoratedLinkRanges, resolvedStyle, contentColor) {
    decoratedLinkRanges.mapNotNull { range ->
      val linkStyle = range.linkStyleOverride
        ?.invoke(resolvedStyle.linkStyle)
        ?: resolvedStyle.linkStyle
      val underlineColor = range.underlineColor
        ?: linkStyle?.style?.color
          ?.takeIf { it.isSpecified }
        ?: contentColor
      UnderlineSpec(
        range = range,
        color = underlineColor,
      )
    }
  }
  val animatedResult = if (renderOptions.animate && inlineContents.isEmpty()) {
    rememberAnimatedTextResult(
      annotated = decoratedTextResult.annotatedString,
      contentColor = contentColor,
      renderOptions = renderOptions,
      isLeafText = isLeafText,
      sharedAnimationState = sharedAnimationState,
    )
  } else {
    null
  }
  val animatedText = animatedResult?.text ?: decoratedTextResult.annotatedString
  val paragraphStyledText = remember(animatedText, textAlign, textDirection) {
    applyParagraphStyle(
      text = animatedText,
      textAlign = textAlign,
      textDirection = textDirection,
    )
  }
  val underlineAlphaForOffset = animatedResult?.alphaForOffset
  val accentTextMeasurer = rememberTextMeasurer(cacheSize = 1)
  val accentOverlayText = remember(
    animatedResult?.accentOverlayText,
    textAlign,
    textDirection,
  ) {
    animatedResult?.accentOverlayText
      ?.let { applyParagraphStyle(it, textAlign, textDirection) }
  }
  val accentTextLayoutResult = remember(accentOverlayText, textLayoutResult) {
    val layoutInput = textLayoutResult?.layoutInput ?: return@remember null
    accentOverlayText?.let { text ->
      accentTextMeasurer.measure(
        text = text,
        style = layoutInput.style,
        overflow = layoutInput.overflow,
        softWrap = layoutInput.softWrap,
        maxLines = layoutInput.maxLines,
        placeholders = layoutInput.placeholders,
        constraints = layoutInput.constraints,
        layoutDirection = layoutInput.layoutDirection,
        density = layoutInput.density,
        fontFamilyResolver = layoutInput.fontFamilyResolver,
      )
    }
  }
  val streamingTextAccentModifier = Modifier.streamingTextAccentOverlay(
    accentColor = renderOptions.streamingTextAccent?.color,
    textLayoutResult = { accentTextLayoutResult },
    animations = animatedResult?.streamingTextAccentAnimations.orEmpty(),
  )

  val underlineModifier = if (underlineSpecs.isNotEmpty()) {
    Modifier.drawWithContent {
      drawContent()
      val layoutResult = textLayoutResult ?: return@drawWithContent
      underlineSpecs.fastForEach { spec ->
        drawUnderline(
          layoutResult = layoutResult,
          start = spec.range.start,
          end = spec.range.end,
          underlineStyle = spec.range.underlineStyle,
          color = spec.color,
          alphaForOffset = underlineAlphaForOffset,
        )
      }
    }
  } else {
    Modifier
  }

  if (inlineContents.isEmpty()) {
    Text(
      text = paragraphStyledText,
      onTextLayout = { layoutResult ->
        textLayoutResult = layoutResult
        onTextLayout(layoutResult)
      },
      softWrap = softWrap,
      overflow = overflow,
      maxLines = maxLines,
      modifier = modifier.then(underlineModifier).then(streamingTextAccentModifier),
    )
  } else {
    val inlineTextConstraints = remember { mutableStateOf(Constraints()) }
    val inlineTextContents = manageInlineTextContents(
      inlineContents = inlineContents,
      textConstraints = inlineTextConstraints,
    )

    Text(
      text = paragraphStyledText,
      onTextLayout = { layoutResult ->
        textLayoutResult = layoutResult
        onTextLayout(layoutResult)
      },
      inlineContent = inlineTextContents,
      softWrap = softWrap,
      overflow = overflow,
      maxLines = maxLines,
      modifier = modifier.then(underlineModifier).then(streamingTextAccentModifier).layout { measurable, constraints ->
        // Prepares the custom constraints InlineTextContents before they get measured.
        inlineTextConstraints.value = constraints.copy(minWidth = 0, minHeight = 0)
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
          placeable.place(0, 0)
        }
      },
    )
  }
}

internal fun applyParagraphStyle(
  text: AnnotatedString,
  textAlign: TextAlign?,
  textDirection: TextDirection?,
): AnnotatedString {
  if (textAlign == null && textDirection == null) return text

  val paragraphStyle = ParagraphStyle(
    textAlign = textAlign ?: TextAlign.Unspecified,
    textDirection = textDirection ?: TextDirection.Unspecified,
  )

  return buildAnnotatedString {
    append(text)
    addStyle(paragraphStyle, 0, text.length)
  }
}

private data class UnderlineSpec(
  val range: DecoratedLinkRange,
  val color: Color,
)

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawUnderline(
  layoutResult: TextLayoutResult,
  start: Int,
  end: Int,
  underlineStyle: UnderlineStyle,
  color: Color,
  alphaForOffset: ((Int) -> Float)?,
) {
  val textLength = layoutResult.layoutInput.text.text.length
  val clampedStart = start.coerceIn(0, textLength)
  val clampedEnd = end.coerceIn(0, textLength)
  if (clampedStart >= clampedEnd) return

  val strokeWidthPx: Float
  val offsetPx: Float
  val pathEffect: PathEffect?
  val cap: StrokeCap

  with(this) {
    when (underlineStyle) {
      is UnderlineStyle.Solid -> {
        strokeWidthPx = 1.dp.toPx()
        offsetPx = 0.dp.toPx()
        pathEffect = null
        cap = StrokeCap.Butt
      }
      is UnderlineStyle.Dotted -> {
        strokeWidthPx = underlineStyle.strokeWidth.toPx()
        offsetPx = underlineStyle.offset.toPx()
        val gapPx = underlineStyle.gap.toPx()
        // Round-capped zero-length dashes render as true circular dots.
        val patternGapPx = (gapPx + strokeWidthPx).coerceAtLeast(1f)
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(0f, patternGapPx), 0f)
        cap = StrokeCap.Round
      }
      is UnderlineStyle.Dashed -> {
        strokeWidthPx = underlineStyle.strokeWidth.toPx()
        offsetPx = underlineStyle.offset.toPx()
        val dashPx = underlineStyle.dash.toPx()
        val gapPx = underlineStyle.gap.toPx()
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashPx, gapPx), 0f)
        cap = StrokeCap.Butt
      }
    }
  }

  val startLine = layoutResult.getLineForOffset(clampedStart)
  val endLine = layoutResult.getLineForOffset(clampedEnd - 1)
  for (line in startLine..endLine) {
    val lineStart = layoutResult.getLineStart(line)
    val lineEnd = layoutResult.getLineEnd(line, visibleEnd = true)
    val segmentStart = maxOf(clampedStart, lineStart)
    val segmentEnd = minOf(clampedEnd, lineEnd)
    if (segmentEnd <= segmentStart) continue

    val startBox = layoutResult.getBoundingBox(segmentStart)
    val endBox = layoutResult.getBoundingBox(segmentEnd - 1)
    val y = layoutResult.getLineBaseline(line) + strokeWidthPx + offsetPx
    val xStart = startBox.left.roundToInt().toFloat()
    val xEnd = endBox.right.roundToInt().toFloat()

    val alpha = alphaForOffset?.invoke(segmentEnd - 1)?.coerceIn(0f, 1f) ?: 1f
    if (alpha <= 0f) continue
    drawLine(
      color = color.copy(alpha = color.alpha * alpha),
      start = Offset(xStart, y),
      end = Offset(xEnd, y),
      strokeWidth = strokeWidthPx,
      cap = cap,
      pathEffect = pathEffect,
    )
  }
}

@Stable
public class MarkdownAnimationState {

  private var lastAnimationStartTimeMark by mutableStateOf<ComparableTimeMark?>(null)

  public fun addAnimation(renderOptions: RichTextRenderOptions) {
    lastAnimationStartTimeMark = monotonicTimeMark() + calculatedDelay(renderOptions).milliseconds
  }

  private fun calculatedDelay(renderOptions: RichTextRenderOptions): Long {
    val diffMs = lastAnimationStartTimeMark?.millisecondsFromNow() ?: return 0

    return when {
      diffMs < -renderOptions.delayMs -> 0 // We are past the last animation, so launch it now.
      diffMs <= 0 -> renderOptions.delayMs - diffMs
      else -> diffMs + (renderOptions.delayMs * (renderOptions.delayMs / diffMs.toDouble()).pow(
        renderOptions.delayExponent
      )).toLong()
    }
  }

  public fun toDelayMs(): Int =
    lastAnimationStartTimeMark?.millisecondsUntilNow()?.toInt() ?: 0

  internal fun streamingTextAccentInitialAlpha(
    accent: StreamingTextAccent,
    now: ComparableTimeMark = monotonicTimeMark(),
  ): Float =
    (1f - (now - accent.decayStartTimeMark).inWholeMilliseconds /
      accent.decayDurationMs.toFloat()).coerceIn(0f, 1f)
}

private data class AnimatedTextResult(
  val text: AnnotatedString,
  val accentOverlayText: AnnotatedString,
  val alphaForOffset: (Int) -> Float,
  val streamingTextAccentAnimations: Map<Int, StreamingTextAccentAnimation>,
)

@Composable
private fun rememberAnimatedTextResult(
  annotated: AnnotatedString,
  renderOptions: RichTextRenderOptions,
  contentColor: Color,
  sharedAnimationState: MarkdownAnimationState,
  isLeafText: Boolean,
): AnimatedTextResult {
  val coroutineScope = rememberCoroutineScope()
  val animations = remember { mutableStateMapOf<Int, TextAnimation>() }
  val streamingTextAccentAnimations = remember {
    mutableStateMapOf<Int, StreamingTextAccentAnimation>()
  }
  val nextStreamingTextAccentAnimationId = remember { mutableIntStateOf(0) }
  val animationStartTimeMarksByIndex = remember { mutableMapOf<Int, ComparableTimeMark>() }
  val accentedEndByAnimationIndex = remember { mutableMapOf<Int, Int>() }
  val textToRender = remember { mutableStateOf(AnnotatedString("")) }

  val lastAnimationIndex = remember { mutableIntStateOf(-1) }
  val lastPhrases = remember { mutableStateOf(PhraseAnnotatedString()) }
  val lastStreamingTextAccent = remember { mutableStateOf<StreamingTextAccent?>(null) }
  val addStreamingTextAccentAnimation = addAccent@{
    start: Int,
    end: Int,
    startTimeMark: ComparableTimeMark,
    admissionTimeMark: ComparableTimeMark ->
    val accent = renderOptions.streamingTextAccent ?: return@addAccent
    // Decay admission when text becomes accent-eligible, not when delayed reveal begins.
    val initialAlpha = sharedAnimationState.streamingTextAccentInitialAlpha(accent, admissionTimeMark)
    if (initialAlpha <= 0f) return@addAccent
    val elapsedMs = startTimeMark.elapsedMilliseconds().toInt()
    if (elapsedMs >= accent.fadeOutMs) return@addAccent

    if (streamingTextAccentAnimations.size >= MaximumStreamingTextAccentAnimationCount) {
      streamingTextAccentAnimations.keys.minOrNull()?.let(streamingTextAccentAnimations::remove)
    }
    val id = nextStreamingTextAccentAnimationId.intValue++
    val animation = StreamingTextAccentAnimation(
      start = start,
      end = end,
      fadeOutMs = accent.fadeOutMs,
      excludesEmoji = accent.excludesEmoji,
      initialAlpha = initialAlpha,
      elapsedMs = elapsedMs.toFloat(),
      hasStarted = startTimeMark.hasPassedNow(),
    )
    streamingTextAccentAnimations[id] = animation
    coroutineScope.launch {
      delay(startTimeMark.millisecondsUntilNow().milliseconds)
      animation.hasStarted = true
      animation.elapsedMs = startTimeMark.elapsedMilliseconds().toFloat()
      val remainingDurationMs = (animation.durationMs - animation.elapsedMs.toInt()).coerceAtLeast(0)
      animate(
        initialValue = animation.elapsedMs,
        targetValue = animation.durationMs.toFloat(),
        animationSpec = tween(
          durationMillis = remainingDurationMs,
          easing = LinearEasing,
        ),
      ) { value, _ ->
        animation.elapsedMs = value
      }
      streamingTextAccentAnimations.remove(id)
    }
  }
  val addStreamingTextAccentAnimations = addAccents@{
    phrases: PhraseAnnotatedString,
    admissionTimeMark: ComparableTimeMark ->
    if (renderOptions.streamingTextAccent == null) return@addAccents
    streamingTextAccentAdditions(
      phraseSegments = phrases.phraseSegments,
      renderedEnd = textToRender.value.length,
      accentedEndByAnimationIndex = accentedEndByAnimationIndex,
    ).forEach { addition ->
      animationStartTimeMarksByIndex[addition.animationIndex]?.let { startTimeMark ->
        addStreamingTextAccentAnimation(
          addition.range.start,
          addition.range.end,
          startTimeMark,
          admissionTimeMark,
        )
      }
    }
  }
  val updatePhrases = { phrases: PhraseAnnotatedString ->
    lastPhrases.value = phrases
    textToRender.value = phrases.makeCompletePhraseString(!isLeafText)
    phrases.phraseSegments
      .filter { it > lastAnimationIndex.intValue }
      .forEach { phraseIndex ->
        val animation = TextAnimation(phraseIndex)
        animations[phraseIndex] = animation
        lastAnimationIndex.intValue = phraseIndex
        sharedAnimationState.addAnimation(renderOptions)
        val delayMs = sharedAnimationState.toDelayMs()
        animationStartTimeMarksByIndex[phraseIndex] = monotonicTimeMark() + delayMs.milliseconds
        coroutineScope.launch {
          var hasAnimationFired = false
          animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = tween(
              durationMillis = renderOptions.textFadeInMs,
              delayMillis = delayMs,
            )
          ) { value, _ ->
            animation.alpha = value
            if (!hasAnimationFired) {
              renderOptions.onPhraseAnimate()
              hasAnimationFired = true
            } else {
              renderOptions.onTextAnimate()
            }
          }
          // Remove animation right away, in case it had split at an inappropriate unicode point.
          animations.remove(phraseIndex)
        }
      }
    addStreamingTextAccentAnimations(phrases, monotonicTimeMark())
  }
  LaunchedEffect(isLeafText, annotated, renderOptions.streamingTextAccent) {
    val shouldAccentExistingText =
      lastStreamingTextAccent.value == null && renderOptions.streamingTextAccent != null
    lastStreamingTextAccent.value = renderOptions.streamingTextAccent
    val isComplete = !isLeafText
    // If we detect a new phrase, kick off the animation now.
    val phrases = annotated.segmentIntoPhrases(renderOptions, isComplete = isComplete)
    val hasNewPhrases = if (isComplete) {
      phrases != lastPhrases.value
    } else {
      phrases.hasNewPhrasesFrom(lastPhrases.value)
    }
    if (hasNewPhrases) {
      updatePhrases(phrases)
    } else if (shouldAccentExistingText) {
      addStreamingTextAccentAnimations(phrases, monotonicTimeMark())
    } else {
      return@LaunchedEffect
    }

    if (!isComplete) {
      // In case no changes happen for a while, we'll render after some timeout
      delay(renderOptions.debounceMs.milliseconds)
      if (annotated.text.isNotEmpty()) {
        val debouncedPhrases = annotated.segmentIntoPhrases(renderOptions, isComplete = true)
        if (debouncedPhrases != lastPhrases.value) {
          updatePhrases(debouncedPhrases)
        }
      }
    }
  }

  // contentColor rarely changes, and it's not already a State. When contentColor changes, a new
  // AnnotatedString must be created using the updated contentColor value.
  val text = remember(contentColor) {
    // textToRender and the set of animations are tracked as States, and trigger the derivedStateOf
    // to return a new value in order to create a new AnnotatedString with the latest text and
    // animated Brushes.
    // This will only return a new value if the actual text changes, or the *set* of animated spans
    // has changed.
    // When a span gets a new animated value, the AnnotatedString will not be updated. Instead,
    // the text will just be re-drawn, since the animated alpha state was read only inside
    // DynamicSolidColor during the draw phase.
    derivedStateOf {
      textToRender.value.withDynamicColorPhrases(
        contentColor = contentColor,
        animations = animations.values,
        onlyVisible = renderOptions.onlyRenderVisibleText,
      )
    }
  }.value

  return AnimatedTextResult(
    text = text,
    accentOverlayText = textToRender.value,
    alphaForOffset = { offset ->
      var bestStart = -1
      var bestAlpha = 1f
      animations.values.forEach { animation ->
        if (animation.startIndex <= offset && animation.startIndex > bestStart) {
          bestStart = animation.startIndex
          bestAlpha = animation.alpha
        }
      }
      bestAlpha
    },
    streamingTextAccentAnimations = streamingTextAccentAnimations,
  )
}

private class TextAnimation(val startIndex: Int) {

  var alpha by mutableFloatStateOf(0f)
  val isVisible by derivedStateOf { alpha > 0f }
}

private class StreamingTextAccentAnimation(
  val start: Int,
  val end: Int,
  val fadeOutMs: Int,
  val excludesEmoji: Boolean,
  private val initialAlpha: Float,
  elapsedMs: Float,
  hasStarted: Boolean,
) {
  var hasStarted by mutableStateOf(hasStarted)
  var elapsedMs by mutableFloatStateOf(elapsedMs)
  val durationMs = fadeOutMs

  fun alpha(): Float {
    if (!hasStarted) return 0f
    val progress = (elapsedMs / fadeOutMs).coerceIn(0f, 1f)
    return initialAlpha * (1f - StreamingTextAccentFadeEasing.transform(progress))
  }
}

internal data class StreamingTextAccentRange(
  val start: Int,
  val end: Int,
)

internal data class StreamingTextAccentAddition(
  val animationIndex: Int,
  val range: StreamingTextAccentRange,
)

internal fun streamingTextAccentAdditions(
  phraseSegments: List<Int>,
  renderedEnd: Int,
  accentedEndByAnimationIndex: MutableMap<Int, Int>,
): List<StreamingTextAccentAddition> = buildList {
  phraseSegments.forEachIndexed { index, animationIndex ->
    val end = minOf(phraseSegments.getOrNull(index + 1) ?: renderedEnd, renderedEnd)
    val accentedEnd = accentedEndByAnimationIndex[animationIndex] ?: animationIndex
    if (accentedEnd < end) {
      add(
        StreamingTextAccentAddition(
          animationIndex = animationIndex,
          range = StreamingTextAccentRange(accentedEnd, end),
        )
      )
      accentedEndByAnimationIndex[animationIndex] = end
    }
  }
}

internal fun AnnotatedString.streamingTextAccentRanges(
  start: Int,
  end: Int,
  excludesEmoji: Boolean,
): List<StreamingTextAccentRange> {
  val clampedStart = start.coerceIn(0, length)
  val clampedEnd = end.coerceIn(0, length)
  if (clampedStart >= clampedEnd) return emptyList()

  var ranges = listOf(StreamingTextAccentRange(clampedStart, clampedEnd))
  getLinkAnnotations(clampedStart, clampedEnd).forEach { link ->
    ranges = ranges.excluding(
      StreamingTextAccentRange(
        start = link.start.coerceAtLeast(clampedStart),
        end = link.end.coerceAtMost(clampedEnd),
      )
    )
  }
  return if (excludesEmoji) {
    ranges.flatMap(text::nonEmojiRanges)
  } else {
    ranges
  }
}

private fun List<StreamingTextAccentRange>.excluding(
  excluded: StreamingTextAccentRange,
): List<StreamingTextAccentRange> = flatMap { range ->
  when {
    excluded.end <= range.start || excluded.start >= range.end -> listOf(range)
    else -> buildList {
      if (range.start < excluded.start) {
        add(StreamingTextAccentRange(range.start, excluded.start))
      }
      if (excluded.end < range.end) {
        add(StreamingTextAccentRange(excluded.end, range.end))
      }
    }
  }
}

private fun String.nonEmojiRanges(
  range: StreamingTextAccentRange,
): List<StreamingTextAccentRange> {
  val ranges = mutableListOf<StreamingTextAccentRange>()
  var rangeStart = range.start
  var index = range.start
  while (index < range.end) {
    val emojiEnd = emojiClusterEnd(index, range.end)
    if (emojiEnd == null) {
      index += Character.charCount(Character.codePointAt(this, index))
      continue
    }
    if (rangeStart < index) {
      ranges += StreamingTextAccentRange(rangeStart, index)
    }
    index = emojiEnd
    rangeStart = emojiEnd
  }
  if (rangeStart < range.end) {
    ranges += StreamingTextAccentRange(rangeStart, range.end)
  }
  return ranges
}

private fun String.emojiClusterEnd(
  start: Int,
  end: Int,
): Int? {
  val first = Character.codePointAt(this, start)
  if (first.isKeycapBase()) {
    var index = start + Character.charCount(first)
    if (codePointAtOrNull(index, end) == VariationSelector16) {
      index += Character.charCount(VariationSelector16)
    }
    return if (codePointAtOrNull(index, end) == CombiningEnclosingKeycap) {
      index + Character.charCount(CombiningEnclosingKeycap)
    } else {
      null
    }
  }

  var index = start + Character.charCount(first)
  if (first.isRegionalIndicator()) {
    val second = codePointAtOrNull(index, end)
    return if (second?.isRegionalIndicator() == true) {
      index + Character.charCount(second)
    } else {
      index
    }
  }
  if (!first.isEmojiPresentationCodePoint() && codePointAtOrNull(index, end) != VariationSelector16) {
    return null
  }
  while (index < end) {
    val codePoint = Character.codePointAt(this, index)
    when {
      codePoint == VariationSelector16 || codePoint.isEmojiModifier() -> {
        index += Character.charCount(codePoint)
      }
      codePoint == ZeroWidthJoiner -> {
        val next = index + Character.charCount(codePoint)
        if (next >= end) return end
        index = next + Character.charCount(Character.codePointAt(this, next))
      }
      else -> return index
    }
  }
  return index
}

private fun String.codePointAtOrNull(index: Int, end: Int): Int? =
  if (index < end) Character.codePointAt(this, index) else null

private fun Int.isKeycapBase(): Boolean = this == '#'.code || this == '*'.code || this in '0'.code..'9'.code

private fun Int.isEmojiModifier(): Boolean = this in 0x1F3FB..0x1F3FF

private fun Int.isRegionalIndicator(): Boolean = this in 0x1F1E6..0x1F1FF

private fun Int.isEmojiPresentationCodePoint(): Boolean = when (this) {
  in 0x231A..0x231B,
  in 0x23E9..0x23EC,
  0x23F0,
  0x23F3,
  in 0x25FD..0x25FE,
  in 0x2614..0x2615,
  in 0x2648..0x2653,
  0x267F,
  0x2693,
  0x26A1,
  in 0x26AA..0x26AB,
  in 0x26BD..0x26BE,
  in 0x26C4..0x26C5,
  0x26CE,
  0x26D4,
  0x26EA,
  in 0x26F2..0x26F3,
  0x26F5,
  0x26FA,
  0x26FD,
  0x2705,
  in 0x270A..0x270B,
  0x2728,
  0x274C,
  0x274E,
  in 0x2753..0x2755,
  0x2757,
  in 0x2795..0x2797,
  0x27B0,
  0x27BF,
  0x1F004,
  0x1F0CF,
  0x1F18E,
  in 0x1F191..0x1F19A,
  in 0x1F1E6..0x1F1FF,
  in 0x1F201..0x1F202,
  0x1F21A,
  0x1F22F,
  in 0x1F232..0x1F23A,
  in 0x1F250..0x1F251,
  in 0x1F300..0x1FAFF,
  -> true
  else -> false
}

private fun monotonicTimeMark(): ComparableTimeMark = TimeSource.Monotonic.markNow()

private fun ComparableTimeMark.millisecondsFromNow(): Long =
  -elapsedNow().inWholeMilliseconds

private fun ComparableTimeMark.millisecondsUntilNow(): Long =
  millisecondsFromNow().coerceAtLeast(0)

private fun ComparableTimeMark.elapsedMilliseconds(): Long =
  elapsedNow().inWholeMilliseconds.coerceAtLeast(0)

private fun Modifier.streamingTextAccentOverlay(
  accentColor: Color?,
  textLayoutResult: () -> TextLayoutResult?,
  animations: Map<Int, StreamingTextAccentAnimation>,
): Modifier = if (accentColor == null) {
  this
} else {
  drawWithContent {
    drawContent()
    val layoutResult = textLayoutResult() ?: return@drawWithContent
    val text = layoutResult.layoutInput.text
    val textLength = layoutResult.layoutInput.text.length
    animations.values
      .sortedBy(StreamingTextAccentAnimation::start)
      .forEach { animation ->
        val end = minOf(animation.end, textLength)
        val alpha = animation.alpha()
        text.streamingTextAccentRanges(
          start = animation.start,
          end = end,
          excludesEmoji = animation.excludesEmoji,
        ).fastForEach { range ->
          if (alpha > 0f) {
            val start = range.start.coerceIn(0, textLength)
            val end = range.end.coerceIn(0, textLength)
            if (start < end) clipPath(layoutResult.getPathForRange(start, end)) {
              drawText(
                textLayoutResult = layoutResult,
                color = accentColor.copy(alpha = accentColor.alpha * alpha),
              )
            }
          }
        }
      }
  }
}

private fun AnnotatedString.withDynamicColorPhrases(
  contentColor: Color,
  animations: Collection<TextAnimation>,
  onlyVisible: Boolean,
): AnnotatedString {
  if (text.isEmpty() || animations.isEmpty()) {
    return this
  }
  var remainingLength = length
  val modifiedTextSnippets = mutableListOf<AnnotatedString>()
  var dropInvisible = onlyVisible
  for (animation in animations.sortedByDescending { it.startIndex }) {
    if (animation.startIndex >= remainingLength) continue
    if (!dropInvisible || animation.isVisible) {
      dropInvisible = false
      modifiedTextSnippets += subSequence(animation.startIndex, remainingLength)
        .withDynamicColor(contentColor, alpha = { animation.alpha })
    }
    remainingLength = animation.startIndex
  }
  return buildAnnotatedString {
    append(this@withDynamicColorPhrases, start = 0, end = remainingLength)
    modifiedTextSnippets.reversed().forEach { append(it) }
  }
}

private fun AnnotatedString.withDynamicColor(color: Color, alpha: () -> Float): AnnotatedString {
  val useDynamicColor = !maybeContainsEmojis()

  val subStyles = spanStyles.map {
    val style = it.item
    if (useDynamicColor) {
      it.copy(item = style.copy(brush = DynamicSolidColor(style.color) { style.alpha * alpha() }))
    } else if (style.color.isSpecified) {
      it.copy(item = style.copy(color = style.color.copy(alpha = style.color.alpha * alpha())))
    } else {
      it.copy(item = style.copy(brush = style.brush, alpha = alpha()))
    }
  }
  val fullStyle = AnnotatedString.Range(
    item = if (useDynamicColor) {
      SpanStyle(brush = DynamicSolidColor(color, alpha))
    } else {
      SpanStyle(brush = DynamicSolidColor(color) { 1f }, alpha = alpha())
    },
    start = 0,
    end = length
  )
  return AnnotatedString(text, subStyles + fullStyle)
}

private fun CharSequence.maybeContainsEmojis(): Boolean {
  var i = 0
  val n = length
  while (i < n) {
    val cp = Character.codePointAt(this, i)

    // --- Quick accepts: common emoji blocks ---
    val isEmoji = when (cp) {
      // Misc Symbols + Dingbats + arrows subset that often render as emoji
      in 0x2600..0x27BF -> true
      // Enclosed CJK (e.g., 🈶, 🈚)
      in 0x1F200..0x1F2FF -> true
      // Misc Symbols & Pictographs
      in 0x1F300..0x1F5FF -> true
      // Emoticons
      in 0x1F600..0x1F64F -> true
      // Transport & Map
      in 0x1F680..0x1F6FF -> true
      // Supplemental Symbols & Pictographs
      in 0x1F900..0x1F9FF -> true
      // Symbols & Pictographs Extended-A (newer emoji live here)
      in 0x1FA70..0x1FAFF -> true
      // Regional indicators (flags as pairs, but single is enough for "contains")
      in 0x1F1E6..0x1F1FF -> true
      // Keycap base digits/#/* (paired with VS16 + COMBINING ENCLOSING KEYCAP, but base char is fine)
      in 0x0030..0x0039, 0x0023, 0x002A -> true
      // Variation Selector-16 forces emoji presentation for some BMP symbols
      0xFE0F -> true
      else -> false
    }

    if (isEmoji) return true

    i += Character.charCount(cp)
  }
  return false
}

private fun AnnotatedString.getConsumableAnnotations(
  textFormatObjects: Map<String, Any>,
  offset: Int,
): Sequence<Format.Link> =
  getStringAnnotations(Format.FormatAnnotationScope, offset, offset)
    .asSequence()
    .mapNotNull {
      Format.findTag(
        it.item,
        textFormatObjects
      ) as? Format.Link
    }

/**
 * Custom brush allows animating the alpha of this Brush via recompositions only in the draw phase.
 */
private data class DynamicSolidColor(private val color: Color, private val alpha: () -> Float) :
  ShaderBrush() {

  override fun createShader(size: Size): Shader {
    val color = color.copy(alpha = color.alpha * alpha())
    return LinearGradientShader(Offset.Zero, Offset(size.width, size.height), listOf(color, color))
  }
}

private const val CombiningEnclosingKeycap = 0x20E3
private const val MaximumStreamingTextAccentAnimationCount = 24
private val StreamingTextAccentFadeEasing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
private const val VariationSelector16 = 0xFE0F
private const val ZeroWidthJoiner = 0x200D
