package com.styba.twitfeeds.common

abstract class BasePresenter<V> {

    protected var view: V? = null

    /**
     * Call this method in Activity#onCreate
     * or Fragment#onAttach(Context) to attach the MVP View object
     */
    open fun attachView(view: V) {
        this.view = view
    }

    /**
     * Call this method in Activity#onDestroy()
     * or Fragment#onDetach() to detach the MVP View object
     */
    open fun detachView() {
        view = null
    }

    /**
     * Call this method in Activity#onDestroy()
     */
    open fun cancelTask() {

    }

    /**
     * Check if the view is attached.
     * This checking is only necessary when returning from an asynchronous call
     */
    protected val isViewAttached: Boolean
        get() = view != null

}