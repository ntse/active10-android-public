package com.flipsidegroup.active10.presentation.discover.presenter

import com.flipsidegroup.active10.data.models.api.InfoPage
import com.flipsidegroup.active10.data.models.api.InfoPageWrapper
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.data.persistance.newapi.DiscoverRepository
import com.flipsidegroup.active10.data.persistance.newapi.ScreenRepository
import com.flipsidegroup.active10.presentation.common.presenter.BasePresenter
import com.flipsidegroup.active10.presentation.discover.adapter.GeneralInfoItem
import com.flipsidegroup.active10.presentation.discover.adapter.GeneralScreen
import com.flipsidegroup.active10.presentation.discover.adapter.GeneralScreenItem
import com.flipsidegroup.active10.presentation.discover.adapter.GeneralViewType
import com.flipsidegroup.active10.presentation.discover.view.DiscoverView
import com.flipsidegroup.active10.utils.analytics.FirebaseAnalyticsHelper
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit


class DiscoverPresenterImpl internal constructor(
    private val screenRepository: ScreenRepository,
    private val discoverRepository: DiscoverRepository,
    private val firebaseAnalyticsHelper: FirebaseAnalyticsHelper
) : BasePresenter<DiscoverView>(), DiscoverPresenter {

    private val selectedTab: BehaviorSubject<ScreenContent> = BehaviorSubject.create()
    private val searchText: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    override fun loadData() {
        view?.showLoading()

        val discoverScreens = getDiscoverScreens()
        val articles = getArticles()

        val itemsGroupedByCategories = Single.zip(discoverScreens, articles, this::createGeneralScreenItems)
            .subscribeOn(Schedulers.io())

        discoverScreens.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                selectedTab.onNext(it.first())
                view?.showTabs(it)
            }, {
                Timber.e(it)
                view?.showAlert(it)
            }).addToDisposables()

        Observable.combineLatest(
                itemsGroupedByCategories.toObservable(),
                selectedTab.debounce(500, TimeUnit.MILLISECONDS),
                searchText.debounce(500, TimeUnit.MILLISECONDS),
                this::filterItems
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                view?.showData(it.first, it.second)
                view?.hideLoading()
            }, {
                Timber.e(it)
                view?.showAlert(it)
            }).addToDisposables()
    }

    private fun createGeneralScreenItems(
        screens: List<ScreenContent>,
        articles: List<InfoPage>
    ) = screens.map { screen ->
        GeneralScreenItem(
            item = screen,
            infoPages = articles
                .filter { it.id in screen.infoPageIds.orEmpty() }
                .sortedBy { screen.infoPageIds?.indexOf(it.id) }
                .map { InfoPageWrapper(it) }
        )
    }.filterEmptyScreens()
        .flattenScreen()

    // return list of items, and boolean value if we should show empty state
    private fun filterItems(
        items: List<GeneralScreen>,
        selectedTab: ScreenContent,
        searchText: String
    ) = if (searchText.isBlank()) {
        items.filter { it.filterByTab(selectedTab) }
    } else {
        items.filterByTextAndSort(searchText)
    }.let { objects ->
        firebaseAnalyticsHelper.discoverSearch(searchText, objects.size)
        val results = if (searchText.isNotBlank()) objects.map { it.showAsArticleItem() } else objects
        results to (searchText.isNotBlank() && objects.isEmpty())
    } // return list and showEmptyState

    private fun List<GeneralScreen>.filterByTextAndSort(searchQuery: String): List<GeneralScreen> {
        fun GeneralScreen.matchPriority(query: String): Pair<Int, Long> = when (this) {
            is GeneralScreenItem -> when {
                item.title.contains(query, ignoreCase = true) -> 1 to item.id
                item.description.contains(query, ignoreCase = true) -> 2 to item.id
                infoPages.any { it.infoPage.content.joinToString { item -> item.body ?: "" }.contains(searchQuery, ignoreCase = true) }
                || infoPages.any { it.infoPage.description.contains(searchQuery, ignoreCase = true) }
                || infoPages.any { it.infoPage.title.contains(searchQuery, ignoreCase = true) } -> 3 to item.id
                else -> 0 to item.id
            }
            is GeneralInfoItem -> when {
                item.infoPage.title.contains(query, ignoreCase = true) -> 1 to item.infoPage.id
                item.infoPage.description.contains(query, ignoreCase = true) -> 2 to item.infoPage.id
                item.infoPage.content.joinToString { it.body ?: "" }.contains(query, ignoreCase = true) -> 3 to item.infoPage.id
                else -> 0 to item.infoPage.id
            }
        }

        return this
            .map { it.matchPriority(searchQuery) to it }
            .filter { (priority, _) -> priority.first > 0 }
            .sortedBy { (priority, _) -> priority.first }
            .map { (_, item) -> item }
    }

    private fun GeneralScreen.filterByText(searchQuery: String): Boolean {
        return when (this) {
            is GeneralScreenItem -> item.title.contains(searchQuery, ignoreCase = true)
                    || item.description.contains(searchQuery, ignoreCase = true)
                    || infoPages.any{ it.infoPage.content.joinToString { it.body ?: "" }.contains(searchQuery, ignoreCase = true) }
                    || infoPages.any{ it.infoPage.description.contains(searchQuery, ignoreCase = true) }
                    || infoPages.any{ it.infoPage.title.contains(searchQuery, ignoreCase = true) }

            is GeneralInfoItem ->  item.infoPage.title.contains(searchQuery, ignoreCase = true)
                    || item.infoPage.description.contains(searchQuery, ignoreCase = true)
                    || item.infoPage.content.joinToString{ it.body ?: "" }.contains(searchQuery, ignoreCase = true)
                    || screenContent.title.contains(searchQuery, ignoreCase = true)
        }
    }

    private fun GeneralScreen.filterByTab(tab: ScreenContent): Boolean {
        return when (this) {
            is GeneralScreenItem -> item.id == tab.id
            is GeneralInfoItem -> screenContent.id == tab.id
        }
    }

    private fun GeneralScreen.showAsArticleItem(): GeneralScreen {
        return when (this) {
            is GeneralScreenItem -> GeneralScreenItem(item, infoPages, GeneralViewType.ARTICLE_LIST_ITEM)
            is GeneralInfoItem ->  GeneralInfoItem(item, screenContent, GeneralViewType.ARTICLE_LIST_ITEM)
        }
    }

    private fun List<GeneralScreenItem>.filterEmptyScreens() = filter {
        when (it.viewType) {
            GeneralViewType.CAROUSEL_DOUBLE_ITEMS,
            GeneralViewType.CAROUSEL_LARGE_ITEMS,
            GeneralViewType.CAROUSEL_SMALL_ITEMS -> it.infoPages.isNotEmpty()
            GeneralViewType.ROOT_PAGE,
            GeneralViewType.UNKNOWN -> {
                Timber.e("Unknown view type: ${it.item.type}")
                false
            }

            else -> true
        }
    }

    private fun List<GeneralScreenItem>.flattenScreen() = flatMap { screenItem ->
        when (screenItem.viewType) {
            GeneralViewType.ARTICLE_LIST -> {
                buildList {
                    addAll(
                        screenItem.infoPages
                            .map {
                                GeneralInfoItem(
                                    it,
                                    screenItem.item,
                                    GeneralViewType.ARTICLE_LIST_ITEM
                                )
                            }
                    )
                }
            }

            GeneralViewType.ARTICLE_LIST_SMALL_ITEMS -> {
                buildList {
                    addAll(
                        screenItem.infoPages
                            .map {
                                GeneralInfoItem(
                                    it,
                                    screenItem.item,
                                    GeneralViewType.ARTICLE_LIST_SMALL_ITEM_ELEMENT
                                )
                            }
                    )
                }
            }

            GeneralViewType.HERO_ITEM -> {
                screenItem.infoPages
                    .map { GeneralInfoItem(it, screenItem.item, GeneralViewType.HERO_ITEM) }
                    .take(1)
            }

            else -> listOf(screenItem)
        }
    }

    private fun getArticles() = discoverRepository
        .getArticles()
        .subscribeOn(Schedulers.io())

    private fun getCategories() = discoverRepository
        .getCategories()
        .subscribeOn(Schedulers.io())

    private fun getDiscoverScreens() = screenRepository
        .getScreenContentBySlug(ScreenRepository.SLUG_DISCOVER)
        .flatMap { screenRepository.getScreensByIds(it.childrenIds.orEmpty()) }
        .subscribeOn(Schedulers.io())

    override fun selectTab(tab: ScreenContent) {
        firebaseAnalyticsHelper.sendViewScreenEvent("DiscoverView", tab = tab.title)
        selectedTab.onNext(tab)
    }

    override fun enterSearchText(text: String) {
        searchText.onNext(text)
    }

}