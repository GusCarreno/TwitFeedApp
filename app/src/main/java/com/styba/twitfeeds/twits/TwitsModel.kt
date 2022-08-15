package com.styba.twitfeeds.twits

import android.text.TextUtils
import android.util.Log
import com.google.gson.Gson
import com.styba.twitfeeds.common.BaseModel
import com.styba.twitfeeds.common.Routes
import com.styba.twitfeeds.common.TwitFeedManager
import com.styba.twitfeeds.models.ReaderNew
import org.json.JSONObject

class TwitsModel : BaseModel<TwitsContract.Presenter>(), TwitsContract.Model {

    private val tag = "TwitsModel"

    override fun initManager(twitFeedManager: TwitFeedManager?) {
        this.twitFeedManager = twitFeedManager
    }

    override fun getReaderMode(url: String) {
        twitFeedManager?.get(
                Routes.READER_MODE + url,
                onSuccess = { result ->
                    val jsonResponse = result as String
                    if (!TextUtils.isEmpty(jsonResponse)) {
                        //val jsonContent = JSONObject(jsonResponse)
                        val readerNew = Gson().fromJson(jsonResponse, ReaderNew::class.java)
                        /*val html = if (jsonContent.has("content"))
                            jsonContent.optString("content")
                        else
                            ""
                        if (!TextUtils.isEmpty(html))
                            presenter?.onReaderModeSuccess(html, true)
                        else
                            presenter?.onReaderModeFail()*/
                        presenter?.onReaderModeSuccess(readerNew, true)
                    } else
                        presenter?.onReaderModeFail()
                },
                onError = { result ->
                    Log.e(tag, "$result")
                    presenter?.onReaderModeFail()
                }
        )
    }

}