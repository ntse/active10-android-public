package com.flipsidegroup.active10.presentation.home.presenter

import androidx.core.net.toUri
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.home.adapters.DISCOVER_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.home.adapters.MY_WALK_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.home.adapters.SETTINGS_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.home.adapters.TODAY_WALK_SCREEN_POSITION
import com.flipsidegroup.active10.presentation.home.view.DeepLinkView
import com.flipsidegroup.active10.utils.Constants.ScreenInfoParameters.EXTERNAL_LINK
import com.flipsidegroup.active10.utils.Constants.ScreenInfoParameters.FIRST_BUTTON_TITLE
import com.flipsidegroup.active10.utils.Constants.ScreenInfoParameters.INTERNAL_LINK
import com.flipsidegroup.active10.utils.Constants.ScreenInfoParameters.SECOND_BUTTON_TITLE
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import kotlin.collections.orEmpty
import kotlin.text.isNullOrEmpty
import kotlin.text.toLong

class DeepLinkPresenter @Inject internal constructor(
    private val screenRepository: ScreenRepository
) : BasePresenter<DeepLinkView>() {
    private var screenInfo: ScreenContent? = null

    fun loadScreenData() {
        screenRepository.getScreenContentBySlug(ScreenRepository.SLUG_DEEP_LINKING_MODAL)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { screen ->
                screenInfo = screen

                view?.onScreenLoaded(
                    title = screen.title,
                    description = screen.description,
                    iconUrl = screen.firstImageUrl,
                    firstButtonTitle = screen.getPropertyValue(FIRST_BUTTON_TITLE) ?: "",
                    secondButtonTitle = screen.getPropertyValue(SECOND_BUTTON_TITLE) ?: "",
                )
            }
            .addToDisposables()
    }

    // Link format - active10://campaign/today
    fun validateInternalLinkAndGetHomeScreenTab(): Int? {
        val internalLinkUri = getInternalLink()?.toUri()
        val pathSegments = internalLinkUri?.pathSegments.orEmpty()

        if (pathSegments.isNotEmpty() && internalLinkUri?.host == PATH_CAMPAIGN) {
            val destination = pathSegments[0]

            val homeTabToSelect = when (destination) {
                DESTINATION_TODAY -> TODAY_WALK_SCREEN_POSITION
                DESTINATION_WALKS -> MY_WALK_SCREEN_POSITION
                DESTINATION_DISCOVER -> DISCOVER_SCREEN_POSITION
                DESTINATION_SETTINGS -> SETTINGS_SCREEN_POSITION

                else -> TODAY_WALK_SCREEN_POSITION
            }

            return homeTabToSelect
        }

        return null
    }

    // Link format - active10://campaign/discover/123
    fun parseArticleIdFromInternalLink(): Long? {
        val articleId = parseArticleIdentifier()

        if (!articleId.isNullOrEmpty()) {
            try {
                val parsedArticleId = articleId.toLong()
                return parsedArticleId
            } catch (_: Throwable) {
                return null
            }
        }

        return null
    }

    // Link format active10://campaign/discover/some_article_slug
    fun parseArticleSlugFromInternalLink(): String? {
        val articleId = parseArticleIdentifier()

        // If the article id can't be parsed, it means that there is an article slug
        if (parseArticleIdFromInternalLink() == null) {
            return articleId
        }

        return null
    }

    fun getExternalLink(): String? {
        return screenInfo?.getPropertyValue(EXTERNAL_LINK)
    }

    private fun getInternalLink(): String? {
        return screenInfo?.getPropertyValue(INTERNAL_LINK)
    }

    private fun parseArticleIdentifier(): String? {
        val internalLinkUri = getInternalLink()?.toUri()
        val pathSegments = internalLinkUri?.pathSegments.orEmpty()

        if (pathSegments.size < 2) return null
        if (pathSegments[0] != DESTINATION_DISCOVER) return null

        return pathSegments[1].trim()
    }

    companion object {
        private const val PATH_CAMPAIGN = "campaign"
        private const val DESTINATION_TODAY = "today"
        private const val DESTINATION_WALKS = "walks"
        private const val DESTINATION_DISCOVER = "discover"
        private const val DESTINATION_SETTINGS = "settings"
    }
}