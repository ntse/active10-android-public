package com.flipsidegroup.active10.presentation.couch

import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface CouchAdvertView : BaseView {

    fun showContent(content: ScreenContent)

}