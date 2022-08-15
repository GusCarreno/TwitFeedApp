package com.styba.twitfeeds.main

import android.util.Log
import com.styba.twitfeeds.common.BaseModel
import com.styba.twitfeeds.common.Routes
import com.styba.twitfeeds.common.TwitFeedManager
import org.json.JSONObject

class MainModel : BaseModel<MainContract.Presenter>(), MainContract.Model  {
    private val tag = "TwitModel"

    override fun initManager(twitFeedManager: TwitFeedManager?) {
        this.twitFeedManager = twitFeedManager
    }

    override fun getAds() {
        val params = "&LocationId=" + twitFeedManager?.getLocationId()
        twitFeedManager?.post(
                Routes.ADVERTISING,
                params,
                onSuccess = { result ->
                    val jsonResponse = parseResponse(result as String)
                    twitFeedManager?.setJsonAds(jsonResponse)
                    presenter?.onLoadAds()
                },
                onError = { result ->
                    Log.e(tag, "$result")
                    presenter?.onLoadAds()
                })
    }

    override fun register() {
        val jsonSource = JSONObject()
        jsonSource.put("LocationId", twitFeedManager?.getLocationId())
        jsonSource.put("registrationId", twitFeedManager?.getFireBaseToken())
        jsonSource.put("type", 1)
        jsonSource.put("lang", twitFeedManager?.getLanguage())
        jsonSource.put("oldlocation", 0)
        jsonSource.put("DeviceType", "ANDROID")
        jsonSource.put("tokenId", twitFeedManager?.getTokenId())
        val params = "&json=" + jsonSource.toString()
        twitFeedManager?.post(
                Routes.REGISTRATION,
                params,
                onSuccess = { result ->
                    Log.e(tag, "$result")
                    twitFeedManager?.setIsFireBaseTokenChange(false)
                },
                onError = { result ->
                    Log.e(tag, "$result")
                })
    }
}