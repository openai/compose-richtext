package com.zachklipp.richtext.sample

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.markdown.node.AstCustomNode
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material.RichText
import com.halilibo.richtext.ui.string.InlineContent
import com.halilibo.richtext.ui.string.RichTextRenderOptions
import com.halilibo.richtext.ui.string.RichTextStringStyle
import com.zachklipp.richtext.sample.contentreference.ApiContentReference
import com.zachklipp.richtext.sample.contentreference.ContentReferenceDelimiterProcessor
import com.zachklipp.richtext.sample.contentreference.ContentReferenceNode
import org.commonmark.node.AbstractVisitor
import org.commonmark.node.Node
import org.commonmark.node.SoftLineBreak
import org.commonmark.parser.Parser
import kotlin.math.roundToInt

@Composable
fun AnimationSample() {
  val renderOptions = remember {
    RichTextRenderOptions(
      animate = true,
      textFadeInMs = 10000,
      delayMs = 1000,
      debounceMs = 4000,
    )
  }
//  val content = Markdown(
//    "abc 234567890123456 def",
//    references = listOf(
//      ApiContentReference.Tldr(4, 19, "OpenAI", "", listOf("OpenAI"))
//    )
//  )

  val content = Markdown(
    "abc def ghi jkl mno pqr",
  )

  RichText(
    style = MessageRichTextStyle,
    modifier = Modifier.padding(8.dp),
  ) {
    Markdown(
      content.markdownNode,
      richtextRenderOptions = renderOptions,
      inlineContentOverride = { node, stringBuilder, defaultContent, _ ->
        val type = node.type
        if (type is AstCustomNode) {
          when (val n = type.node) {
            is ContentReferenceNode -> {
              when (val cr = n.contentReference) {
                is ApiContentReference.Tldr -> {
                  stringBuilder.tldrContent(cr, {}, { _, _ -> })
                }

                else -> stringBuilder.appendInlineContent(
                  content = InlineContent(
                    renderOnNewLine = false,
                    initialSize = { IntSize(51.dp.toPx().roundToInt(), 13.dp.toPx().roundToInt()) },
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center,
                    content = {
                      Box(
                        Modifier
                          .size(51.dp, 13.dp)
                          .background(Color.Red)
                      )
                    },
                  ),
                )
              }
              null
            }

            else -> defaultContent()
          }
        } else {
          defaultContent()
        }
      },
    )
  }
}

@Immutable
data class Markdown(
  val content: String,
  val audioTranscription: Boolean = false,
  val references: List<ApiContentReference> = emptyList(),
  val contentAlreadyProcessedForMarkdownParsing: Boolean = false,
) {
  /**
   * Process the raw content string and makes a few changes that aid with markdown parsing:
   */
  @VisibleForTesting
  val contentForMarkdownParsing: String by lazy {
    when (contentAlreadyProcessedForMarkdownParsing) {
      true -> content
      false ->
        content
          .replaceContentReferences(references)
          .trimPartialContentReference()
    }
  }

  val contentForTextSelection by lazy {
    contentForMarkdownParsing
      .replaceContentReferencesWithAlt(references)
      .removeContextListDelimiter()
  }

  val markdownNode: Node by lazy {
    val parser = when (references.isEmpty()) {
      true -> parserWithoutReferences
      false -> parserBuilder()
        .customDelimiterProcessor(ContentReferenceDelimiterProcessor(references))
        .build()
    }
    parser.parse(contentForMarkdownParsing).also { node ->
      node.accept(object : AbstractVisitor() {
        override fun visit(softLineBreak: SoftLineBreak) {
          super.visit(softLineBreak)
          if (softLineBreak.previous is ContentReferenceNode) {
            // A content reference is usually shown as block content.
            // If we don't remove soft line breaks after then there will be extra space in the content.
            softLineBreak.unlink()
          }
        }
      })
    }
  }

  fun findLinks(): List<String> {
    val links = mutableSetOf<String>()
    markdownNode.accept(object : AbstractVisitor() {
      override fun visit(node: org.commonmark.node.Link) {
        links.add(node.destination)
        super.visit(node)
      }
    })
    return links.toList()
  }

  companion object {
    private fun parserBuilder(): Parser.Builder {
      return Parser.builder()
    }

    private val parserWithoutReferences = parserBuilder().build()

    /**
     * Iterates through the content references and replaces their spans with
     * their index in the references array.
     * Markdown parsing then parses the indices and knows which reference to
     * use in the [com.openai.feature.conversations.domain.message.reference.ContentReferenceNode].
     */
    private fun String.replaceContentReferences(references: List<ApiContentReference>): String {
      return references.reversed().foldIndexed(this) { i, acc, reference ->
        val startIdx = reference.startIdx ?: return@foldIndexed acc
        val endIdx = reference.endIdx ?: return@foldIndexed acc
        val refIndex = references.size - i - 1

        when (reference) {
          is ApiContentReference.Unsupported -> acc.replaceRangeByCodePoints(
            startIdx,
            endIdx,
            reference.alt.orEmpty(),
          )

          else -> acc.replaceRangeByCodePoints(
            startIdx,
            endIdx,
            buildString {
              append('\u200B')
              append(ApiContentReference.MarkdownStartDelimiter)
              append(refIndex)
              append(ApiContentReference.MarkdownEndDelimiter)
              append('\u200B')
            },
          )
        }
      }
    }

    private fun String.replaceContentReferencesWithAlt(references: List<ApiContentReference>): String {
      return references.foldIndexed(this) { i, acc, ref ->
        val replacementString = buildString {
          append(ApiContentReference.MarkdownStartDelimiter)
          append(i)
          append(ApiContentReference.MarkdownEndDelimiter)
        }
        acc.replace(replacementString, ref.alt.orEmpty())
      }
    }

    private fun String.removeContextListDelimiter(): String {
      return this.replace(":::contextList", "").replace(":::", "")
    }

    private fun String.trimPartialContentReference(): String {
      // These are the characters the model currently delimit citations on. However, it is valid punctuation
      // in CJK so, ideally, this would be a unicode private space character or something.
      val lastCitationStart = lastIndexOf('\uE200')
      val lastCitationEnd = lastIndexOf('\uE201')
      return if (lastCitationStart >= 0 && lastCitationStart > lastCitationEnd) {
        substring(0, lastCitationStart)
      } else {
        this
      }
    }
  }
}

private fun String.replaceContentReferences(references: List<ApiContentReference>): String {
  return references.reversed().foldIndexed(this) { i, acc, reference ->
    val startIdx = reference.startIdx ?: return@foldIndexed acc
    val endIdx = reference.endIdx ?: return@foldIndexed acc
    val refIndex = references.size - i - 1

    acc.replaceRangeByCodePoints(
      startIdx,
      endIdx,
      "\u200B${ApiContentReference.MarkdownStartDelimiter}${refIndex}${ApiContentReference.MarkdownEndDelimiter}\u200B",
    )
  }
}

/**
 * Replaces a substring specified by Unicode code point indices with the provided replacement text.
 *
 * This must be used instead of a normal substring replacement because python treats graphemes (groups of
 * unicode characters that join together like a person + skin tone modifier) as a single character.
 *
 * However, jvm substring counts each unicode code point. We have to translate the original indices to
 * code point indices before doing any string operations.
 */
fun String.replaceRangeByCodePoints(
  originalStartIndex: Int,
  originalEndIndex: Int,
  replacement: CharSequence,
): String {
  if (originalStartIndex < 0) {
    Log.d("Gabe", "Start index must be positive")
    return this
  } else if (originalEndIndex < 0) {
    Log.d("Gabe", "End index must be positive")
    return this
  } else if (originalStartIndex > originalEndIndex) {
    Log.d("Gabe", "End index must be greater than the start index")
    return this
  }

  val totalCodePoints = codePointCount(0, length)

  if (originalStartIndex > totalCodePoints) {
    Log.d("Gabe", "End index (with code points) must be greater than the the start index")
    return this
  } else if (originalEndIndex > totalCodePoints) {
    Log.d("Gabe", "End index is out of range ($originalEndIndex > $totalCodePoints)")
    return this
  }

  val startCharIndex = offsetByCodePoints(0, originalStartIndex)
  val endCharIndex = offsetByCodePoints(0, originalEndIndex)

  return this.replaceRange(startCharIndex, endCharIndex, replacement)
}

val MessageRichTextStyle: RichTextStyle
  @Composable
  @ReadOnlyComposable
  get() = RichTextStyle(
    paragraphSpacing = 24.sp,
    stringStyle = RichTextStringStyle(
      codeStyle = SpanStyle(
        fontFamily = FontFamily.Monospace,
        fontSize = 14.sp,
        color = Color.Black,
      ),
      linkStyle = SpanStyle(
        textDecoration = TextDecoration.None,
        color = Color.Blue,
      ),
    ),
  )
