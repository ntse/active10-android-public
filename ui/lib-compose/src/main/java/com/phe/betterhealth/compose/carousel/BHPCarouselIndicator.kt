package com.phe.betterhealth.compose.carousel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.phe.betterhealth.compose.R
import com.phe.betterhealth.compose.theme.BetterHealthTheme
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun BHCarouselIndicator(pagerState: PagerState, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    val previousItemIndex = pagerState.currentPage - 1
    val nextItemIndex = pagerState.currentPage + 1
    val nextElementsCount = pagerState.pageCount - nextItemIndex
    val currentPage = if (pagerState.pageCount > 0) pagerState.currentPage + 1 else 0

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        if (previousItemIndex >= 0) {
            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(previousItemIndex)
                    }
                },
                modifier = Modifier
                    .semantics {
                        traversalIndex = 0f
                        contentDescription = buildString {
                            append("Move carousel to previous slide, ")
                            append(previousItemIndex + 1)
                            append(" previous slide")
                            if (previousItemIndex > 0) append("s")
                            append(" available")
                        }
                    }
                    .height(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.bh_indicator_left),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                )
            }
        } else {
            Spacer(Modifier.width(48.dp))
        }
        Text(
            text = buildString {
                append(pagerState.currentPage + 1)
                append(" of ")
                append(pagerState.pageCount)
            },
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.roboto_bold)),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                letterSpacing = 0.2.sp,
            ),
            modifier = Modifier
                .semantics {
                    traversalIndex = 1f
                    contentDescription =
                        "Slide $currentPage of ${pagerState.pageCount} is currently selected"
                }
        )
        if (nextElementsCount > 0) {
            IconButton(
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(nextItemIndex)
                    }
                },
                modifier = Modifier
                    .semantics {
                        traversalIndex = 2f
                        contentDescription = buildString {
                            append("Move carousel to next slide, ")
                            append(nextElementsCount)
                            append(" next slide")
                            if (nextElementsCount > 0) append("s")
                            append(" available")
                        }
                    }
                    .height(48.dp)

            ) {
                Icon(
                    painter = painterResource(R.drawable.bh_indicator_right),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                )
            }
        } else {
            Spacer(Modifier.width(48.dp))
        }
    }
}

@Preview
@Composable
@OptIn(ExperimentalFoundationApi::class)
fun BHCarouselIndicatorPreview() {
    BetterHealthTheme {
        val state = rememberPagerState(
            initialPage = 0,
            pageCount = { 5 },
        )
        HorizontalPager(state) {
            Text("Page ${it + 1}")
        }
        BHCarouselIndicator(state)
    }
}
