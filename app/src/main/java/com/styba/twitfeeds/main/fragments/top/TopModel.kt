package com.styba.twitfeeds.main.fragments.top

import android.text.TextUtils
import android.util.Log
import com.google.gson.reflect.TypeToken
import com.styba.twitfeeds.common.BaseModel
import com.styba.twitfeeds.common.Routes
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.models.LatestNew
import com.styba.twitfeeds.models.Twit
import com.styba.twitfeeds.models.Weather

class TopModel : BaseModel<TopContract.Presenter>(), TopContract.Model {

    private val tag = "TopModel"

    override fun initManager(twitFeedManager: TwitFeedManager?) {
        this.twitFeedManager = twitFeedManager
    }

    override fun getTopNews() {
        val hasMenu = if (twitFeedManager?.isExplore()!!)
            twitFeedManager?.hasExploreMenu()
        else
            twitFeedManager?.hasMenu()
        var params = if (hasMenu!!)
            "&lang=" + twitFeedManager?.getLanguage() + "&Section=LOCAL"
        else
            "&lang=" + twitFeedManager?.getLanguage() + "&Section=NATIONAL"
        params += if (twitFeedManager?.isExplore()!!)
            "&LocationId=" + twitFeedManager?.getExploreLocationId() +
                    "&Type=" + 0
        else
            "&LocationId=" + twitFeedManager?.getLocationId() +
                    "&Type=" + twitFeedManager?.getType()

        twitFeedManager?.post(
                Routes.TWITS,
                params,
                onSuccess = { result ->
                    val jsonResponse = parseResponse(result as String)
                    if (!TextUtils.isEmpty(jsonResponse)) {
                        val twits: List<Twit> = gSon.fromJson(jsonResponse, object : TypeToken<List<Twit>>() {}.type)
                        if (!twits.isEmpty()) {
                            presenter?.onLoadTwitsSuccess(twits[0])
                        } else
                            presenter?.onLoadTwitsFail()
                    } else
                        presenter?.onLoadTwitsFail()
                },
                onError = { result ->
                    Log.e(tag, "$result")
                    presenter?.onLoadTwitsFail()
                })
    }

    override fun getLastNews() {
        val params = "&lang=" + twitFeedManager?.getLanguage()
        twitFeedManager?.post(
                Routes.LATEST_NEWS,
                params,
                onSuccess = { result ->
                    val jsonResponse = parseResponse(result as String)
                    if (!TextUtils.isEmpty(jsonResponse)) {
                        val news: List<LatestNew> = gSon.fromJson(jsonResponse, object : TypeToken<List<LatestNew>>() {}.type)
                        if (!news.isEmpty()) {
                            val latestNew = news[0]
                            presenter?.onLoadLastNewsSuccess(latestNew)
                        } else
                            presenter?.onLoadLastNewsFail()
                    } else
                        presenter?.onLoadLastNewsFail()
                },
                onError = { result ->
                    Log.e(tag, "$result")
                    presenter?.onLoadLastNewsFail()
                })
    }

    override fun getWeather() {
        val params = "&LocationId=" + twitFeedManager?.getLocationId()
        twitFeedManager?.post(
                Routes.WEATHER,
                params,
                onSuccess = { result ->
                    val jsonResponse = parseResponse(result as String)
                    if (!TextUtils.isEmpty(jsonResponse)) {
                        val weatherList: List<Weather> = gSon.fromJson(jsonResponse, object : TypeToken<List<Weather>>() {}.type)
                        if (!weatherList.isEmpty()) {
                            val weather = weatherList[0]
                            presenter?.onLoadWeatherSuccess(weather)
                        } else
                            presenter?.onLoadWeatherFail()
                    } else
                        presenter?.onLoadWeatherFail()
                },
                onError = { result ->
                    Log.e(tag, "$result")
                    presenter?.onLoadWeatherFail()
                })
    }

}