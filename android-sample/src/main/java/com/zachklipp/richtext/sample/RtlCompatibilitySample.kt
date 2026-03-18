package com.zachklipp.richtext.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

@Preview(name = "RTL Preview Narrow", widthDp = 420, heightDp = 1600, showBackground = true)
@Composable
private fun RtlCompatibilityNarrowPreview() {
  SampleTheme {
    Surface {
      RtlCompatibilitySample()
    }
  }
}

@Preview(name = "RTL Preview Wide", widthDp = 960, heightDp = 1200, showBackground = true)
@Composable
private fun RtlCompatibilityWidePreview() {
  SampleTheme {
    Surface {
      RtlCompatibilitySample()
    }
  }
}

@Composable
fun RtlCompatibilitySample() {
  BoxWithConstraints(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
  ) {
    val isWideLayout = maxWidth >= 720.dp

    Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
        text = "Compare the same markdown under LTR and RTL layout directions.",
        style = MaterialTheme.typography.bodyLarge,
      )

      if (isWideLayout) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          layoutDirectionPanels.forEach { panel ->
            LayoutDirectionPanel(
              title = panel.title,
              layoutDirection = panel.layoutDirection,
              modifier = Modifier.weight(1f),
            )
          }
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          layoutDirectionPanels.forEach { panel ->
            LayoutDirectionPanel(
              title = panel.title,
              layoutDirection = panel.layoutDirection,
            )
          }
        }
      }
    }
  }
}

@Composable
private fun LayoutDirectionPanel(
  title: String,
  layoutDirection: LayoutDirection,
  modifier: Modifier = Modifier,
) {
  CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
    Card(
      modifier = modifier.fillMaxWidth(),
      elevation = CardDefaults.elevatedCardElevation(),
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
      ) {
        Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
        )
        markdownCases.forEach { previewCase ->
          MarkdownCaseCard(
            title = previewCase.title,
            markdown = previewCase.markdown,
          )
        }
      }
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

private data class LayoutDirectionPanelModel(
  val title: String,
  val layoutDirection: LayoutDirection,
)

private data class MarkdownCase(
  val title: String,
  val markdown: String,
)

private val layoutDirectionPanels = listOf(
  LayoutDirectionPanelModel(
    title = "LTR UI",
    layoutDirection = LayoutDirection.Ltr,
  ),
  LayoutDirectionPanelModel(
    title = "RTL UI",
    layoutDirection = LayoutDirection.Rtl,
  ),
)

private val markdownCases = listOf(
  MarkdownCase(
    title = "English-first paragraph",
    markdown = """
      # English heading

      This paragraph starts in English, links to [example.com](https://example.com),
      then mentions עברית before ending in English.

      > English quote with a little עברית mixed in.
    """.trimIndent(),
  ),
  MarkdownCase(
    title = "Hebrew-first paragraph",
    markdown = """
      # כותרת בעברית

      הפסקה הזאת מתחילה בעברית, מוסיפה [קישור](https://example.com),
      ואז משלבת English לפני הסיום.

      > ציטוט בעברית עם English בפנים.
    """.trimIndent(),
  ),
  MarkdownCase(
    title = "Lists and nesting",
    markdown = """
      1. English item
         - Nested עברית item
      2. פריט בעברית
         - Nested English item
    """.trimIndent(),
  ),
  MarkdownCase(
    title = "Inline styling and code blocks",
    markdown = """
      This sentence includes `fixed width` text in the middle.

      This sentence makes one word **bold** for emphasis.

      ```kotlin
      val english = "Hello"
      val hebrew = "שלום"
      println(english + " / " + hebrew)
      ```
    """.trimIndent(),
  ),
  MarkdownCase(
    title = "HTML alignment blocks",
    markdown = """
      <p align="left">Left aligned English block.</p>
      <p align="right">בלוק מיושר לימין.</p>
    """.trimIndent(),
  ),
)
