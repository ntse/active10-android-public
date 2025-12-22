package com.flipsidegroup.active10.presentation.nhsuserdetails

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter

interface NhsUserDetailsPresenter : LifecycleAwarePresenter<NhsUserDetailsView> {

    fun getUpdateDialogContent()

    fun loadContent()

    fun loadData()

    fun updateData()

}