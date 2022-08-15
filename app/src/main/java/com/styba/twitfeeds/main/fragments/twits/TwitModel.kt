package com.styba.twitfeeds.main.fragments.twits

import android.text.TextUtils
import android.util.Log
import com.google.gson.reflect.TypeToken
import com.styba.twitfeeds.common.BaseModel
import com.styba.twitfeeds.common.Routes
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.common.TypeAdapter
import com.styba.twitfeeds.models.Ad
import com.styba.twitfeeds.models.Twit
import com.styba.twitfeeds.models.TwitRow
import org.json.JSONArray

class TwitModel : BaseModel<TwitContract.Presenter>(), TwitContract.Model {

    private val tag = "TwitModel"
    private val twitListOrigin : MutableList<TwitRow> = arrayListOf()

    override fun initManager(twitFeedManager: TwitFeedManager?) {
        this.twitFeedManager = twitFeedManager
    }

    override fun initList(twitListOrigin: MutableList<TwitRow>) {
        this.twitListOrigin.addAll(twitListOrigin)
    }

    override fun getTwits(section: String?) {
        var params = "&lang=" + twitFeedManager?.getLanguage() +
                "&Section=" + section
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
                        twitListOrigin.clear()
                        val twits: List<Twit> = gSon.fromJson(jsonResponse, object : TypeToken<List<Twit>>() {}.type)
                        if (!twits.isEmpty()) {
                            val topTwit = TwitRow()
                            topTwit.viewType = TypeAdapter.TOP_TWIT_HOLDER
                            val doubleTwit = TwitRow()
                            doubleTwit.viewType = TypeAdapter.DOUBLE_TWIT_HOLDER
                            val tripleTwit1 = TwitRow()
                            tripleTwit1.viewType = TypeAdapter.TRIPLE_TWIT_HOLDER
                            val tripleTwit2 = TwitRow()
                            tripleTwit2.viewType = TypeAdapter.TRIPLE_TWIT_HOLDER
                            val singleTwitList : MutableList<TwitRow> = arrayListOf()
                            for (i in 0 until twits.size) {
                                when (i) {
                                    0 -> topTwit.twit = twits[i]
                                    in 1..2 -> {
                                        twits[i].viewType = TypeAdapter.TWIT_HOLDER
                                        doubleTwit.twitList.add(twits[i])
                                    }
                                    in 3..4 -> {
                                        twits[i].viewType = TypeAdapter.TWIT_HOLDER
                                        tripleTwit1.twitList.add(twits[i])
                                    }
                                    in 5..7 -> {
                                        twits[i].viewType = TypeAdapter.TWIT_HOLDER
                                        tripleTwit2.twitList.add(twits[i])
                                    }
                                    else -> {
                                        val twit = TwitRow()
                                        twit.viewType = TypeAdapter.SINGLE_TWIT_HOLDER
                                        twit.twit = twits[i]
                                        singleTwitList.add(twit)
                                    }
                                }
                            }
                            val ads: List<Ad> = gSon.fromJson(twitFeedManager?.getJsonAds(), object : TypeToken<List<Ad>>() {}.type)
                            val facebookAds = JSONArray(twitFeedManager?.getJsonFacebookAds())
                            if (!ads.isEmpty() && ads.size > 5) {
                                val twit = Twit(0, 1, "", "", "",
                                        "", "", "", "", "", "", "")
                                twit.viewType = TypeAdapter.AD_HOLDER
                                when (section) {
                                    "LOCAL" -> {
                                        twit.adUrlImage = ads[0].adUrl
                                        twit.adUrl = ads[0].url
                                        twit.adType = ads[0].advertising
                                        twit.facebookAd = facebookAds[0] as String
                                    }
                                    "NATIONAL" -> {
                                        twit.adUrlImage = ads[0].adUrl
                                        twit.adUrl = ads[0].url
                                        twit.adType = ads[0].advertising
                                        twit.facebookAd = facebookAds[1] as String
                                    }
                                    "INTERNATIONAL" -> {
                                        twit.adUrlImage = ads[1].adUrl
                                        twit.adUrl = ads[1].url
                                        twit.adType = ads[1].advertising
                                        twit.facebookAd = facebookAds[2] as String
                                    }
                                    "SPORTS" -> {
                                        twit.adUrlImage = ads[2].adUrl
                                        twit.adUrl = ads[2].url
                                        twit.adType = ads[2].advertising
                                        twit.facebookAd = facebookAds[3] as String
                                    }
                                    "ECONOMY_BUSINESS" -> {
                                        twit.adUrlImage = ads[3].adUrl
                                        twit.adUrl = ads[3].url
                                        twit.adType = ads[3].advertising
                                        twit.facebookAd = facebookAds[4] as String
                                    }
                                    else -> {
                                        twit.adUrlImage = ads[4].adUrl
                                        twit.adUrl = ads[4].url
                                        twit.adType = ads[4].advertising
                                        twit.facebookAd = facebookAds[5] as String
                                    }
                                }
                                tripleTwit1.twitList.add(twit)
                            }
                            twitListOrigin.add(topTwit)
                            twitListOrigin.add(doubleTwit)
                            twitListOrigin.add(tripleTwit1)
                            twitListOrigin.add(tripleTwit2)
                            val middlePos = (singleTwitList.size / 2)
                            if (!ads.isEmpty() && ads.size >= 20) {

                                val adTwitStart = TwitRow()
                                adTwitStart.viewType = TypeAdapter.AD_HOLDER
                                val adTwitMiddle = TwitRow()
                                adTwitMiddle.viewType = TypeAdapter.AD_HOLDER
                                val adTwitEnd = TwitRow()
                                adTwitEnd.viewType = TypeAdapter.AD_HOLDER

                                when (section) {
                                    "LOCAL" -> {
                                        adTwitStart.adUrlImage = ads[5].adUrl
                                        adTwitStart.adUrl = ads[5].url
                                        adTwitStart.adType = ads[5].advertising
                                        adTwitStart.facebookAd = facebookAds[6] as String
                                        singleTwitList.add(0, adTwitStart)
                                        adTwitMiddle.adUrlImage = ads[6].adUrl
                                        adTwitMiddle.adUrl = ads[6].url
                                        adTwitMiddle.adType = ads[6].advertising
                                        adTwitMiddle.facebookAd = facebookAds[7] as String
                                        singleTwitList.add(middlePos, adTwitMiddle)
                                        adTwitEnd.adUrlImage = ads[7].adUrl
                                        adTwitEnd.adUrl = ads[7].url
                                        adTwitEnd.adType = ads[7].advertising
                                        adTwitEnd.facebookAd = facebookAds[8] as String
                                        singleTwitList.add(singleTwitList.size, adTwitEnd)
                                    }
                                    "NATIONAL" -> {
                                        adTwitStart.adUrlImage = ads[5].adUrl
                                        adTwitStart.adUrl = ads[5].url
                                        adTwitStart.adType = ads[5].advertising
                                        adTwitStart.facebookAd = facebookAds[9] as String
                                        singleTwitList.add(0, adTwitStart)
                                        adTwitMiddle.adUrlImage = ads[6].adUrl
                                        adTwitMiddle.adUrl = ads[6].url
                                        adTwitMiddle.adType = ads[6].advertising
                                        adTwitMiddle.facebookAd = facebookAds[10] as String
                                        singleTwitList.add(middlePos, adTwitMiddle)
                                        adTwitEnd.adUrlImage = ads[7].adUrl
                                        adTwitEnd.adUrl = ads[7].url
                                        adTwitEnd.adType = ads[7].advertising
                                        adTwitEnd.facebookAd = facebookAds[11] as String
                                        singleTwitList.add(singleTwitList.size, adTwitEnd)
                                    }
                                    "INTERNATIONAL" -> {
                                        adTwitStart.adUrlImage = ads[8].adUrl
                                        adTwitStart.adUrl = ads[8].url
                                        adTwitStart.adType = ads[8].advertising
                                        adTwitStart.facebookAd = facebookAds[12] as String
                                        singleTwitList.add(0, adTwitStart)
                                        adTwitMiddle.adUrlImage = ads[9].adUrl
                                        adTwitMiddle.adUrl = ads[9].url
                                        adTwitMiddle.adType = ads[9].advertising
                                        adTwitMiddle.facebookAd = facebookAds[13] as String
                                        singleTwitList.add(middlePos, adTwitMiddle)
                                        adTwitEnd.adUrlImage = ads[10].adUrl
                                        adTwitEnd.adUrl = ads[10].url
                                        adTwitEnd.adType = ads[10].advertising
                                        adTwitEnd.facebookAd = facebookAds[14] as String
                                        singleTwitList.add(singleTwitList.size, adTwitEnd)
                                    }
                                    "SPORTS" -> {
                                        adTwitStart.adUrlImage = ads[11].adUrl
                                        adTwitStart.adUrl = ads[11].url
                                        adTwitStart.adType = ads[11].advertising
                                        adTwitStart.facebookAd = facebookAds[15] as String
                                        singleTwitList.add(0, adTwitStart)
                                        adTwitMiddle.adUrlImage = ads[12].adUrl
                                        adTwitMiddle.adUrl = ads[12].url
                                        adTwitMiddle.adType = ads[12].advertising
                                        adTwitMiddle.facebookAd = facebookAds[16] as String
                                        singleTwitList.add(middlePos, adTwitMiddle)
                                        adTwitEnd.adUrlImage = ads[13].adUrl
                                        adTwitEnd.adUrl = ads[13].url
                                        adTwitEnd.adType = ads[13].advertising
                                        adTwitEnd.facebookAd = facebookAds[17] as String
                                        singleTwitList.add(singleTwitList.size, adTwitEnd)
                                    }
                                    "ECONOMY_BUSINESS" -> {
                                        adTwitStart.adUrlImage = ads[14].adUrl
                                        adTwitStart.adUrl = ads[14].url
                                        adTwitStart.adType = ads[14].advertising
                                        adTwitStart.facebookAd = facebookAds[18] as String
                                        singleTwitList.add(0, adTwitStart)
                                        adTwitMiddle.adUrlImage = ads[15].adUrl
                                        adTwitMiddle.adUrl = ads[15].url
                                        adTwitMiddle.adType = ads[15].advertising
                                        adTwitMiddle.facebookAd = facebookAds[19] as String
                                        singleTwitList.add(middlePos, adTwitMiddle)
                                        adTwitEnd.adUrlImage = ads[16].adUrl
                                        adTwitEnd.adUrl = ads[16].url
                                        adTwitEnd.adType = ads[16].advertising
                                        adTwitEnd.facebookAd = facebookAds[20] as String
                                        singleTwitList.add(singleTwitList.size, adTwitEnd)
                                    }
                                    else -> {
                                        adTwitStart.adUrlImage = ads[17].adUrl
                                        adTwitStart.adUrl = ads[17].url
                                        adTwitStart.adType = ads[17].advertising
                                        adTwitStart.facebookAd = facebookAds[21] as String
                                        singleTwitList.add(0, adTwitStart)
                                        adTwitMiddle.adUrlImage = ads[18].adUrl
                                        adTwitMiddle.adUrl = ads[18].url
                                        adTwitMiddle.adType = ads[18].advertising
                                        adTwitMiddle.facebookAd = facebookAds[22] as String
                                        singleTwitList.add(middlePos, adTwitMiddle)
                                        adTwitEnd.adUrlImage = ads[19].adUrl
                                        adTwitEnd.adUrl = ads[19].url
                                        adTwitEnd.adType = ads[19].advertising
                                        adTwitEnd.facebookAd = facebookAds[23] as String
                                        singleTwitList.add(singleTwitList.size, adTwitEnd)
                                    }
                                }

                            }
                            twitListOrigin.addAll(singleTwitList)
                            presenter?.onLoadTwitsSuccess()
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

    override fun getTwitRow(position: Int): TwitRow? {
        return twitListOrigin[position]
    }

    override fun getTwitsCount(): Int {
        return if (twitListOrigin.isEmpty())
            0
        else
            twitListOrigin.size
    }

}