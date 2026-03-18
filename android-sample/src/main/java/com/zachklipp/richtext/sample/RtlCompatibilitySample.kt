package com.zachklipp.richtext.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText

@Preview(widthDp = 420, heightDp = 1000, showBackground = true)
@Composable
private fun RtlCompatibilityLtrPreview() {
  SampleTheme {
    Surface {
      RtlCompatibilitySample(layoutDirection = LayoutDirection.Ltr)
    }
  }
}

@Preview(widthDp = 420, heightDp = 1000, showBackground = true)
@Composable
private fun RtlCompatibilityRtlPreview() {
  SampleTheme {
    Surface {
      RtlCompatibilitySample(layoutDirection = LayoutDirection.Rtl)
    }
  }
}

@Composable
fun RtlCompatibilitySample(
  layoutDirection: LayoutDirection = LayoutDirection.Ltr,
) {
  CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
        text = if (layoutDirection == LayoutDirection.Rtl) "RTL UI" else "LTR UI",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
      )
      MarkdownCaseCard(
        title = "English Content",
        markdown = englishMarkdown,
      )
      MarkdownCaseCard(
        title = "Hebrew Content",
        markdown = hebrewMarkdown,
      )
    }
  }
}

@Composable
private fun MarkdownCaseCard(
  title: String,
  markdown: String,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    elevation = CardDefaults.elevatedCardElevation(),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
      )
      RichText(modifier = Modifier.fillMaxWidth()) {
        Markdown(content = markdown)
      }
    }
  }
}

private val englishMarkdown = """
  # Title English

  Text in English and then a [link](https://example.com) and more text.

  A list with both languages:
  1. English
  2. עברית

  <p align="right">Right</p>
""".trimIndent()

private val hebrewMarkdown = """
  # כותרת בעברית

  טקסט בעברית ואז [קישור](https://example.com) ועוד טקסט.

  רשימה עם שתי השפות:
  1. עברית
  2. English

  <p align="right">ימין</p>
""".trimIndent()
