package com.zachklipp.richtext.sample

import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val Samples = listOf<Pair<String, @Composable () -> Unit>>(
  "RichText Demo" to @Composable { RichTextSample() },
  "Markdown Demo" to @Composable { MarkdownSample() },
  "Lazy Markdown Demo" to @Composable { LazyMarkdownSample() },
  "Pagination" to @Composable { PagedSample() },
  "Printable Document" to @Composable { DocumentSample() },
  "Slideshow" to @Composable { SlideshowSample() },
  "Text Animation" to @Composable { AnimatedRichTextSample() },
)

@Preview(showBackground = true)
@Composable private fun SampleLauncherPreview() {
  SamplesListScreen(onSampleClicked = {})
}

@Composable fun SampleLauncher() {
  var currentSampleIndex: Int? by remember { mutableStateOf(null) }

  Crossfade(currentSampleIndex) { index ->
    if (index != null) {
      BackHandler(onBack = { currentSampleIndex = null })
      Samples[index].second()
    } else {
      SamplesListScreen(onSampleClicked = { currentSampleIndex = it })
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable private fun SamplesListScreen(onSampleClicked: (Int) -> Unit) {
  SampleTheme(colorScheme = darkColorScheme()) {
    Scaffold(
      topBar = {
        TopAppBar(title = { Text("Samples") })
      }
    ) { contentPadding ->
      LazyColumn(modifier = Modifier.padding(contentPadding)) {
        itemsIndexed(Samples) { index, (title, sampleContent) ->
          ListItem(
            headlineContent = { Text(title) },
            modifier = Modifier.clickable(onClick = { onSampleClicked(index) }),
            leadingContent = {
              // Slideshow tries to take over the screen through LocalView. It needs to be
              // overridden to prevent the launcher from going fullscreen.
              // Overriding the local view can't be done always, because it causes AndroidView
              // usages to crash.
              val overrideLocalView = (title == "Slideshow")
              SamplePreview(overrideLocalView = overrideLocalView, sampleContent)
            }
          )
        }
      }
    }
  }
}

@Composable private fun SamplePreview(
  overrideLocalView: Boolean,
  content: @Composable () -> Unit,
) {
  val localView = if (overrideLocalView) {
    val context = LocalContext.current
    remember { FrameLayout(context.applicationContext) }
  } else {
    LocalView.current
  }
  CompositionLocalProvider(LocalView provides localView) {
    ScreenPreview(
      Modifier
        .size(50.dp)
        .aspectRatio(1f)
        .clipToBounds()
        // "Zoom in" to the top-start corner to make the preview more legible.
        .graphicsLayer(
          scaleX = 1.5f, scaleY = 1.5f,
          transformOrigin = TransformOrigin(0f, 0f)
        ),
    ) {
      SampleTheme(colorScheme = darkColorScheme()) {
        Surface(content = content)
      }
    }
  }
}
