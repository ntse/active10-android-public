package com.flipsidegroup.active10.presentation.common.dialogfragments

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.flipsidegroup.active10.presentation.common.view.BaseView
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwareView
import com.flipsidegroup.active10.presentation.common.widgets.ContentLoadingProgressLayout
import com.flipsidegroup.active10.utils.AlertHelper
import dagger.android.support.AndroidSupportInjection


abstract class BaseDialogFragment<V : LifecycleAwareView> : DialogFragment(), BaseView {

    private lateinit var loadingWidget: ContentLoadingProgressLayout

    override fun onAttach(context: Context) {
        performInjection()
        super.onAttach(context)
        attachPresenter()
        setUpLoadingWidget()
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

}