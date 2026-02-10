package com.halilibo.richtext.commonmark

/**
 * Allows configuration of the Markdown parser
 *
 * @param autolink Detect plain text links and turn them into Markdown links.
 * @param autoCloseInlineDelimiters Close incomplete inline delimiters in streaming content so
 * partial chunks are rendered as formatted text instead of raw markers.
 */
public class CommonMarkdownParseOptions(
  public val autolink: Boolean,
  public val autoCloseInlineDelimiters: Boolean = true,
) {

  override fun toString(): String {
    return "CommonMarkdownParseOptions(autolink=$autolink, autoCloseInlineDelimiters=$autoCloseInlineDelimiters)"
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is CommonMarkdownParseOptions) return false

    return autolink == other.autolink &&
      autoCloseInlineDelimiters == other.autoCloseInlineDelimiters
  }

  override fun hashCode(): Int {
    var result = autolink.hashCode()
    result = 31 * result + autoCloseInlineDelimiters.hashCode()
    return result
  }

  public fun copy(
    autolink: Boolean = this.autolink,
    autoCloseInlineDelimiters: Boolean = this.autoCloseInlineDelimiters,
  ): CommonMarkdownParseOptions = CommonMarkdownParseOptions(
    autolink = autolink,
    autoCloseInlineDelimiters = autoCloseInlineDelimiters,
  )

  public companion object {
    public val Default: CommonMarkdownParseOptions = CommonMarkdownParseOptions(
      autolink = true,
      autoCloseInlineDelimiters = true,
    )
  }
}
