package com.styba.twitfeeds.twits

import com.styba.twitfeeds.models.ReaderNew

interface TwitsContract {
    interface View {
        fun showLoading()
        fun hideLoading()
        fun initWebView(readerNew: ReaderNew?, isRealUrl: Boolean)
    }

    interface Presenter {
        fun initModel(model: TwitsModel)
        fun getReaderMode(url: String)
        fun onReaderModeSuccess(readerNew: ReaderNew, isRealUrl: Boolean)
        fun onReaderModeFail()
    }

    interface Model {
        fun getReaderMode(url: String)
    }
}