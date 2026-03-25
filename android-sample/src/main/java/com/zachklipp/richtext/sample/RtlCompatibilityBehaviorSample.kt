package com.zachklipp.richtext.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.halilibo.richtext.ui.material3.RichText
import com.halilibo.richtext.ui.string.RichTextRenderOptions

@Preview(name = "RTL quote gutter behavior · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun QuoteBehaviorPreview() {
  BehaviorPreviewSurface {
    BehaviorPreviewColumn {
      BehaviorSection(
        title = "English-starting quote keeps its gutter on the left",
        markdown = englishStartingQuoteMarkdown,
      )
      BehaviorSection(
        title = "Hebrew-starting quote keeps its gutter on the right",
        markdown = hebrewStartingQuoteMarkdown,
      )
      BehaviorSection(
        title = "Neutral quote falls back to the system side",
        markdown = symbolsQuoteMarkdown,
      )
    }
  }
}

@Preview(name = "RTL list marker behavior · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun ListBehaviorPreview() {
  BehaviorPreviewSurface {
    BehaviorPreviewColumn {
      BehaviorSection(
        title = "English first item keeps bullets on the left",
        markdown = englishStartingListMarkdown,
      )
      BehaviorSection(
        title = "Hebrew first item keeps bullets on the right",
        markdown = hebrewStartingListMarkdown,
      )
      BehaviorSection(
        title = "Neutral first item falls back to the system side",
        markdown = neutralStartingListMarkdown,
      )
    }
  }
}

@Preview(name = "RTL code width behavior · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun CodeWidthBehaviorPreview() {
  BehaviorPreviewSurface {
    BehaviorPreviewColumn {
      BehaviorSection(
        title = "English code keeps the same width",
        markdown = englishCodeMarkdown,
      )
      BehaviorSection(
        title = "Symbols-only code keeps the same width",
        markdown = symbolsCodeMarkdown,
      )
    }
  }
}

@Preview(name = "RTL document width behavior · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun DocumentWidthBehaviorPreview() {
  BehaviorPreviewSurface {
    BehaviorPreviewColumn {
      BehaviorSection(
        title = "English document keeps list aligned to surrounding paragraphs",
        markdown = englishDocumentWithOrderedListMarkdown,
      )
      BehaviorSection(
        title = "Hebrew document keeps list aligned to surrounding paragraphs",
        markdown = hebrewDocumentWithOrderedListMarkdown,
      )
    }
  }
}

@Composable
private fun BehaviorPreviewSurface(
  content: @Composable () -> Unit,
) {
  SampleTheme {
    Surface {
      CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        content()
      }
    }
  }
}

@Composable
private fun BehaviorPreviewColumn(
  content: @Composable () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(24.dp),
  ) {
    content()
  }
}

@Composable
private fun BehaviorSection(
  title: String,
  markdown: String,
) {
  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelLarge,
    )
    BehaviorMarkdown(markdown = markdown)
  }
}

@Composable
private fun BehaviorMarkdown(
  markdown: String,
) {
  RichText(modifier = Modifier.fillMaxWidth()) {
    Markdown(
      content = markdown,
      richtextRenderOptions = RichTextRenderOptions(),
    )
  }
}

private val englishStartingQuoteMarkdown = """
  > English opens this quote.
  > אחר כך מופיעה עברית.
""".trimIndent()

private val hebrewStartingQuoteMarkdown = """
  > הציטוט הזה מתחיל בעברית.
  > English shows up later.
""".trimIndent()

private val symbolsQuoteMarkdown = """
  > () [] {} <> / == -> ++
""".trimIndent()

private val englishStartingListMarkdown = """
  - English starts this list item.
  - אחר כך מופיע פריט בעברית.
""".trimIndent()

private val hebrewStartingListMarkdown = """
  - הפריט הראשון מתחיל בעברית.
  - English appears in the second item.
""".trimIndent()

private val neutralStartingListMarkdown = """
  - 12345
  - English appears in the second item.
""".trimIndent()

private val englishCodeMarkdown = """
  ```kotlin
  val total = value + 42
  println("A/B -> ${'$'}total")
  ```
""".trimIndent()

private val symbolsCodeMarkdown = """
  ```
  () [] {} <> / == -> ++
  ```
""".trimIndent()

private val englishDocumentWithOrderedListMarkdown = """
  The Emoji 15.1 update introduced 118 new emojis, including six completely new concepts:

  1. Phoenix
  2. Lime
  3. Brown Mushroom
  4. Broken Chain
  5. Head Shaking Horizontally

  For more detailed information on the new emojis and their meanings, you can visit Emojipedia or other emoji-related resources.
""".trimIndent()

private val hebrewDocumentWithOrderedListMarkdown = """
  עדכון Emoji 15.1 הציג 118 אימוג׳ים חדשים, כולל שישה רעיונות חדשים לגמרי:

  1. Phoenix
  2. Lime
  3. Brown Mushroom
  4. Broken Chain
  5. Head Shaking Horizontally

  למידע מפורט יותר על האימוג׳ים החדשים והמשמעויות שלהם אפשר לעיין ב-Emojipedia או במקורות נוספים.
""".trimIndent()
