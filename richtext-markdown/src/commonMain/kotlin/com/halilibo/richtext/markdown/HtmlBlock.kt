package com.halilibo.richtext.markdown

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextDirection
import com.halilibo.richtext.ui.RichTextScope

/**
 * Android and JVM can have different WebView or HTML rendering implementations.
 * We are leaving HTML rendering to platform side.
 */
@Composable
internal expect fun RichTextScope.HtmlBlock(
  content: String,
  textDirection: TextDirection? = null,
)
