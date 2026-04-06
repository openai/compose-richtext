package com.zachklipp.richtext.sample

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText
import com.halilibo.richtext.ui.string.RichTextRenderOptions

@Preview(name = "Centered neutral HTML · on · en-US", locale = "en-rUS", widthDp = 412, showBackground = true)
@Preview(name = "Centered neutral HTML · on · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun CenteredNeutralHtmlCompatibilityOnPreview() {
  CenteredHtmlPreview(
    markdown = "<center>12345</center>",
    enableRtlCompatibility = true,
  )
}

@Preview(name = "Centered neutral HTML · off · en-US", locale = "en-rUS", widthDp = 412, showBackground = true)
@Preview(name = "Centered neutral HTML · off · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun CenteredNeutralHtmlCompatibilityOffPreview() {
  CenteredHtmlPreview(
    markdown = "<center>12345</center>",
    enableRtlCompatibility = false,
  )
}

@Preview(name = "Centered Hebrew HTML · on · en-US", locale = "en-rUS", widthDp = 412, showBackground = true)
@Preview(name = "Centered Hebrew HTML · on · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun CenteredHebrewHtmlCompatibilityOnPreview() {
  CenteredHtmlPreview(
    markdown = "<center>שלום עולם</center>",
    enableRtlCompatibility = true,
  )
}

@Preview(name = "Centered Hebrew HTML · off · en-US", locale = "en-rUS", widthDp = 412, showBackground = true)
@Preview(name = "Centered Hebrew HTML · off · he-IL", locale = "he-rIL", widthDp = 412, showBackground = true)
@Composable
private fun CenteredHebrewHtmlCompatibilityOffPreview() {
  CenteredHtmlPreview(
    markdown = "<center>שלום עולם</center>",
    enableRtlCompatibility = false,
  )
}

@Composable
private fun CenteredHtmlPreview(
  markdown: String,
  enableRtlCompatibility: Boolean,
) {
  SampleTheme {
    Surface {
      RichText(modifier = Modifier.fillMaxWidth()) {
        Markdown(
          content = markdown,
          richtextRenderOptions = RichTextRenderOptions(
            enableRtlCompatibility = enableRtlCompatibility,
          ),
        )
      }
    }
  }
}
