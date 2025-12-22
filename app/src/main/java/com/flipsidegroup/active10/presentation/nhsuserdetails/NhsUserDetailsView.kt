package com.flipsidegroup.active10.presentation.nhsuserdetails

import com.flipsidegroup.active10.data.models.api.NhsUserDetails
import com.flipsidegroup.active10.data.models.api.ScreenContent
import com.flipsidegroup.active10.presentation.common.view.BaseView

interface NhsUserDetailsView : BaseView {

    fun onUpdateDialogContentReceived(content: ScreenContent)

    fun showContent(content: ScreenContent)

    fun showData(nhsUser: NhsUserDetails?)

}