package com.flipsidegroup.active10.presentation.common.presenter

import com.flipsidegroup.active10.presentation.common.view.LifecycleAwarePresenter
import com.flipsidegroup.active10.presentation.common.view.LifecycleAwareView
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber


abstract class BasePresenter<V : LifecycleAwareView> : LifecycleAwarePresenter<V> {

    protected var view: V? = null
    private var disposables: CompositeDisposable? = null

    /**
     * The name of the method is given by the fact that it's ont only a setter for view,
     * it also inits other components.
     * @param view View implementation
     */
    override fun bind(view: V) {
        this.view = view
        initCompositeDisposable()
    }

    private fun initCompositeDisposable() {
        this.disposables = CompositeDisposable()
    }

    protected fun Disposable.addToDisposables() {
        disposables?.add(this)
    }

    /**
     * The name of the method is given by the fact that beside destroying the view, it also disposes
     * the observers.
     */
    override fun unbind() {
        view = null
        disposeObservers()
    }

    private fun disposeObservers() {
        disposables?.clear()
    }

    /**
     * An observer that disposes itself on [unbind]
     * @param D
     */
    protected open inner class SelfDisposingObserver<D>(
        private var disposeOnUnbind: Boolean = true,
        private inline val onErrorAction: ((Throwable) -> Unit)? = null,
        private inline val onSuccessAction: ((D) -> Unit)? = null
    ) : Observer<D> {

        /**
         * This method handles the disposing mechanism for observers.
         * If overridden, do not forget to call .super
         */
        override fun onSubscribe(d: Disposable) {
            if (disposeOnUnbind) {
                disposables?.add(d)
            }
        }

        /**
         * This method can be overridden is necessary.
         */
        override fun onComplete() {

        }

        override fun onNext(t: D & Any) {
            onSuccessAction?.invoke(t)
        }

        override fun onError(e: Throwable) {
            Timber.d(e)
            onErrorAction?.invoke(e)
        }
    }
}