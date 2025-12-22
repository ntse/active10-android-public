package com.flipsidegroup.active10.data.models.api

import com.phe.betterhealth.components.carousel.ArticlePageWrapper

data class InfoPageWrapper(
    override val infoPage: InfoPage,
    override val missionId: Long? = null,
) : ArticlePageWrapper
