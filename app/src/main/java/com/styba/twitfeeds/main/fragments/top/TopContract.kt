package com.styba.twitfeeds.main.fragments.top

import com.styba.twitfeeds.models.LatestNew
import com.styba.twitfeeds.models.Twit
import com.styba.twitfeeds.models.Weather

interface TopContract {
    interface View {
        fun initBanner(twit: Twit)
        fun initLatestNews(latestNew: LatestNew)
        fun latestNewsNotAvailable()
        fun initWeather(weather: Weather)
        fun weatherNotAvailable()
        fun showLoading()
        fun hideLoading()
    }

    interface Presenter {
        fun initModel(topModel: TopModel)
        fun getTopNews()
        fun onLoadTwitsSuccess(twit: Twit)
        fun onLoadTwitsFail()
        fun onLoadLastNewsSuccess(latestNew: LatestNew)
        fun onLoadLastNewsFail()
        fun onLoadWeatherSuccess(weather: Weather)
        fun onLoadWeatherFail()
    }

    interface Model {
        fun getTopNews()
        fun getLastNews()
        fun getWeather()
    }
}