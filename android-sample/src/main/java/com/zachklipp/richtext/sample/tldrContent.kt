package com.zachklipp.richtext.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.PlaceholderVerticalAlign.Companion.AboveBaseline
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.ui.string.InlineContent
import com.halilibo.richtext.ui.string.RichTextString
import com.zachklipp.richtext.sample.contentreference.ApiContentReference

/** Some arbitrary number that is wider than the screen (but not so wide that an exception gets thrown). */
internal const val InlineContentInitialSizeFullWidth = 10_000

/**
 * Render a composable inline with the markdown content.
 */
internal fun RichTextString.Builder.appendInlineContent(
  renderOnNewLine: Boolean = false,
  initialSize: Density.() -> IntSize,
  placeholderVerticalAlign: PlaceholderVerticalAlign = AboveBaseline,
  content: @Composable Density.(alternateText: String) -> Unit,
) {
  appendInlineContent(
    content = InlineContent(
      renderOnNewLine = renderOnNewLine,
      initialSize = initialSize,
      placeholderVerticalAlign = placeholderVerticalAlign,
      content = content,
    ),
  )
}

internal fun RichTextString.Builder.tldrContent(
  tldr: ApiContentReference.Tldr,
  onLinkClick: (String) -> Unit,
  trackClick: (ApiContentReference, Int?) -> Unit,
) {
  appendInlineContent(
    renderOnNewLine = true,
    initialSize = { IntSize(InlineContentInitialSizeFullWidth, 48.dp.roundToPx()) },
  ) {
    TldrContent(tldr, onLinkClick, trackClick)
  }
}

@Composable
private fun TldrContent(
  tldr: ApiContentReference.Tldr,
  onLinkClick: (String) -> Unit,
  trackClick: (ApiContentReference, Int?) -> Unit,
) {
  Row(
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 8.dp),
  ) {
    Column(
      modifier = Modifier
        .weight(1f),
    ) {
      Text(
        tldr.displayTitle,
        style = TextStyle(
          fontSize = 18.sp,
          fontWeight = FontWeight.W500,
        ),
      )
      if (tldr.breadcrumbs.isNotEmpty()) {
        Text(
          tldr.breadcrumbs.joinToString(">"),
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          style = MaterialTheme.typography.bodyLarge.let { style ->
            when (tldr.url.isNullOrBlank()) {
              true -> style
              false -> style.copy(color = Color.Blue)
            }
          },
          modifier = Modifier.padding(end = 8.dp),
        )
      }
    }
    Box(
      modifier = Modifier
        .size(32.dp)
        .background(Color.Gray),
    )
  }

}
