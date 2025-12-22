package com.flipsidegroup.active10.presentation.walksneardetails.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.recyclerview.widget.LinearLayoutManager
import coil.compose.AsyncImage
import com.flipsidegroup.active10.R
import com.flipsidegroup.active10.data.models.response.CuratedWalk
import com.flipsidegroup.active10.databinding.ActivityWalkNearDetailsBinding
import com.flipsidegroup.active10.presentation.common.activities.BaseSecureActivity
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.fulscreenphoto.FullScreenPhotoActivityIntent
import com.flipsidegroup.active10.presentation.walksneardetails.adapter.WalkNearAttributesAdapter
import com.flipsidegroup.active10.presentation.walksneardetails.dialog.GoJauntlyDialog
import com.flipsidegroup.active10.presentation.walksneardetails.presenter.WalksNearDetailsPresenter
import com.flipsidegroup.active10.presentation.walksneardetails.view.WalksNearDetailsView
import com.flipsidegroup.active10.utils.convertSecondsToMinAndHoursString
import com.flipsidegroup.active10.utils.lifecycleAwareVariable
import com.phe.betterhealth.widgets.utils.setGuidelineBeginToTopInset
import timber.log.Timber
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.absoluteValue

const val PARAM_WALK = "param_walk"

fun Context.WalkNearDetailsIntent(curatedWalk: CuratedWalk): Intent {
    return Intent(this, WalksNearDetailsActivity::class.java).apply {
        putExtra(PARAM_WALK, curatedWalk)
    }
}

class WalksNearDetailsActivity : BaseSecureActivity<WalksNearDetailsView>(), WalksNearDetailsView {

    override fun getPresenter(): LifecycleAwarePresenter<WalksNearDetailsView> = presenter

    private var binding: ActivityWalkNearDetailsBinding by lifecycleAwareVariable()

    private val attributesAdapter by lazy { WalkNearAttributesAdapter() }

    @Inject
    lateinit var presenter: WalksNearDetailsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(
            ActivityWalkNearDetailsBinding.inflate(layoutInflater).apply { binding = this }.root
        )
        with(binding) {
            top.setGuidelineBeginToTopInset()
        }
        binding.toolbar.backTV.setOnClickListener { onBackPressed() }
        binding.toolbar.titleTV.text = getString(R.string.walks_near_me_title)

        val walk: CuratedWalk = intent.getParcelableExtra(PARAM_WALK)!!
        setUpAttributesRecyclerView(walk)
        setUpViews(walk)

        presenter.loadWalk(walk.id)
    }

    private fun setUpAttributesRecyclerView(walk: CuratedWalk) {
        binding.attributesContainer.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.attributesContainer.adapter = attributesAdapter
        attributesAdapter.submitList(walk.attributes)
    }

    private fun setUpViews(walk: CuratedWalk) {
        binding.title.text = walk.title
        binding.location.text = walk.location
        binding.summary.text = walk.summary
        val duration = walk.duration.toDouble().convertSecondsToMinAndHoursString()
        val distance =
            walk.distance.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toDouble()
        binding.measurableDetailsText.text = "$duration / ${distance}km"

        binding.walkDetailsStartWithButton.setOnClickListener {
            firebaseAnalyticsHelper.sendWalksNearButtonClickedEvent(
                ctaName = "StartWalkInGoJauntly",
                walkName = walk.title
            )
            GoJauntlyDialog(walk.id, walk.url).show(supportFragmentManager, null)
        }

        binding.walkDetailsSeeOverviewButton.setOnClickListener {
            (walk.mapImage?.large?.jpeg?.oneX)?.let {
                firebaseAnalyticsHelper.sendWalksNearButtonClickedEvent(
                    ctaName = "SeeTheWalkOverview",
                    walkName = walk.title
                )
                startActivity(FullScreenPhotoActivityIntent(it, "OPEN_GO_JAUNTLY", getString(R.string.walks_near_details_start_with_go_jauntly), walk.url, walk.id, walk.title))
            }
        }

        walk.steps?.fold(listOf(walk.image)) { images, step -> images.plus(step.image) }
            ?.let { images ->
                Timber.d("Images: $images")
                buildImageCarousel(images.filterNot { it.isBlank() })
            }
    }

    private fun buildImageCarousel(images: List<String>) {
        binding.imageCarousel.setContent {
            Carousel(images = images) {
                startActivity(FullScreenPhotoActivityIntent(it))
            }
        }

    }

    override fun updateWalk(result: CuratedWalk?) {
        result?.let {
            setUpViews(it)
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Carousel(images: List<String>, onClick: (String) -> Unit) {
    val state = rememberPagerState(
        initialPage = 0,
        pageCount = { images.size },
    )
    var desiredPage by remember { mutableStateOf(-1) }
    LaunchedEffect(desiredPage) {
        if (desiredPage >= 0) {
            state.animateScrollToPage(desiredPage)
        }
    }

    Column {
        HorizontalPager(
            state,
            contentPadding = PaddingValues(horizontal = 120.dp),
            pageSpacing = 32.dp
        ) { page ->
            val pageOffset =
                ((state.currentPage - page) + state.currentPageOffsetFraction).absoluteValue
            val darknessFactor = 1f - (0.4f * pageOffset.coerceIn(0f, 1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .carouselTransition(pageOffset)
            ) {
                AsyncImage(
                    model = images[page],
                    contentDescription = "Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onClick(images[page]) },
                    contentScale = ContentScale.Crop,
                    colorFilter = ColorFilter.tint(
                        Color.Black.copy(alpha = 1f - darknessFactor),
                        blendMode = BlendMode.Multiply
                    )
                )
            }
        }

        IndicatorContainer(
            currentIndex = state.currentPage + 1,
            maxIndex = images.size,
            onBackClicked = {
                desiredPage = (state.currentPage - 1).coerceAtLeast(0)
            },
            onNextClicked = {
                desiredPage = (state.currentPage + 1).coerceAtMost(images.size - 1)
            }
        )
    }

}

fun Modifier.carouselTransition(pageOffset: Float) =
    graphicsLayer {
        val transformation =
            lerp(
                start = 0.85f,
                stop = 1f,
                fraction = 1f - pageOffset.coerceIn(0f, 1f)
            )
        scaleY = transformation
    }

@Composable
fun IndicatorContainer(
    currentIndex: Int,
    maxIndex: Int,
    onBackClicked: () -> Unit,
    onNextClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { onBackClicked() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent
            ),
            contentPadding = PaddingValues(start = 0.dp, end = 16.dp),
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Back",
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight(700)
            )
        }

        Text(
            "$currentIndex of $maxIndex",
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.SansSerif, fontWeight = FontWeight(700),
            color = Color.Black,
            fontSize = 16.sp,
        )

        Button(
            onClick = { onNextClicked() },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent
            ),
            contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
        ) {
            Text(
                "Next",
                color = Color.Black,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight(700)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_next),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}