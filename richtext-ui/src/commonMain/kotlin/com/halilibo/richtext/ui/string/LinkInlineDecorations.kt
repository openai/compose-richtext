package com.halilibo.richtext.ui.string

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

internal data class DecoratedTextResult(
  val annotatedString: AnnotatedString,
  val inlineContents: Map<String, InlineContent>,
  val decoratedLinkRanges: List<DecoratedLinkRange>,
)

internal fun ResolvedLinkDecorationRange.hasInlineContent(): Boolean {
  val inlineContent = inlineContent ?: return false
  return inlineContent.leading != null || inlineContent.trailing != null
}

internal fun decorateAnnotatedStringWithLinkIcons(
  annotated: AnnotatedString,
  baseInlineContents: Map<String, InlineContent>,
  linkDecorations: List<ResolvedLinkDecorationRange>,
): DecoratedTextResult {
  if (linkDecorations.isEmpty()) {
    return DecoratedTextResult(
      annotatedString = annotated,
      inlineContents = baseInlineContents,
      decoratedLinkRanges = emptyList(),
    )
  }

  val builder = AnnotatedString.Builder()
  val inlineContents = LinkedHashMap<String, InlineContent>(baseInlineContents.size)
  inlineContents.putAll(baseInlineContents)
  val decoratedLinkRanges = mutableListOf<DecoratedLinkRange>()
  val sortedDecorations = linkDecorations.sortedBy { it.start }
  var cursor = 0

  sortedDecorations.forEachIndexed { index, decoration ->
    val start = decoration.start
    val end = decoration.end
    if (cursor < start) {
      builder.append(annotated, cursor, start)
    }
    if (start >= end) {
      cursor = maxOf(cursor, end)
      return@forEachIndexed
    }

    val inlineContent = decoration.inlineContent
    val linkAnnotation = annotated.getLinkAnnotations(start, end)
      .firstOrNull()
      ?.item as? LinkAnnotation.Url
    if (inlineContent != null) {
      inlineContent.leading?.let { spec ->
        val id = "link_inline_${index}_leading"
        builder.appendInlineContent(id, REPLACEMENT_CHAR)
        inlineContents[id] = buildInlineContent(
          spec = spec,
          context = LinkContext(decoration.destination, decoration.text),
          spacing = inlineContent.spacing,
          isLeading = true,
        )
        if (inlineContent.includeInHitTarget && linkAnnotation != null) {
          val iconStart = builder.length - 1
          builder.addLink(linkAnnotation, iconStart, builder.length)
        }
      }
    }

    val textStart = builder.length
    builder.append(annotated, start, end)
    val textEnd = builder.length

    if (inlineContent != null) {
      inlineContent.trailing?.let { spec ->
        val id = "link_inline_${index}_trailing"
        builder.appendInlineContent(id, REPLACEMENT_CHAR)
        inlineContents[id] = buildInlineContent(
          spec = spec,
          context = LinkContext(decoration.destination, decoration.text),
          spacing = inlineContent.spacing,
          isLeading = false,
        )
        if (inlineContent.includeInHitTarget && linkAnnotation != null) {
          val iconStart = builder.length - 1
          builder.addLink(linkAnnotation, iconStart, builder.length)
        }
      }
    }

    if (decoration.underlineStyle !is UnderlineStyle.Solid) {
      decoratedLinkRanges += DecoratedLinkRange(
        start = textStart,
        end = textEnd,
        destination = decoration.destination,
        underlineStyle = decoration.underlineStyle,
        underlineColor = decoration.underlineColor,
        linkStyleOverride = decoration.linkStyleOverride,
      )
    }

    cursor = end
  }

  if (cursor < annotated.length) {
    builder.append(annotated, cursor, annotated.length)
  }

  return DecoratedTextResult(
    annotatedString = builder.toAnnotatedString(),
    inlineContents = inlineContents,
    decoratedLinkRanges = decoratedLinkRanges,
  )
}

private fun buildInlineContent(
  spec: InlineIconSpec,
  context: LinkContext,
  spacing: Dp,
  isLeading: Boolean,
): InlineContent {
  val spacingWidth = if (spacing.value > 0f) spacing else 0.dp
  val placeholderSize = if (spacingWidth.value > 0f) {
    DpSize(
      width = spec.size.width + spacingWidth,
      height = spec.size.height,
    )
  } else {
    spec.size
  }

  val paddingModifier = when {
    spacingWidth.value <= 0f -> Modifier
    isLeading -> Modifier.padding(end = spacingWidth)
    else -> Modifier.padding(start = spacingWidth)
  }

  val contentModifier = paddingModifier
    .then(Modifier.size(spec.size))

  return InlineContent(
    initialSize = {
      IntSize(
        placeholderSize.width.toPx().roundToInt(),
        placeholderSize.height.toPx().roundToInt(),
      )
    },
    placeholderVerticalAlign = spec.placeholderVerticalAlign,
  ) {
    when (spec) {
      is InlineIconSpec.Painter -> {
        Image(
          painter = spec.painter,
          contentDescription = spec.contentDescription,
          colorFilter = spec.tint?.let { ColorFilter.tint(it) },
          modifier = contentModifier,
        )
      }
      is InlineIconSpec.Composable -> {
        Box(modifier = contentModifier) {
          spec.content.Render(context)
        }
      }
    }
  }
}
