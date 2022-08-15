package com.styba.twitfeeds.twits

import com.styba.twitfeeds.common.BasePresenter
import com.styba.twitfeeds.models.ReaderNew

class TwitsPresenter : BasePresenter<TwitsContract.View>(), TwitsContract.Presenter {

    private var model: TwitsModel? = null

    override fun initModel(model: TwitsModel) {
        this.model = model
    }

    override fun getReaderMode(url: String) {
        model?.getReaderMode(url)
    }

    override fun onReaderModeSuccess(readerNew: ReaderNew, isRealUrl: Boolean) {
        view?.initWebView(readerNew, isRealUrl)
    }

    override fun onReaderModeFail() {
        view?.initWebView(null, false)
    }

    override fun detachView() {
        super.detachView()
        model?.detachPresenter()
    }

}