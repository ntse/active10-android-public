package com.flipsidegroup.active10.data.persistance.newapi

import com.flipsidegroup.active10.data.persistance.AppRepository
import com.flipsidegroup.active10.data.persistance.local.LocalRepository
import com.google.firebase.perf.util.Optional
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

const val SLUG_HERO_ARTICLE = "widget_android"
const val SLUG_WIDGET_ANDROID = "widget_android"
const val SLUG_WIDGET_IOS = "widget_ios"
const val PLATFORM_IOS = "ios"

@Singleton
class DiscoverRepository @Inject constructor(
    private val localRepository: LocalRepository,
    private val appRepository: AppRepository
) {

    fun getArticles() =
        localRepository.getDiscoveryArticles().flatMap { infoPages ->
            if (infoPages.isNotEmpty()) {
                Single.just(infoPages)
                    .map { it.filter { page -> page.slug != SLUG_WIDGET_IOS && page.platform != PLATFORM_IOS } }
                    .subscribeOn(Schedulers.io())
            } else {
                appRepository.getDiscoverArticles()
                    .subscribeOn(Schedulers.io())
                    .map { it.filter { page -> page.slug != SLUG_WIDGET_IOS && page.platform != PLATFORM_IOS } }
                    .doOnSuccess {
                        localRepository.persistDiscoveryArticles(it)
                    }
            }
        }

    fun getAndroidWidgetArticle() =
        getArticles().map { articles ->
            Optional.fromNullable(articles.firstOrNull {
                it.slug == SLUG_WIDGET_ANDROID
            })
        }

    fun getArticleBySlug(slug: String) =
        getArticles().map { articles ->
            Optional.fromNullable(articles.firstOrNull {
                it.slug == slug
            })
        }

    fun getIosWidgetArticle() =
        getArticles().map { articles ->
            Optional.fromNullable(articles.firstOrNull {
                it.slug == SLUG_WIDGET_ANDROID
            })
        }

    fun getCategories() = localRepository.getDiscoveryCategories().flatMap { categories ->
        if (categories.isNotEmpty()) {
            Single.just(categories)
                .subscribeOn(Schedulers.io())
        } else {
            appRepository.getDiscoverCategories()
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    localRepository.persistDiscoveryCategories(it)
                }
        }
    }
}