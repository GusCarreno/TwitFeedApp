package com.styba.twitfeeds.main

import com.styba.twitfeeds.common.BasePresenter

class MainPresenter : BasePresenter<MainContract.View>(), MainContract.Presenter {

    private var model: MainModel? = null

    override fun initModel(mainModel: MainModel) {
        model = mainModel
    }

    override fun getAds() {
        model?.getAds()
    }

    override fun onLoadAds() {
        view?.initPager()
    }

    override fun register() {
        model?.register()
    }
}