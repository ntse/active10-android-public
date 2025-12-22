package com.flipsidegroup.active10.presentation.common.fragments

import android.content.Context
import android.view.ViewGroup
import com.flipsidegroup.active10.presentation.common.FragmentToolbar
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwareView
import com.flipsidegroup.active10.presentation.common.widgets.ContentLoadingProgressLayout
import com.flipsidegroup.active10.utils.AlertHelper
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


abstract class BaseFragment<V : LifecycleAwareView> : FragmentToolbar(), BaseView {

    private val compositeDisposable = CompositeDisposable()

    private lateinit var loadingWidget: ContentLoadingProgressLayout

    override fun onAttach(context: Context) {
        performInjection()
        super.onAttach(context)
        attachPresenter()
        setUpLoadingWidget()
    }

    override fun onStop() {
        super.onStop()

        compositeDisposable.clear()
    }

    override fun onDetach() {
        super.onDetach()
        getPresenter()?.unbind()
    }

    override fun showLoading() {
        loadingWidget.show()
    }

    override fun hideLoading() {
        loadingWidget.hide()
    }

    override fun showAlert(throwable: Throwable?) {
        activity?.apply {
            AlertHelper.showErrorToast(throwable?.message)
        }
    }

    override fun showAlert(message: String?) {
        activity?.apply {
            AlertHelper.showErrorToast(message)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun attachPresenter() {
        getPresenter()?.bind(this as V)
    }

    private fun performInjection() {
        AndroidSupportInjection.inject(this)
    }

    protected abstract fun getPresenter(): LifecycleAwarePresenter<V>?

    private fun setUpLoadingWidget() {
        activity?.let {
            loadingWidget = ContentLoadingProgressLayout(it)
            (it.window?.decorView as ViewGroup).addView(loadingWidget)
        }
    }

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun clearDisposables() {
        compositeDisposable.clear()
    }
}