package com.zachklipp.richtext.sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.halilibo.richtext.commonmark.Markdown
import com.halilibo.richtext.ui.material3.RichText
import com.halilibo.richtext.ui.string.RichTextRenderOptions
import kotlinx.coroutines.delay
import kotlin.random.Random

@Preview
@Composable private fun AnimatedRichTextSamplePreview() {
  AnimatedRichTextSample()
}

@Preview
@Composable private fun ChineseAnimatedRichTextSamplePreview() {
  ChineseAnimatedRichTextSample()
}

@Composable fun AnimatedRichTextSample() {
  AnimatedTextWrapper(
    title = "Stream incrementally",
    completeContent = { CompleteTextSample() },
    chunkedContent = { ChunkingTextSample() },
  )
}

@Composable fun ChineseAnimatedRichTextSample() {
  AnimatedTextWrapper(
    title = "Stream incrementally",
    completeContent = { ChineseCompleteTextSample() },
    chunkedContent = { ChineseChunkingTextSample() },
  )
}

@Composable
private fun AnimatedTextWrapper(
  title: String,
  completeContent: @Composable () -> Unit,
  chunkedContent: @Composable () -> Unit,
) {
  var isChunked by remember { mutableStateOf(false) }

  SampleTheme {
    Surface {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            title,
            modifier = Modifier
              .weight(1f)
              .padding(16.dp),
          )
          Checkbox(isChunked, onCheckedChange = { isChunked = it })
        }
        Box(Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
          if (!isChunked) {
            completeContent()
          } else {
            chunkedContent()
          }
        }
      }
    }
  }
}

@Composable
private fun CompleteTextSample() {
  val markdownOptions = remember {
    RichTextRenderOptions(
      animate = true,
      textFadeInMs = 500,
      delayMs = 70,
      debounceMs = 200,
    )
  }

  RichText {
    Markdown(
      SampleText,
      richtextRenderOptions = markdownOptions,
    )
  }
}

@Composable
private fun ChunkingTextSample() {
  var currentText by remember { mutableStateOf("") }
  var isComplete by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    var remaining = SampleText
    while (remaining.isNotEmpty()) {
      delay(200L + Random.nextInt(500))
      val chunkLength = 10 + Random.nextInt(100)
      currentText += remaining.take(chunkLength)
      remaining = remaining.drop(chunkLength)
    }
    isComplete = true
  }

  val markdownOptions = remember(isComplete) {
    RichTextRenderOptions(
      animate = !isComplete,
      textFadeInMs = 500,
      delayMs = 70,
      debounceMs = 200,
    )
  }

  RichText {
    Markdown(
      currentText,
      richtextRenderOptions = markdownOptions,
    )
  }
}

@Composable
private fun ChineseCompleteTextSample() {
  val markdownOptions = remember {
    RichTextRenderOptions(
      animate = true,
      textFadeInMs = 500,
      delayMs = 70,
      debounceMs = 200,
    )
  }

  RichText {
    Markdown(
      ChineseSampleText,
      richtextRenderOptions = markdownOptions,
    )
  }
}

@Composable
private fun ChineseChunkingTextSample() {
  var currentText by remember { mutableStateOf("") }
  var isComplete by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) {
    var remaining = ChineseSampleText
    while (remaining.isNotEmpty()) {
      delay(200L + Random.nextInt(500))
      val chunkLength = 6 + Random.nextInt(40)
      currentText += remaining.take(chunkLength)
      remaining = remaining.drop(chunkLength)
    }
    isComplete = true
  }

  val markdownOptions = remember(isComplete) {
    RichTextRenderOptions(
      animate = !isComplete,
      textFadeInMs = 500,
      delayMs = 70,
      debounceMs = 200,
    )
  }

  RichText {
    Markdown(
      currentText,
      richtextRenderOptions = markdownOptions,
    )
  }
}

private const val SampleText = """
1-The quick brown fox jumps over the lazy dog.
1-The quick brown fox jumps over the lazy dog.
1-The quick brown fox jumps over the lazy dog.
1-The quick brown fox jumps over the lazy dog.
1-The quick brown fox jumps over the lazy dog.

* Formatted list 1
* Formatted list 2
  * Sub bullet point

# Header 1
2-The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.

| Column A | Column B |
|----------|----------|
| The quick brown fox jumps over the lazy dog. | The quick brown fox jumps over the lazy dog. |

##### Header 5
4-The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox jumps over the lazy dog.
The quick brown fox **jumps over the lazy dog.**
"""

private const val ChineseSampleText = """
# 大段落和列表交替

第一段没有使用任何标点符号我們不断地写下去仿佛一口气都不愿意停下来只为了测试在长中文文本中动画何时开始生效長長久久的文字连贯地排列直到最终换行
这一段继续延伸依旧没有任何停顿让我们能够观察在无标点的情况下动画如何处理这些词句因为这在列表和段落中很常见
这是第一段的尾声依然没有句号因為我們希望強迫整段文本被視為一個超長詞組

* **粗体列表一** 这一行用于测试在列表里加粗文本而且没有句号
* **粗体列表二** 这行同样没有标点并且包含更多的文字来延长动画
* **粗体列表三** 包含很多很多的文字一直延伸最终在这里结束
  * **粗体子项目** 子项同样保持没有标点以便检查子级列表

第二段文字是一个比较长的段落它包含中文句号和逗号，但是也混杂了没有标点的句子让我们看看动画能否正常结束。还有一行没有标点用来测试整段结束后的行为
继续在第二段里添加更多文字以确保段落足够长让动画不会过早结束同样我们在中途不会停顿

1. *斜体样式项目一* 混合`代码片段`以及[链接](https://example.com)展示不同的风格
2. ~~删除线项目二~~ 包含**粗体**和*斜体*并且没有句号
3. 混合样式项目三 **加粗** *斜体* `代码` [链接](https://example.com) 持续扩展这个句子直到没有标点为止
   1. 子项目三之一 **子项中的粗体** 也可能没有标点为了继续测试
   2. 子项目三之二 包含`代码`和*斜体*同时缺乏终止符

第三段再次变得非常长我们不停地往后添加文字没有任何停顿这对于动画来说是一个极端的测试因为整段看起来像一个巨大的短语但实际上我们只是想确保在没有标点时段落也能完整显示出来
我们继续书写这一段广袤的文字内容不添加标点直到最后一刻让整段维持一种悬而未决的状态

- 列表风格一 **粗体** *斜体* 汇集在同一行里以增加复杂度
- 列表风格二 `代码` [链接](https://example.com) 再加上~~删除线~~来观察不同样式叠加
- 列表风格三 没有标点没有停顿一直延续延续延续延续延续延续延续直到最后才让它自然结束

第四段告诉我们下面即将出现代码块和表格因此我们努力延长段落长度来观察长文本结合代码块时的动画表现最终我们在这里停顿。
```kotlin
fun greet(name: String) {
    println("你好，jo")
}
```

| 列A | 列B |
|-----|-----|
| 内容A | 内容B |
| 很长的一段内容A | 更长的内容B 用来测试表格展示 |

> 最后再来一个引用块其中包含 **粗体** 和 *倾斜* 以及 `代码` 继续延长直到我们觉得足够
> 引用的第二行也不含标点我们想观察整个引用是否可以被完整显示

结尾的大段落再次没有标点我们仍然不断添加文本直到最后在这里加上句号。
"""
