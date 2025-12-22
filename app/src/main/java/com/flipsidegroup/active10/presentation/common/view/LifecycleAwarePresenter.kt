package com.flipsidegroup.active10.presentation.common.view


interface LifecycleAwarePresenter<V : LifecycleAwareView> {

    fun bind(view: V)

    fun unbind()
}