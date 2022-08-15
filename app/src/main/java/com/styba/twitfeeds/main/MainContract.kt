package com.styba.twitfeeds.main

interface MainContract {
    interface View {
        fun initPager()
    }

    interface Presenter {
        fun initModel(mainModel: MainModel)
        fun register()
        fun getAds()
        fun onLoadAds()
    }

    interface Model {
        fun getAds()
        fun register()
    }
}