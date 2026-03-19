package com.halilibo.richtext.markdown

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDirection
import com.halilibo.richtext.ui.RichTextScope
import com.halilibo.richtext.ui.string.Text
import com.halilibo.richtext.ui.string.richTextString

@Composable
internal actual fun RichTextScope.HtmlBlock(
  content: String,
  textDirection: TextDirection?,
) {
  val richTextString = remember(content) {
    richTextString {
      withAnnotatedString {
        append(AnnotatedString.Companion.fromHtml(content))
      }
    }
  }
  Text(richTextString, textDirection = textDirection)
}
