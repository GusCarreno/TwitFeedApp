package com.styba.twitfeeds.main.fragments.top

import com.styba.twitfeeds.common.BasePresenter
import com.styba.twitfeeds.models.LatestNew
import com.styba.twitfeeds.models.Twit
import com.styba.twitfeeds.models.Weather

class TopPresenter : BasePresenter<TopContract.View>(), TopContract.Presenter {

    private var model: TopModel? = null

    override fun initModel(topModel: TopModel) {
        model = topModel
    }

    override fun getTopNews() {
        view?.showLoading()
        model?.getTopNews()
    }

    override fun onLoadTwitsSuccess(twit: Twit) {
        view?.initBanner(twit)
        model?.getLastNews()
    }

    override fun onLoadTwitsFail() {
        model?.getLastNews()
    }

    override fun onLoadLastNewsSuccess(latestNew: LatestNew) {
        view?.initLatestNews(latestNew)
        model?.getWeather()
    }

    override fun onLoadLastNewsFail() {
        view?.latestNewsNotAvailable()
        model?.getWeather()
    }

    override fun onLoadWeatherSuccess(weather: Weather) {
        view?.hideLoading()
        view?.initWeather(weather)
    }

    override fun onLoadWeatherFail() {
        view?.hideLoading()
        view?.weatherNotAvailable()
    }

    override fun detachView() {
        super.detachView()
        model?.detachPresenter()
    }

}