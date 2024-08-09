package com.zachklipp.richtext.sample

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.markdown.MarkdownParseOptions
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material.RichText
import com.halilibo.richtext.ui.resolveDefaults
import kotlinx.coroutines.delay

@Preview
@Composable private fun MarkdownSamplePreview() {
  MarkdownSample()
}

@Composable fun MarkdownSample() {
  var richTextStyle by remember { mutableStateOf(RichTextStyle().resolveDefaults()) }
  var isDarkModeEnabled by remember { mutableStateOf(false) }
  var isWordWrapEnabled by remember { mutableStateOf(true) }
  var markdownParseOptions by remember { mutableStateOf(MarkdownParseOptions.Default) }
  var isAutolinkEnabled by remember { mutableStateOf(true) }
  var restartAnimation by remember { mutableStateOf(1) }

  LaunchedEffect(isWordWrapEnabled) {
    richTextStyle = richTextStyle.copy(
      codeBlockStyle = richTextStyle.codeBlockStyle!!.copy(
        wordWrap = isWordWrapEnabled
      )
    )
  }
  LaunchedEffect(isAutolinkEnabled) {
    markdownParseOptions = markdownParseOptions.copy(
      autolink = isAutolinkEnabled
    )
  }

  val colors = if (isDarkModeEnabled) darkColors() else lightColors()
  val context = LocalContext.current

  MaterialTheme(colors = colors) {
    Surface {
      Column {
        // Config
        Card(elevation = 4.dp) {
          Column {
            CheckboxPreference(
              onClick = {
                restartAnimation += 1
              },
              checked = restartAnimation % 2 == 1,
              label = "Restart"
            )

//            CheckboxPreference(
//              onClick = {
//                isWordWrapEnabled = !isWordWrapEnabled
//              },
//              checked = isWordWrapEnabled,
//              label = "Word Wrap"
//            )
//
//            CheckboxPreference(
//              onClick = {
//                isAutolinkEnabled = !isAutolinkEnabled
//              },
//              checked = isAutolinkEnabled,
//              label = "Autolink"
//            )

//            RichTextStyleConfig(
//              richTextStyle = richTextStyle,
//              onChanged = { richTextStyle = it }
//            )
          }
        }

        SelectionContainer {
          Column(Modifier.verticalScroll(rememberScrollState())) {
            ProvideTextStyle(TextStyle(lineHeight = 1.3.em)) {
              RichText(
                style = richTextStyle,
                modifier = Modifier.padding(8.dp),
              ) {
                var textLength by remember { mutableStateOf(0) }

                LaunchedEffect(restartAnimation) {
                  val totalDuration = 100000L // 30 seconds
                  val delayPerChar = totalDuration / sampleMarkdown.length

                  for (i in 1..sampleMarkdown.length) {
                    textLength = i
                    delay(delayPerChar)
                  }
                }

                Markdown(
                  content = sampleMarkdown.take(textLength),
                  markdownParseOptions = markdownParseOptions,
                  onLinkClicked = {
                    Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                  }
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CheckboxPreference(
  onClick: () -> Unit,
  checked: Boolean,
  label: String
) {
  Row(
    Modifier.clickable(onClick = onClick),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Checkbox(
      checked = checked,
      onCheckedChange = { onClick() },
    )
    Text(label)
  }
}

private val sampleMarkdown = """
  # Demo
  Based on (this cheatsheet)[cheatsheet]

  ---
  
  
  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent tristique velit ac enim fermentum, vitae malesuada purus vulputate. Fusce sed ex sit amet arcu elementum faucibus. Integer sed dolor eu odio vehicula tempor. Vivamus sit amet ligula non ante placerat aliquet. Etiam sagittis tellus in nisi laoreet, ut suscipit felis vestibulum. Nullam efficitur sapien id ante varius, in bibendum mauris dictum. In aenean fringilla orci, a tempor arcu condimentum at. Aliquam malesuada sem nec nulla scelerisque dapibus. Proin ac orci non erat pharetra tincidunt et et purus. Mauris consectetur mi vitae ex pharetra, in vulputate urna ultricies. Vivamus quis mauris eget quam euismod cursus.

  Aenean facilisis, erat a hendrerit luctus, justo arcu dictum turpis, a malesuada nisi nisl non dolor. Quisque vitae leo ac ligula dapibus vestibulum. Suspendisse potenti. Etiam euismod arcu in justo aliquet, quis malesuada arcu cursus. Ut aliquet, ex vel posuere venenatis, magna lorem cursus risus, ut elementum augue enim vel augue. Fusce scelerisque, dolor sit amet tincidunt elementum, justo leo porttitor lacus, et ultricies elit sem ac nisl. In hac habitasse platea dictumst. Sed faucibus nisl in urna luctus, vel luctus leo facilisis. Cras tempus, eros in dictum consectetur, turpis leo efficitur nulla, at gravida metus risus nec lacus. Morbi tincidunt urna non magna malesuada, at efficitur felis aliquet.

  
  1 2 3 4 5 6 7 8 9 10 11,   

  12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251, 252, 253, 254, 255, 256, 257, 258, 259, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 278, 279, 280, 281, 282, 283, 284, 285, 286, 287, 288, 289, 290, 291, 292, 293,


  326 327 328 329 330 331 332 333 334 335 336 337 338 339 340 341 342 343 344 345 346 347 348 349 350 351 352 353 354 355 356 357 358 359 360 361 362 363 364 365 366 367 368 369 370 371 372 373 374 375 376 377 378 379 380 381 382 383 384 385 386 387 388 389 390 391 392 393 394 395 396 397 398 399 400 401 402 403 404 405 406 407 408 409 410 411 412 413 414 415 416 417 418 419 420 421 422 423 424 425 426 427 428 429 430 431 432 433 434 435 436 437 438 439 440 441 442 443 444 445 446 447 448 449 450 451 452 453 454 455 456 457 458 459 460 461 462 463 464 465 466 467 468 469 470 471 472 473 474 475 476 477 478 479 480 481 482 483 484 485 486 487 488 489 490 491 492 493 494 495 496 497 498 499 500 501 502 503 504 505 506 507 508 509 510 511 512 513 514 515 516 517 518 519 520 521 522 523 524 525 526 527 528 529 530 531 532 533 534 535 536 537 538 539 540 541 542 543 544 545 546 547 548 549 550 551 552 553 554 555 556 557 558 559 560 561 562 563 564 565 566 567 568 569 570 571 572 573 574 575 576 577 578 579 580 581 582 583 584 585 586 587 588 589 590 591 592 593 594 595 596 597 598 599 600 601 602 603 604 605 606 607 608 609 610 611 612 613 614 615 616 617 618 619 620 621 622 623 624 625 626 627 628 629 630 631 632 633 634 635 636 637 638 639 640 641 642 643 644 645 646 647 648 649 650 651 652 653 654 655 656 657 658 659 660 661 662 663 664 665 666 667 668 669 670 671 672 673 674 675 676 677 678 679 680 681 682 683 684 685 686 687 688 689 690 691 692 693 694 695 696 697 698 699 700





  ---

  ## Headers
  ---
  # Header 1
  ## Header 2
  ### Header 3
  #### Header 4
  ##### Header 5
  ###### Header 6
  ---
  
  ## Full-bleed Image
  ![](https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Image_created_with_a_mobile_phone.png/1920px-Image_created_with_a_mobile_phone.png)

  ## Images smaller than the width should center
  ![](https://cdn.nostr.build/p/4a84.png)
  
  On LineHeight bug, the image below goes over this text. 
  ![](https://cdn.nostr.build/p/PxZ0.jpg)

  ## Emphasis

  Emphasis, aka italics, with *asterisks* or _underscores_.

  Strong emphasis, aka bold, with **asterisks** or __underscores__.

  Combined emphasis with **asterisks and _underscores_**.

  ---

  ## Lists
  1. First ordered list item
  2. Another item
      * Unordered sub-list.
  1. Actual numbers don't matter, just that it's a number
      1. Ordered sub-list
  4. And another item.

      You can have properly indented paragraphs within list items. Notice the blank line above, and the leading spaces (at least one, but we'll use three here to also align the raw Markdown).

      To have a line break without a paragraph, you will need to use two trailing spaces.
      Note that this line is separate, but within the same paragraph.
      (This is contrary to the typical GFM line break behaviour, where trailing spaces are not required.)

  * Unordered list can use asterisks
  - Or minuses
  + Or pluses

  ---

  ## Links

  [I'm an inline-style link](https://www.google.com)

  [I'm a reference-style link][Arbitrary case-insensitive reference text]

  [I'm a relative reference to a repository file](../blob/master/LICENSE)

  [You can use numbers for reference-style link definitions][1]

  Or leave it empty and use the [link text itself].
  
  Autolink option will detect text links like https://www.google.com and turn them into Markdown links automatically.

  ---

  ## Code

  Inline `code` has `back-ticks around` it.

  ```javascript
  var s = "JavaScript syntax highlighting";
  alert(s);
  ```

  ```python
  s = "Python syntax highlighting"
  print s
  ```

  ```java
  /**
   * Helper method to obtain a Parser with registered strike-through &amp; table extensions
   * &amp; task lists (added in 1.0.1)
   *
   * @return a Parser instance that is supported by this library
   * @since 1.0.0
   */
  @NonNull
  public static Parser createParser() {
    return new Parser.Builder()
        .extensions(Arrays.asList(
            StrikethroughExtension.create(),
            TablesExtension.create(),
            TaskListExtension.create()
        ))
        .build();
  }
  ```

  ```xml
  <ScrollView
    android:id="@+id/scroll_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginTop="?android:attr/actionBarSize">

    <TextView
      android:id="@+id/text"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:layout_margin="16dip"
      android:lineSpacingExtra="2dip"
      android:textSize="16sp"
      tools:text="yo\nman" />

  </ScrollView>
  ```

  ```
  No language indicated, so no syntax highlighting.
  But let's throw in a <b>tag</b>.
  ```
  
  ---

  ## Images
  
  Inline-style:
   
  ![random image](https://picsum.photos/seed/picsum/400/400)
  
  ![random image](https://picsum.photos/seed/picsum/400/400 "Text 1")
  
  Reference-style:
   
  ![random image][logo]
  
  [logo]: https://picsum.photos/seed/picsum2/400/400 "Text 2"

  ---

  ## Tables

  Colons can be used to align columns.

  | Tables        | Are           | Cool  |
  | ------------- |:-------------:| -----:|
  | col 3 is      | right-aligned | ${'$'}1600 |
  | col 2 is      | centered      |   ${'$'}12 |
  | zebra stripes | are neat      |    ${'$'}1 |

  There must be at least 3 dashes separating each header cell.
  The outer pipes (|) are optional, and you don't need to make the
  raw Markdown line up prettily. You can also use inline Markdown.

  Markdown | Less | Pretty
  --- | --- | ---
  *Still* | `renders` | ![random image](https://picsum.photos/seed/picsum/400/400 "Text 1")
  1 | 2 | 3

  ---

  ## Blockquotes

  > Blockquotes are very handy in email to emulate reply text.
  > This line is part of the same quote.

  Quote break.

  > This is a very long line that will still be quoted properly when it wraps. Oh boy let's keep writing to make sure this is long enough to actually wrap for everyone. Oh, you can *put* **Markdown** into a blockquote.

  Nested quotes
  > Hello!
  >> And to you!

  ---

  ## Inline HTML

  ```html
  <u><i>H<sup>T<sub>M</sub></sup><b><s>L</s></b></i></u>
  ```

  <body><u><i>H<sup>T<sub>M</sub></sup><b><s>L</s></b></i></u></body>

  ---

  ## Horizontal Rule

  Three or more...

  ---

  Hyphens (`-`)

  ***

  Asterisks (`*`)

  ___

  Underscores (`_`)


  ## License

  ```
    Copyright 2019 Dimitry Ivanov (legal@noties.io)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
  ```

  [cheatsheet]: https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet

  [arbitrary case-insensitive reference text]: https://www.mozilla.org
  [1]: http://slashdot.org
  [link text itself]: http://www.reddit.com
""".trimIndent()