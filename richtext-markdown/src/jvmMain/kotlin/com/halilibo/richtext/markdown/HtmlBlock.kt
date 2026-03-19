package com.halilibo.richtext.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextDirection
import com.halilibo.richtext.ui.RichTextScope
import com.halilibo.richtext.ui.string.Text
import com.halilibo.richtext.ui.string.richTextString

@Composable
internal actual fun RichTextScope.HtmlBlock(
  content: String,
  textDirection: TextDirection?,
) {
  Text(
    richTextString { append(content.replace(Regex("<[^>]+>"), "")) },
    textDirection = textDirection,
  )
}
