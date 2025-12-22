package com.flipsidegroup.active10.presentation.couch

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface CouchAdvertPresenter : LifecycleAwarePresenter<CouchAdvertView> {

    fun loadContent()

}