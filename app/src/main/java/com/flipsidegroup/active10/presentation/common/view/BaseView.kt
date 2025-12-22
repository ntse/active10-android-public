package com.flipsidegroup.active10.presentation.common.view


interface BaseView : LifecycleAwareView {

    fun showLoading()

    fun hideLoading()

    fun showAlert(throwable: Throwable?)

    fun showAlert(message: String?)
}