package com.zachklipp.richtext.sample.contentreference

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.zachklipp.richtext.sample.R
import java.time.Instant

sealed class ApiContentReference {
  abstract val type: String
  abstract val startIdx: Int?
  abstract val endIdx: Int?
  open val alt: String? = null

  data class Unsupported(
    override val startIdx: Int?,
    override val endIdx: Int?,
    override val type: String = "unsupported",
    override val alt: String? = null,
  ) : ApiContentReference()

  data class Hidden(
    override val startIdx: Int?,
    override val endIdx: Int?,
    override val type: String = "hidden",
    override val alt: String? = null,
  ) : ApiContentReference()

  data class UrlCitation(
    override val startIdx: Int?,
    override val endIdx: Int?,
    val url: String,
    val title: String? = null,
    val attribution: String? = null,
    /**
     * True: skip safe url check
     * False: Show unsafe
     * Null: check against our safe url list
     */
    val urlSafe: Boolean? = null,
    val grayLink: Boolean = false,
    override val type: String = "url_citation",
  ) : ApiContentReference()

  @Immutable
  data class GroupedUrlCitation(
    override val startIdx: Int?,
    override val endIdx: Int?,
    val items: List<UrlCitation>,
    override val type: String = "grouped_webpages",
  ) : ApiContentReference()

  data class FileCitation(
    override val startIdx: Int?,
    override val endIdx: Int?,
    override val type: String = "file_citation",
  ) : ApiContentReference()

  @Immutable
  data class ImageV2(
    override val startIdx: Int?,
    override val endIdx: Int?,
    val images: List<Image> = emptyList(),
    override val type: String = "image_v2",
  ) : ApiContentReference() {
    data class Image(
      val url: String,
      val contentUrl: String,
      val title: String,
      val thumbnailUrl: String,
      val thumbnailSize: Size?,
    ) {
      data class Size(
        val width: Int,
        val height: Int,
      )
    }
  }

  data class Title(
    override val startIdx: Int?,
    override val endIdx: Int?,
    val title: String,
    val description: String? = null,
    val url: String? = null,
    override val type: String = "title_citation",
  ) : ApiContentReference()

  @Immutable
  data class Tldr(
    override val startIdx: Int?,
    override val endIdx: Int?,
    val displayTitle: String,
    val url: String? = null,
    val breadcrumbs: List<String> = emptyList(),
    override val type: String = "tldr",
  ) : ApiContentReference()

  data class Calculator(
    override val startIdx: Int?,
    override val endIdx: Int?,
    val expression: String,
    val result: String,
    override val type: String = "calculator",
  ) : ApiContentReference()

  @Immutable
  data class NavList(
    override val startIdx: Int?,
    override val endIdx: Int?,
    val title: String? = null,
    val items: List<Item> = emptyList(),
    override val type: String = "nav_list",
  ) : ApiContentReference() {
    data class Item(
      val title: String,
      val url: String,
      val thumbnailUrl: String? = null,
      val attribution: String? = null,
    )
  }

  data class Time(
    override val startIdx: Int?,
    override val endIdx: Int?,
    val utcTime: Instant,
    val utcOffset: String,
    override val type: String = "time",
  ) : ApiContentReference()

  @Immutable
  data class Forecast(
    override val startIdx: Int?,
    override val endIdx: Int?,
    val forecast: ForecastResponse?,
    override val type: String = "forecast",
  ) : ApiContentReference() {
    @Immutable
    data class ForecastResponse(
      val location: Location,
      val current: Weather,
      val daily: List<Weather>,
      val hourly: List<Weather>,
    ) {
      data class Location(
        val name: String? = null,
        val state: String? = null,
        val country: String? = null,
        val lat: Double? = null,
        val lon: Double? = null,
      )

      data class Weather(
        val description: Description,
        val temperature: Temperature,
        val timestamp: Long,
        val utcOffsetSec: Double,
        /** If the weather is for night time */
        val night: Boolean,
      ) {
        data class Description(
          // https://learn.microsoft.com/en-us/azure/azure-maps/weather-services-concepts#unit-types
          val id: Int,
          val main: String,
          val description: String,
        )

        /**
         * All temperatures are in celsius.
         */
        data class Temperature(
          /** Celsius */
          val current: Double? = null,
          /** Celsius */
          val min: Double? = null,
          /** Celsius */
          val max: Double? = null,
        )
      }
    }
  }

  data class Video(
    override val startIdx: Int?,
    override val endIdx: Int?,
    val url: String,
    val videoId: String?,
    val title: String?,
    override val type: String = "video",
  ) : ApiContentReference() {
  }

  @Immutable
  data class SourcesFootnote(
    override val startIdx: Int?,
    override val endIdx: Int?,
    val hasImages: Boolean,
    val sources: List<Source> = emptyList(),
    val searchResultGroups: List<ApiSearchResultGroup> = emptyList(),
    val imageResults: List<ImageResult> = emptyList(),
    override val type: String = "sources_footnote",
  ) : ApiContentReference() {
    data class Source(
      val title: String,
      val url: String,
      val attribution: String?,
    )
  }

  companion object {
    const val MarkdownStartDelimiter = '\uEA01'
    const val MarkdownEndDelimiter = '\uEA02'
  }
}

data class ApiSearchResult(
  val url: String,
  val title: String? = null,
  val snippet: String? = null,
) {
  val contentAttributionUrl = url
}

@Immutable
data class ApiSearchResultGroup(
  val domain: String,
  val entries: List<ApiSearchResult> = emptyList(),
)

@Immutable
data class ImageResult(
  val contentUrl: String,
  val thumbnailUrl: String? = null,
  val title: String? = null,
  val thumbnailSize: Size? = null,
) {
  val contentAttributionUrl = contentUrl

  data class Size(
    val width: Int,
    val height: Int,
  )
}
