package com.zachklipp.richtext.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
      ComparisonSection(
        title = "English first item keeps bullets on the left",
        markdown = englishStartingListMarkdown,
      )
      ComparisonSection(
        title = "Hebrew first item keeps bullets on the right",
        markdown = hebrewStartingListMarkdown,
      )
      ComparisonSection(
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
      ComparisonSection(
        title = "English code expands to the paragraph width",
        markdown = englishCodeMarkdown,
      )
      ComparisonSection(
        title = "Hebrew code expands and aligns from the right",
        markdown = hebrewCodeMarkdown,
      )
      ComparisonSection(
        title = "Symbols-only code keeps ambient width",
        markdown = symbolsCodeMarkdown,
      )
    }
  }
}

@Preview(name = "RTL hebrew code focused · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun HebrewCodeFocusedPreview() {
  BehaviorPreviewSurface {
    BehaviorPreviewColumn {
      ComparisonSection(
        title = "Hebrew code expands and aligns from the right",
        markdown = hebrewCodeMarkdown,
      )
    }
  }
}

@Preview(name = "LTR hebrew-only code focused · en-US", locale = "en-rUS", widthDp = 412, showBackground = true)
@Composable
private fun HebrewOnlyCodeLtrFocusedPreview() {
  BehaviorPreviewSurface(layoutDirection = LayoutDirection.Ltr) {
    BehaviorPreviewColumn {
      ComparisonSection(
        title = "Hebrew-only code block in LTR UI",
        markdown = hebrewOnlyCodeMarkdown,
      )
    }
  }
}

@Preview(name = "RTL hebrew-only code focused · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun HebrewOnlyCodeRtlFocusedPreview() {
  BehaviorPreviewSurface {
    BehaviorPreviewColumn {
      ComparisonSection(
        title = "Hebrew-only code block in RTL UI",
        markdown = hebrewOnlyCodeMarkdown,
      )
    }
  }
}

@Preview(name = "RTL english code width focused · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun EnglishCodeWidthFocusedPreview() {
  BehaviorPreviewSurface {
    BehaviorPreviewColumn {
      ComparisonSection(
        title = "English code expands to the paragraph width",
        markdown = englishCodeMarkdown,
      )
    }
  }
}

@Preview(name = "RTL english code alignment focused · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun EnglishCodeAlignmentFocusedPreview() {
  BehaviorPreviewSurface {
    BehaviorPreviewColumn {
      ComparisonSection(
        title = "English-first code line stays left-aligned in RTL content",
        markdown = englishCodeAlignmentMarkdown,
      )
    }
  }
}

@Preview(name = "RTL symbols code focused · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun SymbolsCodeFocusedPreview() {
  BehaviorPreviewSurface {
    BehaviorPreviewColumn {
      ComparisonSection(
        title = "Symbols-only code keeps ambient width",
        markdown = symbolsCodeMarkdown,
      )
    }
  }
}

@Preview(name = "RTL list focused · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun ListFocusedPreview() {
  BehaviorPreviewSurface {
    BehaviorPreviewColumn {
      ComparisonSection(
        title = "Hebrew first item keeps bullets on the right",
        markdown = hebrewStartingListMarkdown,
      )
    }
  }
}

@Preview(name = "RTL neutral item behavior · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun NeutralItemBehaviorPreview() {
  BehaviorPreviewSurface {
    BehaviorPreviewColumn {
      ComparisonSection(
        title = "Small neutral paragraph does not expand",
        markdown = neutralParagraphMarkdown,
      )
      ComparisonSection(
        title = "Single strong paragraph does not expand by itself",
        markdown = singleStrongParagraphMarkdown,
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
  layoutDirection: LayoutDirection = LayoutDirection.Rtl,
  content: @Composable () -> Unit,
) {
  SampleTheme {
    Surface {
      CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
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
    BehaviorCard {
      BehaviorMarkdown(markdown = markdown)
    }
  }
}

/** Renders the same markdown with compatibility disabled and enabled for side-by-side review. */
@Composable
private fun ComparisonSection(
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
    Text(
      text = "compatibility off",
      style = MaterialTheme.typography.bodySmall,
    )
    BehaviorCard {
      BehaviorMarkdown(
        markdown = markdown,
        enableRtlCompatibility = false,
      )
    }
    Text(
      text = "compatibility on",
      style = MaterialTheme.typography.bodySmall,
    )
    BehaviorCard {
      BehaviorMarkdown(
        markdown = markdown,
        enableRtlCompatibility = true,
      )
    }
  }
}

/** Renders one markdown example using the requested RTL compatibility mode. */
@Composable
private fun BehaviorMarkdown(
  markdown: String,
  enableRtlCompatibility: Boolean = true,
) {
  RichText {
    Markdown(
      content = markdown,
      richtextRenderOptions = RichTextRenderOptions(
        enableRtlCompatibility = enableRtlCompatibility,
      ),
    )
  }
}

@Composable
private fun BehaviorCard(
  content: @Composable () -> Unit,
) {
  Card(
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant,
    ),
  ) {
    Column(
      modifier = Modifier
        .padding(16.dp),
    ) {
      content()
    }
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
  This paragraph is intentionally much wider than the code block below so compatibility mode has a document width to align against.

  ```kotlin
  val total = value + 42
  println("A/B -> ${'$'}total")
  ```
""".trimIndent()

private val symbolsCodeMarkdown = """
  This paragraph is intentionally much wider than the symbols-only code block below.

  ```
  () [] {} <> / == -> ++
  ```
""".trimIndent()

private val hebrewCodeMarkdown = """
  הפסקה הזאת רחבה בכוונה יותר מבלוק הקוד שמתחתיה כדי לוודא שמצב התאימות משתמש ברוחב המסמך.

  ```kotlin
  val hebrew = "שלום"
  println(hebrew)
  ```
""".trimIndent()

private val hebrewOnlyCodeMarkdown = """
  הפסקה הזאת רחבה בכוונה יותר מבלוק הקוד שמתחתיה כדי שרוחב המסמך יהיה ברור גם כשכל שורות הקוד בעברית.

  ```
  שלום
  להתראות
  ```
""".trimIndent()

private val englishCodeAlignmentMarkdown = """
  הפסקה הזאת רחבה בכוונה יותר מבלוק הקוד שמתחתיה כדי שרוחב המסמך יהיה ברור גם כשהשורה הראשונה באנגלית.

  ```kotlin
  val x = 1
  println(x)
  ```
""".trimIndent()

private val neutralParagraphMarkdown = """
  12345
""".trimIndent()

private val singleStrongParagraphMarkdown = """
  word
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
