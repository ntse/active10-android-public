package com.flipsidegroup.active10.presentation.walksnear.presenter

import android.location.Address
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.models.response.CircularWalkResponse
import com.flipsidegroup.active10.data.models.response.CuratedWalksResponse
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.data.persistance.walksnear.WalksNearRepository
import com.flipsidegroup.active10.data.preferences.SettingsUtils
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.walksnear.adapters.WalksNearPart
import com.flipsidegroup.active10.presentation.walksnear.view.WalksNearView
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.await
import timber.log.Timber
import java.math.RoundingMode
import javax.inject.Inject

data class WalksNearPresenterResult(
    val circularWalks: List<CircularWalkResponse>,
    val curatedWalks: CuratedWalksResponse,
    val coordinates: Address?,
    val viewContent: ScreenContent,
)

class WalksNearPresenterImpl @Inject constructor(
    private val screenRepository: ScreenRepository,
    private val walksNearRepository: WalksNearRepository,
    private val settingsUtils: SettingsUtils,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
) : BasePresenter<WalksNearView>(), WalksNearPresenter {

    private val presenterScope = CoroutineScope(Dispatchers.Main + Job())

    override fun loadContent() {
        view?.showLoading()

        presenterScope.launch {
            val userPostcode = settingsUtils.getSettingsHolder().nhsUser?.postcode

            if (userPostcode.isNullOrBlank()) {
                runCatching {
                    getView().await()
                }.onSuccess {
                    val introMapped = WalksNearPart.Intro(
                        title = it.getPropertyValue("intro_title") ?: "",
                        description = it.getPropertyValue("intro_description") ?: "",
                    )
                    val noPostcodeMapped = WalksNearPart.NoPostcode(
                        description = it.getPropertyValue("header_text_no_postcode") ?: "",
                    )

                    val list = buildList {
                        add(introMapped)
                        add(noPostcodeMapped)
                        add(WalksNearPart.Credentials())
                    }

                    view?.hideLoading()
                    view?.showData(list)
                }.onFailure {
                    Timber.e(it)
                    view?.hideLoading()
                    view?.showAlert(it)
                }
            } else {
                runCatching {
                    WalksNearPresenterResult(
                        walksNearRepository.getCircularWalks(userPostcode).await(),
                        walksNearRepository.getCuratedWalks(userPostcode).await(),
                        walksNearRepository.getPostcodeCoordinates(userPostcode)?.await(),
                        getView().await(),
                    )
                }.onSuccess { (circularWalks, curatedWalks, coordinates, viewContent) ->
                    var index = 0
                    val introMapped = WalksNearPart.Intro(
                        title = viewContent.getPropertyValue("intro_title") ?: "",
                        description = viewContent.getPropertyValue("intro_description") ?: "",
                    )
                    val bannerMapped = WalksNearPart.Banner(
                        description = viewContent.getPropertyValue("header_text") ?: "",
                        postcode = userPostcode,
                    )
                    val funcItemsMapped = curatedWalks.walks.map { walk ->
                        index += 1
                        WalksNearPart.CuratedWalk(
                            id = index,
                            title = walk.title,
                            image = walk.thumbnailImage,
                            location = walk.location,
                            duration = walk.duration.toDouble(),
                            distance = walk.distance,
                            summary = walk.summary,
                            btnTitle = "Explore this walk",
                            onClickCallback = {
                                firebaseAnalyticsHelper.sendWalksNearButtonClickedEvent(
                                    ctaName = "ExploreThisWalk",
                                    walkName = walk.title
                                )
                                view?.navigateToCuratedWalkDetails(walk)
                            },
                            postcodeDistance = coordinates?.let {
                                walk
                                    .getDistanceFromPostcode(it)
                                    .times(0.001)   // convert to km
                                    .toBigDecimal()
                                    .setScale(1, RoundingMode.HALF_UP)
                                    .toDouble()
                            },
                        )
                    }
                    val circularMapped = circularWalks.map { walk ->
                        index += 1
                        WalksNearPart.CircularWalk(
                            id = index,
                            title = walk.title,
                            image = walk.thumbnailUrl ?: "",
                            duration = walk.duration,
                            distance = walk.distance,
                            btnTitle = "Explore this walk",
                            onClickCallback = {
                                firebaseAnalyticsHelper.sendWalksNearButtonClickedEvent(
                                    ctaName = "ExploreThisWalk",
                                    walkName = walk.title
                                )
                                view?.navigateToCircularWalkDetails(walk)
                            }
                        )
                    }
                    val list = buildList {
                        add(introMapped)
                        addAll(funcItemsMapped)
                        addAll(circularMapped)
                        add(bannerMapped)
                        add(WalksNearPart.Credentials())
                    }
                    view?.hideLoading()
                    view?.showData(list)
                }.onFailure {
                    view?.hideLoading()
                    view?.showAlert(it)
                }
            }
        }
    }

    private fun getView() = screenRepository.getScreenContentBySlug("walks_in_my_area_view")
        .subscribeOn(Schedulers.io())

    override fun unbind() {
        presenterScope.coroutineContext.cancelChildren()
        super.unbind()
    }
}