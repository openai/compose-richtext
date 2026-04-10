package com.zachklipp.richtext.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.string.RichTextRenderOptions
import com.halilibo.richtext.ui.material3.RichText

@Preview(name = "English Heading · en-US", locale = "en-rUS", widthDp = 412, showBackground = true)
@Preview(name = "English Heading · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun EnglishHeadingPreview() {
  PreviewCaseSurface {
    MarkdownCaseContent(markdown = englishHeadingMarkdown)
  }
}

@Preview(name = "כותרת בעברית · en-US", locale = "en-rUS", widthDp = 412, showBackground = true)
@Preview(name = "כותרת בעברית · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun HebrewHeadingPreview() {
  PreviewCaseSurface {
    MarkdownCaseContent(markdown = hebrewHeadingMarkdown)
  }
}

@Preview(name = "Inline styling and code blocks · en-US", locale = "en-rUS", widthDp = 412, showBackground = true)
@Preview(name = "Inline styling and code blocks · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun InlineStylingAndCodeBlocksPreview() {
  PreviewCaseSurface {
    MarkdownCaseContent(markdown = inlineStylingAndCodeBlocksMarkdown)
  }
}

@Composable
private fun PreviewCaseSurface(
  content: @Composable () -> Unit,
) {
  SampleTheme {
    Surface {
      content()
    }
  }
}

@Composable
fun RtlCompatibilitySample() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(28.dp),
  ) {
    markdownCases.forEach { markdown ->
      previewVariants.forEach { previewVariant ->
        LocalizedMarkdownCaseSection(
          markdown = markdown,
          previewVariant = previewVariant,
        )
      }
    }
  }
}

@Composable
private fun LocalizedMarkdownCaseSection(
  markdown: String,
  previewVariant: PreviewVariant,
) {
  CompositionLocalProvider(LocalLayoutDirection provides previewVariant.layoutDirection) {
    MarkdownCaseContent(
      localeLabel = previewVariant.localeLabel,
      layoutDirection = previewVariant.layoutDirection,
      markdown = markdown,
    )
  }
}

@Composable
private fun MarkdownCaseContent(
  markdown: String,
  localeLabel: String? = null,
  layoutDirection: LayoutDirection? = null,
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    if (localeLabel != null) {
      Text(
        text = "locale = $localeLabel",
        style = MaterialTheme.typography.labelLarge,
      )
    }
    if (layoutDirection != null) {
      Text(
        text = "layoutDirection = LayoutDirection.${layoutDirection.name}",
        style = MaterialTheme.typography.labelLarge,
      )
    }
    RichText(modifier = Modifier.fillMaxWidth()) {
      Markdown(
        content = markdown,
        richtextRenderOptions = rtlCompatibilityOptions,
      )
    }
  }
}

private val rtlCompatibilityOptions = RichTextRenderOptions(enableRtlCompatibility = true)

private data class PreviewVariant(
  val localeLabel: String,
  val layoutDirection: LayoutDirection,
)

private val previewVariants = listOf(
  PreviewVariant(localeLabel = "en-US", layoutDirection = LayoutDirection.Ltr),
  PreviewVariant(localeLabel = "he-IL", layoutDirection = LayoutDirection.Rtl),
)

private val englishHeadingMarkdown = """
  # English Heading

  This paragraph starts in English, links to [example.com](https://example.com),
  then mentions עברית before ending in English.

  12345

  > English quote with a little עברית mixed in.
""".trimIndent()

private val hebrewHeadingMarkdown = """
  # כותרת בעברית

  הפסקה הזאת מתחילה בעברית, מוסיפה [קישור](https://example.com),
  ואז משלבת English לפני הסיום.

  12345

  > ציטוט בעברית עם English בפנים.
""".trimIndent()

private val inlineStylingAndCodeBlocksMarkdown = """
  This sentence includes `fixed width` text in the middle.

  This sentence makes one word **bold** for emphasis.

  ```kotlin
  val english = "Hello"
  val hebrew = "שלום"
  println(english + " / " + hebrew)
  ```
""".trimIndent()

private val markdownCases = listOf(
  englishHeadingMarkdown,
  hebrewHeadingMarkdown,
  inlineStylingAndCodeBlocksMarkdown,
)
